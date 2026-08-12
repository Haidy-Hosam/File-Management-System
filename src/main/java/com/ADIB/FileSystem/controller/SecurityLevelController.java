package com.ADIB.FileSystem.controller;


import com.ADIB.FileSystem.Business.dto.request.SecurityLevelRequest;
import com.ADIB.FileSystem.Business.dto.response.SecurityLevelResponse;
import com.ADIB.FileSystem.Business.service.SecurityLevelService;
import com.ADIB.FileSystem.Business.service.file.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/securitylevel")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class SecurityLevelController {

    private final SecurityLevelService SecurityService;

    @GetMapping
    public ResponseEntity<List<SecurityLevelResponse>> getSecurityLevels() {
        return ResponseEntity.ok(SecurityService.getSecurityLevels());
    }

    @PostMapping("/{name}")
    public ResponseEntity<SecurityLevelResponse> addSecurityLevel(@PathVariable String name) {
        if(name == null || name.isEmpty()) {
            throw new RuntimeException("Security Level Name is required");
        }
        return ResponseEntity.ok(SecurityService.createSecurityLevel(name));
    }

    @PutMapping
    public ResponseEntity<SecurityLevelResponse> updateSecurityLevel(@RequestBody SecurityLevelRequest securityLevelreq) {
        long id = securityLevelreq.getId();
        String name = securityLevelreq.getName();
        if(id == 0){
            throw new RuntimeException("Security Level id is required");
        }
        return ResponseEntity.ok(SecurityService.updateSecurityLevel(id,name));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SecurityLevelResponse> deleteSecurityLevel(@PathVariable long id) {
        if(id == 0){
            throw new RuntimeException("Security Level id is required");
        }
        return ResponseEntity.ok(SecurityService.deleteSecurityLevel(id));
    }

}
