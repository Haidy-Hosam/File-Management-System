package com.ADIB.FileSystem.Business.dto.response;

import com.ADIB.FileSystem.Business.Enum.NOTIFICATIONTYPE;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    private Long senderId;
    private Long recipientId;

    private String fileName;
    private String extension;
    private String senderName;
    private String recipientName;
    private String message;

    @JsonProperty("isRead")
    private boolean isRead;

    private LocalDateTime forwardedAt;
    private LocalDateTime readAt;

    private NOTIFICATIONTYPE type;
}
