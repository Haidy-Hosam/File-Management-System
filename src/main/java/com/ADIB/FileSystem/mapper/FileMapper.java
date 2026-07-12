package com.ADIB.FileSystem.mapper;

import com.ADIB.FileSystem.Model.File;
import com.ADIB.FileSystem.dto.request.FileRequest;
import com.ADIB.FileSystem.dto.response.FileResponse;
import org.springframework.stereotype.Component;

@Component
// اعملي Object من FileMapper وخليه عندك في الـ Spring Container.
//
//عشان بعدين في الـ Service أقدر أعمل Dependency Injection:
public class FileMapper {

    public File toEntity(FileRequest request) {

        File file = new File();
        String fileName = request.getFile().getOriginalFilename();

        file.setName(
                request.getFile().getOriginalFilename()
        );

        file.setSize(
                request.getFile().getSize()
        );

        String extension = fileName.substring(
                fileName.lastIndexOf(".") + 1
        );

        file.setExtension(extension);

        return file;
    }
    public FileResponse mapToResponse(File file){
        return FileResponse.builder()
                .id(file.getId())
                .name(file.getName())
                .extension(file.getExtension())
                .department_id(file.getDepartment().getD_id())
                .status(file.getStatus().name())
                .build();
    }
}