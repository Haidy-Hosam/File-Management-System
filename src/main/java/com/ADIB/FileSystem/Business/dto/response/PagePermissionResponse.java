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
public class PagePermissionResponse {
    private PageResponse page;
    private List<PermissionResponse> permissions;
}
