package com.ADIB.FileSystem.Business.service.file.helpers;

import com.ADIB.FileSystem.Business.Enum.FILE_STATUS;
import com.ADIB.FileSystem.Business.Exceptions.FileExpiredException;
import com.ADIB.FileSystem.Business.Model.Department;
import com.ADIB.FileSystem.Business.Model.File;
import com.ADIB.FileSystem.Business.dto.request.FileSearchRequest;
import com.ADIB.FileSystem.Business.service.Permissions.PagePermissionService;
import com.ADIB.FileSystem.Business.service.file.FileEncryptionService;
import com.ADIB.FileSystem.security.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class FileHelpers {
    private final PagePermissionService pagePermissionService;
    private final CurrentUserProvider currentUserProvider;

    private final FileEncryptionService fileEncryptionService;


    public void writeEncrypted(Path filePath, byte[] rawBytes) throws IOException {
        byte[] encryptedBytes;
        try {
            encryptedBytes = fileEncryptionService.encrypt(rawBytes);
        }catch (Exception e) {
            throw new IOException("Failed to encrypt and save file", e);
        }
        Files.write(filePath, encryptedBytes);
    }

    public String extractExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    public String makeUniqueEntryName(String originalName, Set<String> usedNames) {
        if (usedNames.add(originalName)) {
            return originalName;
        }

        String baseName = originalName;
        String extension = "";
        int dotIndex = originalName.lastIndexOf('.');
        if (dotIndex > 0) {
            baseName = originalName.substring(0, dotIndex);
            extension = originalName.substring(dotIndex);
        }

        int counter = 1;
        String candidate;
        do {
            candidate = baseName + " (" + counter + ")" + extension;
            counter++;
        } while (!usedNames.add(candidate));

        return candidate;
    }

    public String mapSortField(String field) {
        return switch (field) {
            case "ownerName" -> "createdBy.name";
            case "createdDate" -> "createdAt";
            case "modifiedDate" -> "updatedAt";
            default -> field;
        };
    }

    public String getDepartmentNames(File f) {
        if(f.getDepartments() == null) return "";
        return  f.getDepartments().stream().map(Department::getName)
                .collect(java.util.stream.Collectors.joining("; "));
    }

    public String escapeCsv(String value) {
        if(value == null) return "";
        if(value.contains(",") || value.contains("\"") || value.contains("\n")){
            return "\"" + value.replace("\"","\"\"")+ "\"";
        }
        return value;
    }

    public void enforceDepartmentScope(FileSearchRequest request) {
        if (pagePermissionService.hasFullReadAccess("Files")) {
            return;
        }
        Department dept = currentUserProvider.getCurrentUser().getDepartment();
        request.setDepartments(dept != null ? List.of(dept.getName()) : List.of("__NO_DEPARTMENT__"));

        if(!pagePermissionService.hasPermission("Files","UPDATE")) {
            request.setStatuses(List.of(FILE_STATUS.APPROVED));
        }
    }

    public void ensureNotExpired(File file) {
        if (Boolean.TRUE.equals(file.getExpired())) {
            throw new FileExpiredException("File has expired.");
        }
    }



}
