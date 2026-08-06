package com.ADIB.FileSystem.Business.service.file;

import com.ADIB.FileSystem.Business.Enum.FILE_STATUS;
import com.ADIB.FileSystem.Business.Enum.NOTIFICATIONTYPE;
import com.ADIB.FileSystem.Business.Exceptions.FileExpiredException;
import com.ADIB.FileSystem.Business.Model.*;
import com.ADIB.FileSystem.Business.dto.response.SecurityLevelResponse;
import com.ADIB.FileSystem.Business.service.Permissions.PagePermissionService;
import com.ADIB.FileSystem.DataAccess.repository.*;
import com.ADIB.FileSystem.Business.dto.request.BulkFileUploadRequest;
import com.ADIB.FileSystem.Business.dto.request.FileRequest;
import com.ADIB.FileSystem.Business.dto.request.FileSearchRequest;
import com.ADIB.FileSystem.Business.dto.request.UpdateFileStatusRequest;
import com.ADIB.FileSystem.Business.dto.response.FileResponse;
import com.ADIB.FileSystem.Business.event.FileForwardedEvent;
import com.ADIB.FileSystem.Business.event.FileUploadedEvent;
import com.ADIB.FileSystem.Business.Exceptions.ResourceNotFoundException;
import com.ADIB.FileSystem.config.FileStorageProperties;
import com.ADIB.FileSystem.mapper.FileMapper;
import com.ADIB.FileSystem.security.CurrentUserProvider;
import com.ADIB.FileSystem.DataAccess.specification.FileSpecification;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
public class FileService {

   private final FileStorageProperties  storageProperties;

    private final FileRepo fileRepository;
    private final FileTypeRepo fileTypeRepo;
    private final FileForwardRepo fileForwardRepo;
    private final DepartmentRepo departmentRepository;
    private final FileDepartmentApprovalRepo fileDepartmentApprovalRepo;
    private final UserRepo userRepo;
    private final CurrentUserProvider currentUserProvider;
    private final FileMapper fileMapper;
    private final FileEncryptionService fileEncryptionService;
    private final ApplicationEventPublisher eventPublisher;
    private final PagePermissionService pagePermissionService;


    @PostConstruct
    void ensureStorageDirectoriesExist() throws IOException {
        Files.createDirectories(storageProperties.uploadPath());
        Files.createDirectories(storageProperties.trashPath());
    }

    public FileResponse uploadFile(FileRequest request) throws IOException {

        Set<Department> departments = new HashSet<>(departmentRepository.findAllById(request.getDepartment_ids()));
        if (departments.isEmpty()) {
            throw new ResourceNotFoundException("No valid departments found");
        }

        FileType fileType = fileTypeRepo.findById(request.getFileType_id())
                .orElseThrow(() -> new ResourceNotFoundException("File type not found"));


       User uploader = currentUserProvider.getCurrentUser();
       FileResponse response = storeSingleFile(
               request.getFile().getOriginalFilename(),
               request.getFile().getBytes(),
               request.getFile().getSize(),
               departments,
               fileType,
               uploader
       );
       return response;
    }

    public List<FileResponse> uploadFilesBulk(BulkFileUploadRequest request) throws IOException {

        List<MultipartFile> files = request.getFiles();
        List<Long> fileTypeIds = request.getFileTypeIds();
        List<Long> departmentIds = request.getDepartmentIds();

        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException("No files provided");
        }
        if (fileTypeIds == null || fileTypeIds.size() != files.size()) {
            throw new IllegalArgumentException("Each file must have a matching file type");
        }
        if (departmentIds == null || departmentIds.isEmpty()) {
            throw new IllegalArgumentException("At least one department must be selected");
        }

        Set<Department> departments = new LinkedHashSet<>();
        for (Long deptId : departmentIds) {
            departments.add(departmentRepository.findById(deptId).orElseThrow(() -> new ResourceNotFoundException("Department not found: " + deptId)));
        }

        User uploader = currentUserProvider.getCurrentUser();

        List<FileResponse> results = new ArrayList<>();

