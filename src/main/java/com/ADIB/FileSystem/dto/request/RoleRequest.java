package com.ADIB.FileSystem.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RoleRequest {
    private String name;
    private List<Long> pageIds;
    private List<Long> permissionIds;
}