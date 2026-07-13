package com.ADIB.FileSystem.controller;

import com.ADIB.FileSystem.dto.request.FileRequest;
import com.ADIB.FileSystem.dto.request.RoleRequest;
import com.ADIB.FileSystem.dto.response.FileResponse;
import com.ADIB.FileSystem.dto.response.RoleResponse;
import com.ADIB.FileSystem.service.FileService;
import com.ADIB.FileSystem.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileResponse> createFile(
            @ModelAttribute FileRequest request
    ) throws IOException {

        return ResponseEntity.ok(fileService.uploadFile(request));
    }
    @DeleteMapping("/{fileId}")
    public ResponseEntity<Void> deleteFile(@PathVariable("fileId") Long fileId) throws IOException {
        fileService.deleteFile(fileId);
        return ResponseEntity.noContent().build();

    }

    @GetMapping("/all")
    public ResponseEntity<List<FileResponse>> getAllFiles() {
        return ResponseEntity.ok(fileService.listAllFiles());
    }
    @GetMapping("/dept/{deptId}")
    public ResponseEntity<List<FileResponse>> getAllFilesByDepartment(@PathVariable("deptId") Long deptId) {
        return ResponseEntity.ok(fileService.listFilesByDepartment(deptId));
    }
    @GetMapping("/{fileId}")
    public ResponseEntity<FileResponse> getFileData(@PathVariable("fileId") Long fileId) throws IOException {
        return ResponseEntity.ok(fileService.getFileData(fileId));
    }
}
