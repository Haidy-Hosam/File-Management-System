package com.ADIB.FileSystem.controller;

import com.ADIB.FileSystem.dto.request.FileTypeRequest;
import com.ADIB.FileSystem.dto.response.FileTypeResponse;
import com.ADIB.FileSystem.service.FileTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/file-types")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
//@PreAuthorize("@permissionService.hasPage('FileTypes')")
public class FileTypeController {
    private final FileTypeService fileTypeService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<FileTypeResponse> create(@RequestBody FileTypeRequest fileTypeRequest) {
        return ResponseEntity.ok(fileTypeService.create(fileTypeRequest));
    }

    @GetMapping
    public ResponseEntity<List<FileTypeResponse>> getAll() {
        return ResponseEntity.ok(fileTypeService.getAll());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<FileTypeResponse> update(@PathVariable Long id, @RequestBody FileTypeRequest fileTypeRequest) {
        return ResponseEntity.ok(fileTypeService.update(id, fileTypeRequest));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void>  delete(@PathVariable Long id) {
        fileTypeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
