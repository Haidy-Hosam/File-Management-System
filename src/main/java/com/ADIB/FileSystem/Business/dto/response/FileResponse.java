package com.ADIB.FileSystem.Business.dto.response;

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
    private Long size;

    private String name;
    private String extension;
    private String status;
    private String fileType;
    private String modifiedDate;
    private String createdDate;
    private String ownerName;

    private List<String> departmentNames;

}