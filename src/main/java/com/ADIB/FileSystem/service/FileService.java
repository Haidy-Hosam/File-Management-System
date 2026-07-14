package com.ADIB.FileSystem.service;

import com.ADIB.FileSystem.Enum.FILE_STATUS;
import com.ADIB.FileSystem.Model.Department;
import com.ADIB.FileSystem.Model.File;
import com.ADIB.FileSystem.Model.FileType;
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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepo fileRepository;
    private final FileMapper fileMapper;
    private final DepartmentRepo departmentRepository;
    private final FileEncryptionService fileEncryptionService;
    private final FileTypeRepo fileTypeRepo;

    public FileResponse uploadFile(FileRequest request) throws IOException {

        Department department = departmentRepository
                .findById(request.getDepartment_id())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Department not found")
                );
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
                .department(department)
                .fileType(fileType)
                .build();

        File savedFile = fileRepository.save(file);

        return fileMapper.mapToResponse(savedFile);
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
        Department department = departmentRepository.findById(departmentId).orElseThrow(() -> new ResourceNotFoundException("Department not found"));
        return fileRepository.findByDepartment(department)
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
}