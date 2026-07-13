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

//    @GetMapping
//    public <> ()
//
//    {
//
//    }

//    @PostMapping
//    public FileResponse uploadFile(
//            @ModelAttribute FileRequest request // @ModelAttribute عشان ببعت multipart/form-data
//    ) {
//        return fileService.uploadFile(request);
//    }
//
//    @GetMapping("/{id}")
//
//    @PutMapping("/{id}")
//
//
//    @DeleteMapping("/{id}")
}
