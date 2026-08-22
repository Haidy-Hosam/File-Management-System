package com.ADIB.FileSystem.controller;

import com.ADIB.FileSystem.Business.dto.request.ForwardFileRequest;
import com.ADIB.FileSystem.Business.dto.response.FileForwardResponse;
import com.ADIB.FileSystem.Business.service.file.FileForwardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class FileForwardController {
    private final FileForwardService fileForwardService;
    @PostMapping("{fileId}/forward")
    public ResponseEntity<List<FileForwardResponse>> forwardFile(@PathVariable Long fileId, @RequestBody ForwardFileRequest request){
        return ResponseEntity.ok(fileForwardService.forwardFile(fileId, request));
    }

    @GetMapping("/forwarded/sent")
    public ResponseEntity<Page<FileForwardResponse>> getSentForwards(@RequestParam(required = false) Long userId,@RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.ok(fileForwardService.getSentForwards(userId, page, size));
    }

    @GetMapping("/forwarded/notifications")
    public ResponseEntity<Page<FileForwardResponse>> getUnreadNotifications(@RequestParam(defaultValue = "0") int page,
                                                                            @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.ok(fileForwardService.getUnreadNotifications(page, size));
    }

    @GetMapping("/forwarded/notifications/count")
    public ResponseEntity<Map<String ,Long>> getUnreadCount(){
        return ResponseEntity.ok(Map.of("unreadCount", fileForwardService.getUnreadCount()));
    }

    @PutMapping("/forwarded/{forwardId}/open")
    public ResponseEntity<FileForwardResponse> openForwardedFile(@PathVariable Long forwardId ){
        return ResponseEntity.ok(fileForwardService.openForwardedFile(forwardId));
    }

    @GetMapping("/forwarded/received")
    public ResponseEntity<List<FileForwardResponse>> getReceivedForwards(@RequestParam(required = false) Long userId,@RequestParam(defaultValue = "0") int page,
                                                                         @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.ok(fileForwardService.getReceivedForwards(userId, page, size));
    }

}
