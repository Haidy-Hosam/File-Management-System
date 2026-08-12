package com.ADIB.FileSystem.Business.service.file;

import com.ADIB.FileSystem.Business.Enum.FILE_STATUS;
import com.ADIB.FileSystem.Business.Enum.NOTIFICATIONTYPE;
import com.ADIB.FileSystem.Business.Exceptions.FileExpiredException;
import com.ADIB.FileSystem.Business.Model.*;
import com.ADIB.FileSystem.Business.dto.response.SecurityLevelResponse;
import com.ADIB.FileSystem.Business.service.Permissions.PagePermissionService;
import com.ADIB.FileSystem.Business.service.file.Notifications.Notifiy;
import com.ADIB.FileSystem.Business.service.file.helpers.FileHelpers;
import com.ADIB.FileSystem.DataAccess.repository.*;
import com.ADIB.FileSystem.Business.dto.request.BulkFileUploadRequest;
import com.ADIB.FileSystem.Business.dto.request.FileRequest;
import com.ADIB.FileSystem.Business.dto.request.FileSearchRequest;
import com.ADIB.FileSystem.Business.dto.request.UpdateFileStatusRequest;
import com.ADIB.FileSystem.Business.dto.response.FileResponse;
import com.ADIB.FileSystem.Business.dto.response.FileApprovalStepsResponse;
import com.ADIB.FileSystem.Business.event.FileForwardedEvent;
import com.ADIB.FileSystem.Business.event.FileUploadedEvent;
import com.ADIB.FileSystem.Business.Exceptions.ResourceNotFoundException;
import com.ADIB.FileSystem.config.FileStorageProperties;
import com.ADIB.FileSystem.mapper.FileApprovalStepsMapper;
import com.ADIB.FileSystem.mapper.FileMapper;
import com.ADIB.FileSystem.mapper.SecurityLevelMapper;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    private final DepartmentRepo departmentRepository;
    private final FileDepartmentApprovalRepo fileDepartmentApprovalRepo;
    private final SecurityLevelRepo securityLevelRepo;
    private final UserRepo userRepo;
    private final CurrentUserProvider currentUserProvider;
    private final FileMapper fileMapper;
    private final SecurityLevelMapper  securityLevelMapper;

    private final FileApprovalStepsMapper fileApprovalStepsMapper;
    private final FileEncryptionService fileEncryptionService;
    private final ApplicationEventPublisher eventPublisher;
    private final PagePermissionService pagePermissionService;
    private final FileHelpers fileHelpers;
    private final Notifiy notifiy;


    @PostConstruct
    void ensureStorageDirectoriesExist() throws IOException {
        Files.createDirectories(storageProperties.uploadPath());
        Files.createDirectories(storageProperties.trashPath());
    }

