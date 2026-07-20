package com.ADIB.FileSystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileResponse {
    private Long id;
    private String name;
    private String extension;
    private Long department_id;
    private String departmentName;
    private String status;
    private String fileType;
    private Long size;
    private String modifiedDate;
}