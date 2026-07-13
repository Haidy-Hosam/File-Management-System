package com.ADIB.FileSystem.service;

import com.ADIB.FileSystem.Enum.FILE_STATUS;
import com.ADIB.FileSystem.Model.Department;
import com.ADIB.FileSystem.Model.File;
import com.ADIB.FileSystem.dto.request.FileRequest;
import com.ADIB.FileSystem.dto.response.FileResponse;
import com.ADIB.FileSystem.exception.ResourceNotFoundException;
import com.ADIB.FileSystem.mapper.FileMapper;
import com.ADIB.FileSystem.repository.DepartmentRepo;
import com.ADIB.FileSystem.repository.FileRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepo fileRepository;
    private final FileMapper fileMapper;
    private final DepartmentRepo departmentRepository;

    public FileResponse uploadFile(FileRequest request) throws IOException {

        Department department = departmentRepository
                .findById(request.getDepartment_id())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Department not found")
                );

        String fileName = request.getFile().getOriginalFilename();

        String extension = fileName.substring(
                fileName.lastIndexOf(".") + 1
        );

        Path uploadDirectory = Paths.get("C:\\Users\\ganna\\Downloads\\FileSystem\\src\\main\\java\\com\\ADIB\\FileSystem\\uploads");

        Files.createDirectories(uploadDirectory);

        Path filePath = uploadDirectory.resolve(fileName);// ركّبي الاتنين على بعض كـ Path

        Files.copy(
                request.getFile().getInputStream(),
                filePath
        );
        File file = File.builder()
                .name(fileName)
                .path(filePath.toString())
                .size(request.getFile().getSize())
                .extension(extension)
                .status(FILE_STATUS.PENDING)
                .department(department)
                .build();

        File savedFile = fileRepository.save(file);

        return fileMapper.mapToResponse(savedFile);
    }

    // -----------------------------------------------------------------------------------------------
//
//    public FileResponse updateFile(FileRequest request , Long id) throws IOException {
//
//        Department department = departmentRepository
//                .findById(request.getDepartment_id())
//                .orElseThrow(() ->
//                        new ResourceNotFoundException("Department not found")
//                );
//
//        String fileName = request.getFile().getOriginalFilename();
//
//        String extension = fileName.substring(
//                fileName.lastIndexOf(".") + 1
//        );
//
//        Path uploadDirectory = Paths.get("uploads");
//
//        Files.createDirectories(uploadDirectory);
//
//        Path filePath = uploadDirectory.resolve(fileName);// ركّبي الاتنين على بعض كـ Path
//
//
//        File file = File.builder()
//                .name(fileName)
//                .path(filePath.toString())
//                .size(request.getFile().getSize())
//                .extension(extension)
//                .department(department)
//                .build();
//
//        File savedFile = fileRepository.save(file);
//
//        return fileMapper.mapToResponse(savedFile);
//    }
}