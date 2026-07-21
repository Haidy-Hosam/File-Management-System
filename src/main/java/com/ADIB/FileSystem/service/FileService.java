package com.ADIB.FileSystem.service;

import com.ADIB.FileSystem.Enum.FILE_STATUS;
import com.ADIB.FileSystem.Model.Department;
import com.ADIB.FileSystem.Model.File;
import com.ADIB.FileSystem.Model.FileType;
import com.ADIB.FileSystem.dto.request.BulkFileUploadRequest;
import com.ADIB.FileSystem.dto.request.FileRequest;
import com.ADIB.FileSystem.dto.request.UpdateFileStatusRequest;
import com.ADIB.FileSystem.dto.response.FileResponse;
import com.ADIB.FileSystem.exception.ResourceNotFoundException;
import com.ADIB.FileSystem.mapper.FileMapper;
import com.ADIB.FileSystem.repository.DepartmentRepo;
import com.ADIB.FileSystem.repository.FileRepo;
import com.ADIB.FileSystem.repository.FileTypeRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepo fileRepository;
    private final FileMapper fileMapper;
    private final DepartmentRepo departmentRepository;
    private final FileEncryptionService fileEncryptionService;
    private final FileTypeRepo fileTypeRepo;
    private static final Path UPLOAD_DIRECTORY = Paths.get(
            "C:\\Users\\ganna\\Downloads\\FileSystem\\src\\main\\java\\com\\ADIB\\FileSystem\\uploads"
    );

    public FileResponse uploadFile(FileRequest request) throws IOException {

        List<Department> departments = departmentRepository.findAllById(request.getDepartment_ids());
        if (departments.isEmpty()) {
            throw new ResourceNotFoundException("No valid departments found");
        }

        FileType fileType = fileTypeRepo.findById(request.getFileType_id())
                .orElseThrow(
                        () -> new ResourceNotFoundException("File type not found")
                );
        String fileName = request.getFile().getOriginalFilename();

        String extension = fileName.substring(
                fileName.lastIndexOf(".") + 1
        );

        Path uploadDirectory = Paths.get("C:\\Users\\ganna\\Downloads\\FileSystem\\src\\main\\java\\com\\ADIB\\FileSystem\\uploads");

        Files.createDirectories(uploadDirectory);

        Path filePath = uploadDirectory.resolve(fileName);
//    store normal file-------------------
//        Files.copy(
//                request.getFile().getInputStream(),
//                filePath
//        );
        byte[] fileBytes = request.getFile().getBytes();
        byte[] encryptedBytes;
        try{
        encryptedBytes = fileEncryptionService.encrypt(fileBytes);
        }catch(Exception e){
            throw new IOException("Failed to encrypt and save file", e);
        }

        Files.write(filePath, encryptedBytes);

        File file = File.builder()
                .name(fileName)
                .path(filePath.toString())
                .size(request.getFile().getSize())
                .extension(extension)
                .status(FILE_STATUS.PENDING)
                .departments(departments)
                .fileType(fileType)
                .build();

        File savedFile = fileRepository.save(file);

        return fileMapper.mapToResponse(savedFile);
    }

    // Multiple files (each with its own file type) sent to one or more departments.
    // Produces one File row per (file, department) combination.
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

        // Resolve all departments up front so we fail fast on a bad id
        // before writing anything to disk.
        List<Department> departments = new ArrayList<>();
        for (Long deptId : departmentIds) {
            departments.add(departmentRepository.findById(deptId)
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found: " + deptId)));
        }

        List<FileResponse> results = new ArrayList<>();

        for (int i = 0; i < files.size(); i++) {
            MultipartFile multipartFile = files.get(i);
            Long fileTypeId = fileTypeIds.get(i);

            FileType fileType = fileTypeRepo.findById(fileTypeId)
                    .orElseThrow(() -> new ResourceNotFoundException("File type not found: " + fileTypeId));

            String fileName = multipartFile.getOriginalFilename();
            String extension = extractExtension(fileName);
            byte[] fileBytes = multipartFile.getBytes();

            // ONE record per file, linked to ALL selected departments at once
            FileResponse response = storeSingleFile(
                    fileName, extension, fileBytes, multipartFile.getSize(), departments, fileType
            );
            results.add(response);
        }

        return results;
    }

    private FileResponse storeSingleFile(
            String originalFileName,
            String extension,
            byte[] fileBytes,
            long size,
            List<Department> departments, // CHANGED — list, not single department
            FileType fileType

    ) throws IOException {

        Files.createDirectories(UPLOAD_DIRECTORY);

        String storedFileName = UUID.randomUUID() + "_" + originalFileName;
        Path filePath = UPLOAD_DIRECTORY.resolve(storedFileName);

        byte[] encryptedBytes;
        try {
            encryptedBytes = fileEncryptionService.encrypt(fileBytes);
        } catch (Exception e) {
            throw new IOException("Failed to encrypt and save file", e);
        }

        Files.write(filePath, encryptedBytes);

        File file = File.builder()
                .name(originalFileName)
                .path(filePath.toString())
                .size(size)
                .extension(extension)
                .status(FILE_STATUS.PENDING)
                .departments(departments) // CHANGED — one row, many departments
                .fileType(fileType)
                .build();

        File savedFile = fileRepository.save(file);
        return fileMapper.mapToResponse(savedFile);
    }


    private String extractExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }


    public void deleteFile(Long fileId)  throws IOException {
        File file = fileRepository.findById(fileId).orElseThrow(() -> new ResourceNotFoundException("File not found"));
        Path filePath = Paths.get(file.getPath());
        Files.deleteIfExists(filePath);
        fileRepository.deleteById(fileId);
    }

    public List<FileResponse> listAllFiles() {
        return fileRepository.findAll()
                .stream()
                .map(fileMapper::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<FileResponse> listFilesByDepartment(Long departmentId) {
        departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        return fileRepository.findByDepartmentId(departmentId)
                .stream()
                .map(fileMapper::mapToResponse)
                .collect(Collectors.toList());
    }
    public FileResponse getFileData(Long fileId) throws IOException {
        File file = fileRepository.findById(fileId).orElseThrow(() -> new ResourceNotFoundException("File not found"));
        return fileMapper.mapToResponse(file);
    }

    public ResponseEntity<ByteArrayResource> downloadFile(Long fileId) throws IOException {
        File file = fileRepository.findById(fileId).orElseThrow(() -> new ResourceNotFoundException("File not found"));

        Path filePath = Paths.get(file.getPath());
        byte[] encryptedBytes  = Files.readAllBytes(filePath);
        ByteArrayResource byteArrayResource;
        try{
            byte[] originalBytes = fileEncryptionService.decrypt(encryptedBytes);
            byteArrayResource =new ByteArrayResource(originalBytes);
        }catch(Exception e){
            throw new IOException("Failed to decrypt and save file", e);
        }
        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + file.getName() + "\""
                )
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(byteArrayResource);
    }

    public FileResponse updateFileStatus(Long fileId, UpdateFileStatusRequest request)  {
        File file = fileRepository.findById(fileId).orElseThrow(() -> new ResourceNotFoundException("File not found"));
        file.setStatus(request.getStatus());
        File updatedFile = fileRepository.save(file);
        return fileMapper.mapToResponse(updatedFile);
    }


    public ResponseEntity<ByteArrayResource> downloadFilesBulk (List<Long> fileIds)  throws IOException {
        List<File> files = fileRepository.findAllById(fileIds);
        if(files.isEmpty()){
            throw new ResourceNotFoundException("Files not found");
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            for (File file : files) {
                Path filePath = Paths.get(file.getPath());
                byte[] encryptedBytes = Files.readAllBytes(filePath);

                byte[] originalBytes ;
                try{
                    originalBytes = fileEncryptionService.decrypt(encryptedBytes);
                }catch (Exception e){
                    throw new IOException("Failed to decrypt and save file" + file.getName(), e);
                }

                String entryName = "Arkive"+ "_" + file.getName();
                zos.putNextEntry(new ZipEntry(entryName));
                zos.write(originalBytes);
                zos.closeEntry();
            }
        }

        ByteArrayResource resource = new ByteArrayResource(baos.toByteArray());

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\"files.zip\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

}