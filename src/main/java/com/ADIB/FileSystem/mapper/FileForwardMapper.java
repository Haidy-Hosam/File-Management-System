package com.ADIB.FileSystem.mapper;

import com.ADIB.FileSystem.Business.Model.FileForward;
import com.ADIB.FileSystem.Business.dto.response.FileForwardResponse;
import org.springframework.stereotype.Component;

@Component
public class FileForwardMapper {
    public FileForwardResponse mapToResponse(FileForward fileForward){
        return FileForwardResponse.builder()
                .id(fileForward.getId())
                .fileId(fileForward.getFile().getId())
                .fileName(fileForward.getFile().getName())
                .extension(fileForward.getFile().getExtension())
                .senderId(fileForward.getSender().getId())
                .senderName(fileForward.getSender().getName())
                .recipientId(fileForward.getRecipient().getId())
                .recipientName(fileForward.getRecipient().getName())
                .message(fileForward.getMessage())
                .isRead(fileForward.getIsRead())
                .forwardedAt(fileForward.getForwardedAt())
                .readAt(fileForward.getReadAt())
                .type(fileForward.getType())
                .build();
    }
}
