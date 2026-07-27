package com.ADIB.FileSystem.controller;

import com.ADIB.FileSystem.dto.request.BulkFileUploadRequest;
import com.ADIB.FileSystem.dto.request.FileRequest;
import com.ADIB.FileSystem.dto.request.UpdateFileStatusRequest;
import com.ADIB.FileSystem.dto.response.FileResponse;
import com.ADIB.FileSystem.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
//@PreAuthorize("@pagePermissionService.hasPage('Files')")
public class FileController {
//nourtest
    private final FileService fileService;

    @PreAuthorize("@pagePermissionService.hasPermission('Files','CREATE')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileResponse> createFile(@ModelAttribute FileRequest request) throws IOException {
        return ResponseEntity.ok(fileService.uploadFile(request));
    }

    @PreAuthorize("@pagePermissionService.hasPermission('Files','CREATE')")
    @PostMapping(value = "/bulk", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<FileResponse>> createFilesBulk(@ModelAttribute BulkFileUploadRequest request) throws IOException {
        return ResponseEntity.ok(fileService.uploadFilesBulk(request));
    }

    @PreAuthorize("@pagePermissionService.hasPermission('Files','DELETE')")
    @DeleteMapping("/{fileId}")
    public ResponseEntity<Void> deleteFile(@PathVariable("fileId") Long fileId) throws IOException {
        fileService.deleteFile(fileId);
        return ResponseEntity.noContent().build();
    }

//    @PreAuthorize("hasRole('ADMIN')")
//    @GetMapping("/all")
//    public ResponseEntity<List<FileResponse>> getAllFiles() {
//        return ResponseEntity.ok(fileService.listAllFiles());
//    }

    @PreAuthorize("@pagePermissionService.hasPermission('Files','DELETE')")
    @GetMapping("/all")
    public ResponseEntity<Page<FileResponse>> getAllFiles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(fileService.listAllFiles(page, size));
    }

    @PreAuthorize("@pagePermissionService.hasPermission('Files','READ')")
    @GetMapping("/dept/{deptId}")
    public ResponseEntity<Page<FileResponse>> getAllFilesByDepartment(@PathVariable("deptId") Long deptId, @RequestParam(defaultValue = "0") int page,
                                                                      @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(fileService.listFilesByDepartment(deptId, page, size));
    }

    @PreAuthorize("@pagePermissionService.hasPermission('Files','READ')")
    @GetMapping("/{fileId}")
    public ResponseEntity<FileResponse> getFileData(@PathVariable("fileId") Long fileId) throws IOException {
        return ResponseEntity.ok(fileService.getFileData(fileId));
    }

    @GetMapping("/{fileId}/download")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable("fileId") Long fileId) throws IOException {
        return fileService.downloadFile(fileId);
    }

    @PreAuthorize("@pagePermissionService.hasPermission('Files','UPDATE')")
    @PutMapping("/{fileId}/status")
    public ResponseEntity<FileResponse> updateFileStatus(@PathVariable("fileId") Long fileId, @RequestBody UpdateFileStatusRequest fileStatus) {
        return ResponseEntity.ok(fileService.updateFileStatus(fileId, fileStatus));
    }

    @PostMapping("/download-bulk")
    public ResponseEntity<ByteArrayResource> downloadFilesBulk(@RequestBody List<Long> fileIds) throws IOException {
        return fileService.downloadFilesBulk(fileIds);
    }

    @PreAuthorize("@pagePermissionService.hasPermission('Files','DELETE')")
    @GetMapping("/trash")
    public ResponseEntity<Page<FileResponse>> listDeletedFiles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(fileService.listAllDeletedFiles(page, size));
    }

    @PreAuthorize("@pagePermissionService.hasPermission('Files','READ')")
    @GetMapping("/my")
    public ResponseEntity<Page<FileResponse>> getMyFiles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(fileService.listMyFiles(page, size));
    }
}
