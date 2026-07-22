package com.ADIB.FileSystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileResponse {
    private Long id;
    private String name;
    private String extension;
    private List<String> departmentNames;
    private String status;
    private String fileType;
    private Long size;
    private String modifiedDate;
}