package com.ADIB.FileSystem.Business.dto.response;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleResponse {

    private Long id;
    private String name;
    private List<PagePermissionResponse> pagePermissions;

}