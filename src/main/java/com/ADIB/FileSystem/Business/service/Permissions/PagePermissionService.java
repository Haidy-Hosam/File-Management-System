package com.ADIB.FileSystem.Business.service.Permissions;

import com.ADIB.FileSystem.Business.Model.Page;
import com.ADIB.FileSystem.Business.Model.RolePagePermission;
import com.ADIB.FileSystem.Business.Model.User;
import com.ADIB.FileSystem.Business.dto.response.PagePermissionResponse;
import com.ADIB.FileSystem.Business.dto.response.PageResponse;
import com.ADIB.FileSystem.Business.dto.response.PermissionResponse;
import com.ADIB.FileSystem.DataAccess.repository.UserRepo;
import com.ADIB.FileSystem.security.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PagePermissionService {
    private final CurrentUserProvider currentUserProvider;

    @Transactional(readOnly = true)
    public List<PageResponse> getMyPages() {
        User user = currentUserProvider.getCurrentUser();
        return user.getRole()
                .getRolePagePermissions()
                .stream()
                .map(RolePagePermission::getPage)
                .distinct()
                .map(p -> new PageResponse(p.getId(), p.getPageName(), p.getRoute()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PagePermissionResponse> getMyPermissions() {
        User user = currentUserProvider.getCurrentUser();
        Map<Page, List<RolePagePermission>> byPage = user.getRole()
                .getRolePagePermissions()
                .stream()
                .collect(Collectors.groupingBy(RolePagePermission::getPage));
        return byPage.entrySet().stream()
                .map(entry -> PagePermissionResponse.builder()
                        .page(new PageResponse(entry.getKey().getId(),
                                entry.getKey().getPageName(),
                                entry.getKey().getRoute()))
                        .permissions(entry.getValue().stream()
                                .map(RolePagePermission::getPermission)
                                .distinct()
                                .map(p -> PermissionResponse.builder()
                                        .permissionId(p.getPermissionId())
                                        .permissionName(p.getPermissionName())
                                        .build())
                                .toList())
                        .build())
                .toList();
    }

    @Transactional(readOnly = true)
    public boolean hasPermission(String pageName,String permissionName){
        User user = currentUserProvider.getCurrentUser();
        return user.getRole()
                .getRolePagePermissions()
                .stream()
                .anyMatch(rpp -> rpp.getPage().getPageName().equalsIgnoreCase(pageName)
                        && rpp.getPermission().getPermissionName().equalsIgnoreCase(permissionName));
    }

    @Transactional(readOnly = true)
    public boolean hasFullReadAccess(String pageName){
        return hasPermission(pageName, "READ_ALL");
    }

    @Transactional(readOnly = true)
    public boolean canRead(String pageName){
        return hasPermission(pageName, "READ_ALL") || hasPermission(pageName, "READ_SCOPED");
    }

}
