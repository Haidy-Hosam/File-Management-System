package com.ADIB.FileSystem.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkFileUploadRequest {
    private List<MultipartFile> files;
    private List<Long> fileTypeIds;

    private List<Long> departmentIds;
}
