package com.ADIB.FileSystem.mapper;

import com.ADIB.FileSystem.Business.Model.Department;
import com.ADIB.FileSystem.Business.Model.File;
import com.ADIB.FileSystem.Business.Model.FileDepartmentApproval;
import com.ADIB.FileSystem.Business.dto.request.FileRequest;
import com.ADIB.FileSystem.Business.dto.response.FileResponse;
import com.ADIB.FileSystem.DataAccess.repository.FileDepartmentApprovalRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FileMapper {
    private final FileDepartmentApprovalRepo fileDepartmentApprovalRepo;

    public File toEntity(FileRequest request) {
        File file = new File();
        String fileName = request.getFile().getOriginalFilename();
        file.setName(request.getFile().getOriginalFilename());
        file.setSize(request.getFile().getSize());
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
        file.setExtension(extension);
        return file;
    }

    public FileResponse mapToResponse(File file) {
        List<String> departmentNames = fileDepartmentApprovalRepo.findByFileId(file.getId()).stream()
                .map(FileDepartmentApproval::getDepartment)
                .map(com.ADIB.FileSystem.Business.Model.Department::getName)
                .distinct()
                .collect(Collectors.toList());

        return FileResponse.builder()
                .id(file.getId())
                .name(file.getName())
                .extension(file.getExtension())
                .departmentNames(departmentNames) // CHANGED — list, not single dept
                .status(file.getStatus().name())
                .fileType(file.getFileType().getName())
                .modifiedDate(file.getUpdatedAt().toLocalDate().toString())
                .createdDate(file.getCreatedAt().toLocalDate().toString())
                .size(file.getSize())
                .ownerName(file.getCreatedBy() != null ? file.getCreatedBy().getName() : "Unknown")
                .securityLevel(file.getSecurityLevel() != null ? file.getSecurityLevel().getName() : null)
                .expired(file.getExpired())
                .build();
    }
}