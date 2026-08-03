package com.ADIB.FileSystem.controller;

import com.ADIB.FileSystem.Business.Model.User;
import com.ADIB.FileSystem.security.CurrentUserProvider;
import com.ADIB.FileSystem.Business.service.NotificationSseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class NotificationController {
    private final NotificationSseService notificationSseService;
    private final CurrentUserProvider currentUserProvider;



    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe() {
        User currentUser = currentUserProvider.getCurrentUser();
        return notificationSseService.subscribe(currentUser.getId());
    }
}
