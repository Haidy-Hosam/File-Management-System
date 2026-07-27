package com.ADIB.FileSystem.controller;

import com.ADIB.FileSystem.Model.Permission;
import com.ADIB.FileSystem.repository.PermissionRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {
    private final PermissionRepo permissionRepo;

    @GetMapping
    public ResponseEntity<List<Permission>> getAllPermissions() {
        return ResponseEntity.ok(permissionRepo.findAll());
    }
}
