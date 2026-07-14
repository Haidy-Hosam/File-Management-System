package com.ADIB.FileSystem.mapper;

import com.ADIB.FileSystem.Model.FileType;
import com.ADIB.FileSystem.dto.response.FileTypeResponse;
import org.springframework.stereotype.Component;

@Component
public class FileTypeMapper {
    public FileTypeResponse mapToResponse(FileType fileType) {
        return   FileTypeResponse.builder()
                .id(fileType.getId())
                .name(fileType.getName())
                .description(fileType.getDescription())
                .build();
    }
}