        for (int i = 0; i < files.size(); i++) {
            MultipartFile multipartFile = files.get(i);
            Long fileTypeId = fileTypeIds.get(i);

            FileType fileType = fileTypeRepo.findById(fileTypeId)
                    .orElseThrow(() -> new ResourceNotFoundException("File type not found: " + fileTypeId));

            results.add(storeSingleFile(
                    multipartFile.getOriginalFilename(),
                    multipartFile.getBytes(),
                    multipartFile.getSize(),
                    departments,
                    fileType,
                    uploader
            ));
        }
        return results;
    }


    private FileResponse storeSingleFile(
            String originalFileName,
            byte[] fileBytes,
            long size,
            Set<Department> departments,
            FileType fileType,
            User uploader
    ) throws IOException {

        String extension =extractExtension(originalFileName);
        String storedFileName = UUID.randomUUID() + "_" + originalFileName;
        Path filePath = storageProperties.uploadPath().resolve(storedFileName);

        writeEncrypted(filePath,fileBytes);
        filePath.toFile().setReadOnly();

        File file = File.builder()
                .name(originalFileName)
                .path(filePath.toString())
                .size(size)
                .extension(extension)
                .status(FILE_STATUS.PENDING)
                .departments(departments)
                .fileType(fileType)
                .isDeleted(false)
                .build();

        File savedFile = fileRepository.save(file);

        List<FileDepartmentApproval> approvals = departments.stream()
                        .map(dept -> FileDepartmentApproval.builder()
                                .file(savedFile)
                                .department(dept)
                                .status(FILE_STATUS.PENDING)
                                .build())
                                .toList();
        fileDepartmentApprovalRepo.saveAll(approvals);

        eventPublisher.publishEvent(new FileUploadedEvent(this, savedFile, uploader));

        return fileMapper.mapToResponse(savedFile);
    }

    private void writeEncrypted(Path filePath, byte[] rawBytes) throws IOException {
        byte[] encryptedBytes;
        try {
            encryptedBytes = fileEncryptionService.encrypt(rawBytes);
        }catch (Exception e) {
            throw new IOException("Failed to encrypt and save file", e);
        }
        Files.write(filePath, encryptedBytes);
    }


    private String extractExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }


    public void deleteFile(Long fileId) throws IOException {
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new ResourceNotFoundException("File not found"));

        ensureNotExpired(file);

        Path filePath = Paths.get(file.getPath());
        Path targetPath = storageProperties.trashPath().resolve(filePath.getFileName());
        Files.move(filePath, targetPath);
        file.setPath(targetPath.toString());
        file.setIsDeleted(true);
        file.setStatus(FILE_STATUS.REJECTED);
        fileRepository.save(file);
    }

    public Page<FileResponse> listFiles(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortBy != null && !sortBy.isBlank()
                ? Sort.by("desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC, mapSortField(sortBy))
                : Sort.unsorted();
        Pageable pageable = PageRequest.of(page, size, sort);

        if (pagePermissionService.hasFullReadAccess("Files")) {
            return fileRepository.findByIsDeletedFalse(pageable).map(fileMapper::mapToResponse);
        }

        Long deptId = currentUserProvider.getCurrentUser().getDepartment() != null
                ? currentUserProvider.getCurrentUser().getDepartment().getId()
                : null;

        if (deptId == null) {
            return Page.empty(pageable);
        }

        return fileRepository.findByDepartmentIdAndNotDeleted(deptId, pageable).map(fileMapper::mapToResponse);
    }
    public Page<FileResponse> listAllDeletedFiles(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        if (pagePermissionService.hasFullReadAccess("Files")) {
            return fileRepository.findDeletedFiles(pageable).map(fileMapper::mapToResponse);
        }

        Long deptId = currentUserProvider.getCurrentUser().getDepartment() != null
                ? currentUserProvider.getCurrentUser().getDepartment().getId()
                : null;
        if (deptId == null) return Page.empty(pageable);

        return fileRepository.findDeletedByDepartmentId(deptId, pageable).map(fileMapper::mapToResponse);
    }

    public Page<FileResponse> listFilesByUser(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return fileRepository.findByCreatedByIdAndIsDeletedFalse(userId, pageable)
                .map(fileMapper::mapToResponse);
    }

    public Page<FileResponse> listFilesByDepartment(Long departmentId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        return fileRepository.findByDepartmentId(departmentId, pageable).map(fileMapper::mapToResponse);
    }

    public Page<FileResponse> listMyFiles(int page, int size) {
        Long userId = currentUserProvider.getCurrentUser().getId();
        Pageable pageable = PageRequest.of(page, size);
        return fileRepository.findByCreatedByIdAndIsDeletedFalse(userId, pageable)
                .map(fileMapper::mapToResponse);
    }

    public FileResponse getFileData(Long fileId) throws IOException {
        File file = fileRepository.findById(fileId).orElseThrow(() -> new ResourceNotFoundException("File not found"));

        if (Boolean.TRUE.equals(file.getExpired())) {
            throw new FileExpiredException("File has expired.");
        }
        return fileMapper.mapToResponse(file);
    }

    public ResponseEntity<ByteArrayResource> downloadFile(Long fileId) throws IOException {
        File file = fileRepository.findById(fileId).orElseThrow(() -> new ResourceNotFoundException("File not found"));
        ensureNotExpired(file);

        Path filePath = Paths.get(file.getPath());
        byte[] encryptedBytes = Files.readAllBytes(filePath);
        ByteArrayResource byteArrayResource;
        try {
            byte[] originalBytes = fileEncryptionService.decrypt(encryptedBytes);
            byteArrayResource = new ByteArrayResource(originalBytes);
        } catch (Exception e) {
            throw new IOException("Failed to decrypt and save file", e);
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(byteArrayResource);
    }

    public ResponseEntity<ByteArrayResource> downloadFilesBulk(List<Long> fileIds) throws IOException {
        List<File> files = fileRepository.findAllById(fileIds);
        if (files.isEmpty()) {
            throw new ResourceNotFoundException("Files not found");
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Set<String> usedNames = new HashSet<>();

        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            for (File file : files) {
                Path filePath = Paths.get(file.getPath());
                byte[] encryptedBytes = Files.readAllBytes(filePath);
                byte[] originalBytes;
                try {
                    originalBytes = fileEncryptionService.decrypt(encryptedBytes);
                } catch (Exception e) {
                    throw new IOException("Failed to decrypt and save file" + file.getName(), e);
                }
                String entryName = makeUniqueEntryName(file.getName(), usedNames);

                zos.putNextEntry(new ZipEntry(entryName));
                zos.write(originalBytes);
                zos.closeEntry();
            }
        }

        ByteArrayResource resource = new ByteArrayResource(baos.toByteArray());

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"files.zip\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    public FileResponse updateFileStatus(Long fileId, UpdateFileStatusRequest request) {
        File file = fileRepository.findById(fileId).orElseThrow(() -> new ResourceNotFoundException("File not found"));
        ensureNotExpired(file);
        User currentUser = currentUserProvider.getCurrentUser();
        Department managerDepartment =currentUser.getDepartment();

        if(managerDepartment == null) {
            throw new IllegalStateException("USer has no department; cannot approve files");
        }

        FileDepartmentApproval approval = fileDepartmentApprovalRepo.findByFileIdAndDepartmentId(fileId,managerDepartment.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("This file is not routed to your department"));

        approval.setStatus(request.getStatus());
        approval.setManager(currentUser);
        approval.setDecidedAt(java.time.LocalDateTime.now());
        fileDepartmentApprovalRepo.save(approval);

        recomputeFileStatus(file);
//        file.setStatus(request.getStatus());
        return fileMapper.mapToResponse(fileRepository.save(file));
    }

    private void recomputeFileStatus(File file) {
        List<FileDepartmentApproval> approvals = fileDepartmentApprovalRepo.findByFileId(file.getId());

        boolean anyRejected = approvals.stream()
                .anyMatch(a -> a.getStatus() == FILE_STATUS.REJECTED);
        boolean allApproved = approvals.stream()
                .allMatch(a -> a.getStatus() == FILE_STATUS.APPROVED);

        if(anyRejected){
            file.setStatus(FILE_STATUS.REJECTED);
        }else if (allApproved && !approvals.isEmpty()){
            file.setStatus(FILE_STATUS.APPROVED);
        }else {
            file.setStatus(FILE_STATUS.PENDING);
        }
    }


    private String makeUniqueEntryName(String originalName, Set<String> usedNames) {
        if (usedNames.add(originalName)) {
            return originalName;
        }

        String baseName = originalName;
        String extension = "";
        int dotIndex = originalName.lastIndexOf('.');
        if (dotIndex > 0) {
            baseName = originalName.substring(0, dotIndex);
            extension = originalName.substring(dotIndex);
        }

        int counter = 1;
        String candidate;
        do {
            candidate = baseName + " (" + counter + ")" + extension;
            counter++;
        } while (!usedNames.add(candidate));

        return candidate;
    }

    public void notifyDepartmentsOnUpload(File file, User uploader) {
        Set<Department> departments = file.getDepartments();
        if (departments == null || departments.isEmpty()) {
            return;
        }

        List<User> recipients = userRepo.findByDepartmentInAndIdNot(departments, uploader.getId());

        String message = "New file uploaded: " + file.getName();
        for (User recipient : recipients) {
            FileForward notification = FileForward.builder()
                    .file(file)
                    .sender(uploader)
                    .recipient(recipient)
                    .message(message)
                    .type(NOTIFICATIONTYPE.DEPARTMENT_UPLOAD)
                    .isRead(false)
                    .build();
            FileForward saved = fileForwardRepo.save(notification);
            eventPublisher.publishEvent(new FileForwardedEvent(this, saved));
        }
    }

    public Page<FileResponse> search(FileSearchRequest request) {
        enforceDepartmentScope(request);

        int pageNumber = request.getPage() != null ? request.getPage() : 0;
        int pageSize = request.getSize() != null ? request.getSize() : 10;

        Sort sort = Sort.unsorted();
        if(request.getSortBy() != null &&  !request.getSortBy().isBlank()) {
            Sort.Direction direction = "desc".equalsIgnoreCase(request.getSortDir()) ? Sort.Direction.DESC : Sort.Direction.ASC;
            sort = Sort.by(direction,  mapSortField(request.getSortBy()));
        }
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        Specification<File> spec = FileSpecification.search(request);
        Page<File> results = fileRepository.findAll(spec, pageable);

        return results.map(fileMapper::mapToResponse);
    }

    public ResponseEntity<ByteArrayResource> exportSearchResults(FileSearchRequest request) throws IOException {
        enforceDepartmentScope(request);

        Specification<File> spec = FileSpecification.search(request);
        List<File> files = fileRepository.findAll(spec);

        StringBuilder csv = new StringBuilder();
        csv.append("ID,Name,Extension,Departments,Status,FileType,Size,Owner,CreatedDate,ModifiedDate\n");
        for (File f : files) {
            csv.append(f.getId()).append(",")
                    .append(escapeCsv(f.getName())).append(",")
                    .append(f.getExtension()).append(",")
                    .append(escapeCsv(getDepartmentNames(f))).append(",")
                    .append(f.getStatus()).append(",")
                    .append(f.getFileType() != null ? f.getFileType().getName() : "").append(",")
                    .append(f.getSize()).append(",")
                    .append(f.getCreatedBy() != null ? escapeCsv(f.getCreatedBy().getName()) : "").append(",")
                    .append(f.getCreatedAt()).append(",")
                    .append(f.getUpdatedAt()).append(",")
                    .append("\n");
        }

        byte[] bytes = csv.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"search-results.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(new ByteArrayResource(bytes));
    }

    public List<SecurityLevelResponse> getAllowedSecurityLevels(List<Long> departmentIds){
        if(departmentIds == null || departmentIds.isEmpty()){
            throw new IllegalArgumentException("At least one department must be selected");
        }

        List<Department> departments = departmentRepository.findAllById(departmentIds);
        if(departments.size() != departmentIds.size()){
            throw new ResourceNotFoundException("One or more departments is not found");
        }

        return  departments.stream()
                .map(Department::getSecurityLevels)
                .filter(Objects::nonNull)
                .distinct()
                .map(sl -> new SecurityLevelResponse(sl.getId(), sl.getName()))
                .toList();
    }

    private String mapSortField(String field) {
        return switch (field) {
            case "ownerName" -> "createdBy.name";
            case "createdDate" -> "createdAt";
            case "modifiedDate" -> "updatedAt";
            default -> field;
        };
    }

    private String getDepartmentNames(File f) {
        if(f.getDepartments() == null) return "";
        return  f.getDepartments().stream().map(Department::getName)
                .collect(java.util.stream.Collectors.joining("; "));
    }

    private String escapeCsv(String value) {
        if(value == null) return "";
        if(value.contains(",") || value.contains("\"") || value.contains("\n")){
            return "\"" + value.replace("\"","\"\"")+ "\"";
        }
        return value;
    }

    private void enforceDepartmentScope(FileSearchRequest request) {
        if (pagePermissionService.hasFullReadAccess("Files")) {
            return;
        }
        Department dept = currentUserProvider.getCurrentUser().getDepartment();
        request.setDepartments(dept != null ? List.of(dept.getName()) : List.of("__NO_DEPARTMENT__"));
    }

    private void ensureNotExpired(File file) {
        if (Boolean.TRUE.equals(file.getExpired())) {
            throw new FileExpiredException("File has expired.");
        }
    }
}