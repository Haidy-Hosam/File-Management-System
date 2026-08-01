package com.ADIB.FileSystem.Business.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagePermissionRequest {
    private Long pageId;
    private List<Long> permissionIds;
}