//    public FileResponse uploadFile(FileRequest request) throws IOException {
//
//        Set<Department> departments = new HashSet<>(departmentRepository.findAllById(request.getDepartment_ids()));
//        if (departments.isEmpty()) {
//            throw new ResourceNotFoundException("No valid departments found");
//        }
//
//        FileType fileType = fileTypeRepo.findById(request.getFileType_id())
//                .orElseThrow(() -> new ResourceNotFoundException("File type not found"));
//
//
//        User uploader = currentUserProvider.getCurrentUser();
//        FileResponse response = storeSingleFile(
//                request.getFile().getOriginalFilename(),
//                request.getFile().getBytes(),
//                request.getFile().getSize(),
//                departments,
//                fileType,
//                uploader
//        );
//        return response;
//    }

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

        String extension =fileHelpers.extractExtension(originalFileName);
        String storedFileName = UUID.randomUUID() + "_" + originalFileName;
        Path filePath = storageProperties.uploadPath().resolve(storedFileName);

        fileHelpers.writeEncrypted(filePath,fileBytes);


        List<Department> orderedDepts = departments.stream()
                .sorted(Comparator.comparing(d -> d.getSecurityLevels().getId()))
                .toList();

        Department firstDept =  orderedDepts.get(0);

        File file = File.builder()
                .name(originalFileName)
                .path(filePath.toString())
                .size(size)
                .extension(extension)
                .status(FILE_STATUS.PENDING)
                .departments(new HashSet<>(Set.of(firstDept)))
                .fileType(fileType)
                .isDeleted(false)
                .securityLevel(firstDept.getSecurityLevels())
                .currentApprovalOrder(orderedDepts.get(0).getSecurityLevels().getId())
                .build();

        File savedFile = fileRepository.save(file);

        List<FileDepartmentApproval> approvals = orderedDepts.stream()
                .map(dept -> FileDepartmentApproval.builder()
                        .file(savedFile)
                        .department(dept)
                        .manager(userRepo.findDepartmentManager(dept.getId()).orElseThrow(() -> new ResourceNotFoundException("Department manager not found: " + dept.getId())))
                        .status(FILE_STATUS.PENDING)
                        .currentApprovalOrder(dept.getSecurityLevels().getId())
                        .build())
                .toList();
        fileDepartmentApprovalRepo.saveAll(approvals);

        eventPublisher.publishEvent(new FileUploadedEvent(this, savedFile, uploader));
        notifiy.notifyManagerForApproval(savedFile, approvals.get(0).getManager());

        filePath.toFile().setReadOnly();
        return fileMapper.mapToResponse(savedFile);
    }


    public void deleteFile(Long fileId) throws IOException {
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new ResourceNotFoundException("File not found"));

        fileHelpers.ensureNotExpired(file);

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
                ? Sort.by("desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC, fileHelpers.mapSortField(sortBy))
                : Sort.by(Sort.Direction.DESC, "createdAt");
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
        if (pagePermissionService.hasScoppedReadAccess("Files")) {
            return fileRepository.findByDepartmentIdAndNotDeleted(deptId, pageable)
                    .map(fileMapper::mapToResponse);
        }

        return fileRepository.findByDepartmentIdAndStatusAndNotDeleted(deptId, FILE_STATUS.APPROVED, pageable).map(fileMapper::mapToResponse);
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
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return fileRepository.findByCreatedByIdAndIsDeletedFalse(userId, pageable)
                .map(fileMapper::mapToResponse);
    }

    public Page<FileResponse> listFilesByDepartment(Long departmentId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
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

    public Page<FileResponse> listFilesPendingMyApproval(int page, int size) {
        Department dept = currentUserProvider.getCurrentUser().getDepartment();
        if(dept == null) return Page.empty(PageRequest.of(page, size));
        Pageable pageable = PageRequest.of(page, size);
        return fileRepository.findPendingApprovalForDepartment(dept.getId(), pageable)
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
        fileHelpers.ensureNotExpired(file);

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
                String entryName =  fileHelpers.makeUniqueEntryName(file.getName(), usedNames);

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

    @Transactional
    public FileResponse updateFileStatus(Long fileId, UpdateFileStatusRequest request) {
        File file = fileRepository.findById(fileId).orElseThrow(() -> new ResourceNotFoundException("File not found"));
        fileHelpers.ensureNotExpired(file);

        if (file.getStatus() != FILE_STATUS.PENDING) {
            throw new IllegalStateException("This file has already been finalized");
        }

        User currentUser = currentUserProvider.getCurrentUser();
        Department managerDepartment =currentUser.getDepartment();

        if(managerDepartment == null) {
            throw new IllegalStateException("User has no department; cannot approve files");
        }

        FileDepartmentApproval approval = fileDepartmentApprovalRepo.findByFileIdAndDepartmentId(fileId,managerDepartment.getId())
                .orElseThrow(() -> new ResourceNotFoundException("This file is not routed to your department"));

        if (!approval.getCurrentApprovalOrder().equals(file.getCurrentApprovalOrder())) {
            throw new IllegalStateException("This file is not yet pending your department's approval");
        }

        approval.setStatus(request.getStatus());
        approval.setManager(currentUser);
        approval.setDecidedAt(java.time.LocalDateTime.now());

        fileDepartmentApprovalRepo.save(approval);

        advanceApproval(file, approval);
        return fileMapper.mapToResponse(fileRepository.save(file));
    }

    private void advanceApproval(File file, FileDepartmentApproval decidedApproval) {
        if(decidedApproval.getStatus() == FILE_STATUS.REJECTED){
            file.setStatus(FILE_STATUS.REJECTED);
            return;
        }
        Optional<FileDepartmentApproval> nextStep = fileDepartmentApprovalRepo
                .findByFileId(file.getId()).stream()
                .filter(a -> a.getCurrentApprovalOrder() > file.getCurrentApprovalOrder())
                .min(Comparator.comparing(FileDepartmentApproval::getCurrentApprovalOrder));

        if(nextStep.isPresent()){
            FileDepartmentApproval next = nextStep.get();
            file.setCurrentApprovalOrder(next.getCurrentApprovalOrder());
            file.getDepartments().add(next.getDepartment());
            notifiy.notifyManagerForApproval(file, next.getManager());
        }else{
            file.setStatus(FILE_STATUS.APPROVED);
            notifiy.notifyEmployeesOnApproval(file);
        }
    }

//    private void recomputeFileStatus(File file) {
//        List<FileDepartmentApproval> approvals = fileDepartmentApprovalRepo.findByFileId(file.getId());
//
//        boolean anyRejected = approvals.stream()
//                .anyMatch(a -> a.getStatus() == FILE_STATUS.REJECTED);
//        boolean allApproved = approvals.stream()
//                .allMatch(a -> a.getStatus() == FILE_STATUS.APPROVED);
//
//        if(anyRejected){
//            file.setStatus(FILE_STATUS.REJECTED);
//        }else if (allApproved && !approvals.isEmpty()){
//            file.setStatus(FILE_STATUS.APPROVED);
//        }else {
//            file.setStatus(FILE_STATUS.PENDING);
//        }
//    }


    public Page<FileResponse> search(FileSearchRequest request) {
        fileHelpers.enforceDepartmentScope(request);

        int pageNumber = request.getPage() != null ? request.getPage() : 0;
        int pageSize = request.getSize() != null ? request.getSize() : 10;

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        if(request.getSortBy() != null &&  !request.getSortBy().isBlank()) {
            Sort.Direction direction = "desc".equalsIgnoreCase(request.getSortDir()) ? Sort.Direction.DESC : Sort.Direction.ASC;
            sort = Sort.by(direction,  fileHelpers.mapSortField(request.getSortBy()));
        }
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        //        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Specification<File> spec = FileSpecification.search(request);
        Page<File> results = fileRepository.findAll(spec, pageable);

        return results.map(fileMapper::mapToResponse);
    }

    public ResponseEntity<ByteArrayResource> exportSearchResults(FileSearchRequest request) throws IOException {
        fileHelpers.enforceDepartmentScope(request);

        Specification<File> spec = FileSpecification.search(request);
        List<File> files = fileRepository.findAll(spec);

        StringBuilder csv = new StringBuilder();
        csv.append("ID,Name,Extension,Departments,Status,FileType,Size,Owner,CreatedDate,ModifiedDate\n");
        for (File f : files) {
            csv.append(f.getId()).append(",")
                    .append(fileHelpers.escapeCsv(f.getName())).append(",")
                    .append(f.getExtension()).append(",")
                    .append( fileHelpers.escapeCsv( fileHelpers.getDepartmentNames(f))).append(",")
                    .append(f.getStatus()).append(",")
                    .append(f.getFileType() != null ? f.getFileType().getName() : "").append(",")
                    .append(f.getSize()).append(",")
                    .append(f.getCreatedBy() != null ?  fileHelpers.escapeCsv(f.getCreatedBy().getName()) : "").append(",")
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

//    public List<SecurityLevelResponse> getSecurityLevels(){
//        List<SecurityLevel> securityLevels = securityLevelRepo.findAll();
//        return  securityLevels.stream().map(securityLevelMapper::mapToResponse).toList();
//    }

    public List<FileApprovalStepsResponse> GetFileApprovalSteps(Long fileId) {
        List<FileDepartmentApproval> fileApprovalSteps = fileDepartmentApprovalRepo.findByFileId(fileId);
        return fileApprovalSteps.stream()
                .map(fileApprovalStepsMapper::mapToResponse)
                .toList();
    }
}