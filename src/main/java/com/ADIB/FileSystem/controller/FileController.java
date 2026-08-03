package com.ADIB.FileSystem.controller;

import com.ADIB.FileSystem.Business.dto.request.BulkFileUploadRequest;
import com.ADIB.FileSystem.Business.dto.request.FileRequest;
import com.ADIB.FileSystem.Business.dto.request.FileSearchRequest;
import com.ADIB.FileSystem.Business.dto.request.UpdateFileStatusRequest;
import com.ADIB.FileSystem.Business.dto.response.FileResponse;
import com.ADIB.FileSystem.Business.service.file.FileService;
import jakarta.validation.Valid;
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
public class FileController {
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

    @PreAuthorize("@pagePermissionService.hasPermission('Files','UPDATE')")
    @PutMapping("/{fileId}/status")
    public ResponseEntity<FileResponse> updateFileStatus(@PathVariable("fileId") Long fileId, @RequestBody UpdateFileStatusRequest fileStatus) {
        return ResponseEntity.ok(fileService.updateFileStatus(fileId, fileStatus));
    }
    @PreAuthorize("@pagePermissionService.hasPermission('Files','DELETE')")
    @GetMapping("/trash")
    public ResponseEntity<Page<FileResponse>> listDeletedFiles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(fileService.listAllDeletedFiles(page, size));
    }

    @PreAuthorize("@pagePermissionService.hasPermission('Files','DELETE')")
    @DeleteMapping("/{fileId}")
    public ResponseEntity<Void> deleteFile(@PathVariable("fileId") Long fileId) throws IOException {
        fileService.deleteFile(fileId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("@pagePermissionService.canRead('Files')")
    @PostMapping("/search")
    public ResponseEntity<Page<FileResponse>> search(@Valid @RequestBody FileSearchRequest request) {
        return ResponseEntity.ok(fileService.search(request));
    }

    @PreAuthorize("@pagePermissionService.canRead('Files')")
    @PostMapping("/search/export")
    public ResponseEntity<ByteArrayResource> exportSearch(@Valid @RequestBody FileSearchRequest request) throws IOException {
        return fileService.exportSearchResults(request);
    }

    @PreAuthorize("@pagePermissionService.canRead('Files')")
    @GetMapping
    public ResponseEntity<Page<FileResponse>> listFiles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDir
    ) {
        return ResponseEntity.ok(fileService.listFiles(page, size, sortBy, sortDir));
    }

    @PreAuthorize("@pagePermissionService.canRead('Files')")
    @GetMapping("/dept/{deptId}")
    public ResponseEntity<Page<FileResponse>> getAllFilesByDepartment(@PathVariable("deptId") Long deptId, @RequestParam(defaultValue = "0") int page,
                                                                      @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(fileService.listFilesByDepartment(deptId, page, size));
    }
    @PreAuthorize("@pagePermissionService.canRead('Files')")
    @GetMapping("/my")
    public ResponseEntity<Page<FileResponse>> getMyFiles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(fileService.listMyFiles(page, size));
    }

    @PreAuthorize("@pagePermissionService.canRead('Files')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<FileResponse>> getFilesByUser(
            @PathVariable("userId") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(fileService.listFilesByUser(userId, page, size));
    }

    @PreAuthorize("@pagePermissionService.canRead('Files')")
    @GetMapping("/{fileId}")
    public ResponseEntity<FileResponse> getFileData(@PathVariable("fileId") Long fileId) throws IOException {
        return ResponseEntity.ok(fileService.getFileData(fileId));
    }

    @PreAuthorize("@pagePermissionService.canRead('Files')")
    @GetMapping("/{fileId}/download")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable("fileId") Long fileId) throws IOException {
        return fileService.downloadFile(fileId);
    }
    @PreAuthorize("@pagePermissionService.canRead('Files')")
    @PostMapping("/download-bulk")
    public ResponseEntity<ByteArrayResource> downloadFilesBulk(@RequestBody List<Long> fileIds) throws IOException {
        return fileService.downloadFilesBulk(fileIds);
    }
}
