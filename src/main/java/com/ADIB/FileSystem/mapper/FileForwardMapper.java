package com.ADIB.FileSystem.mapper;

import com.ADIB.FileSystem.Model.FileForward;
import com.ADIB.FileSystem.dto.response.FileForwardResponse;
import org.springframework.stereotype.Component;

@Component
public class FileForwardMapper {
    public FileForwardResponse mapToResponse(FileForward fileForward){
        return FileForwardResponse.builder()
                .id(fileForward.getId())
                .fileId(fileForward.getFile().getId())
                .fileName(fileForward.getFile().getName())
                .extension(fileForward.getFile().getExtension())
                .senderId(fileForward.getSender().getU_id())
                .senderName(fileForward.getSender().getName())
                .recipientId(fileForward.getRecipient().getU_id())
                .recipientName(fileForward.getRecipient().getName())
                .message(fileForward.getMessage())
                .isRead(Boolean.TRUE.equals(fileForward.getIsRead()))
                .forwardedAt(fileForward.getForwardedAt())
                .readAt(fileForward.getReadAt())
                .build();
    }
}
