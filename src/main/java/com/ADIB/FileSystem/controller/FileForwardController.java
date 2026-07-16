package com.ADIB.FileSystem.controller;

import com.ADIB.FileSystem.dto.request.ForwardFileRequest;
import com.ADIB.FileSystem.dto.response.FileForwardResponse;
import com.ADIB.FileSystem.service.FileForwardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@PreAuthorize("@permissionService.hasPage('FileForward')")
public class FileForwardController {
    private final FileForwardService fileForwardService;
    @PostMapping("{fileId}/forward")
    public ResponseEntity<List<FileForwardResponse>> forwardFile(@PathVariable Long fileId, @RequestBody ForwardFileRequest request){
        return ResponseEntity.ok(fileForwardService.forwardFile(fileId, request));
    }

    @GetMapping("/forwarded/sent")
    public ResponseEntity<List<FileForwardResponse>> getSentForwards(){
        return ResponseEntity.ok(fileForwardService.getFilesForwardedByMe());
    }

    @GetMapping("/forwarded/notifications")
    public ResponseEntity<List<FileForwardResponse>> getUnreadNotifications(){
        return ResponseEntity.ok(fileForwardService.getUnreadNotifications());
    }

    @GetMapping("/forwarded/notifications/count")
    public ResponseEntity<Map<String ,Long>> getUnreadCount(){
        return ResponseEntity.ok(Map.of("unreadCount", fileForwardService.getUnreadCount()));
    }

    @PutMapping("/forwarded/{forwardId}/open")
    public ResponseEntity<FileForwardResponse> openForwardedFile(@PathVariable Long forwardId ){
        return ResponseEntity.ok(fileForwardService.openForwardedFile(forwardId));
    }
}
