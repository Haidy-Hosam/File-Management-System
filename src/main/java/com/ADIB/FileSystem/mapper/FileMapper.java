package com.ADIB.FileSystem.mapper;

import com.ADIB.FileSystem.Model.Department;
import com.ADIB.FileSystem.Model.File;
import com.ADIB.FileSystem.dto.request.FileRequest;
import com.ADIB.FileSystem.dto.response.FileResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class FileMapper {

    public File toEntity(FileRequest request) {
        File file = new File();
        String fileName = request.getFile().getOriginalFilename();
        file.setName(request.getFile().getOriginalFilename());
        file.setSize(request.getFile().getSize());
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
        file.setExtension(extension);
        return file;
    }

    public FileResponse mapToResponse(File file) {
        List<String> departmentNames = file.getDepartments() == null
                ? List.of()
                : file.getDepartments().stream()
                .map(Department::getName)
                .collect(Collectors.toList());

        return FileResponse.builder()
                .id(file.getId())
                .name(file.getName())
                .extension(file.getExtension())
                .departmentNames(departmentNames) // CHANGED — list, not single dept
                .status(file.getStatus().name())
                .fileType(file.getFileType().getName())
                .modifiedDate(file.getUpdatedAt().toLocalDate().toString())
                .size(file.getSize())
                .build();
    }
}