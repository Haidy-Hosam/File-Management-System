package com.ADIB.FileSystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileForwardResponse {
    private Long id;

    private Long fileId;
    private String fileName;
    private String extension;

    private Long senderId;
    private String senderName;

    private Long recipientId;
    private String recipientName;

    private String message;
    private boolean isRead;
    private LocalDateTime forwardedAt;
    private LocalDateTime readAt;
}
