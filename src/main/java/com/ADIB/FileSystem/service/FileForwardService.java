package com.ADIB.FileSystem.service;

import com.ADIB.FileSystem.Model.File;
import com.ADIB.FileSystem.Model.FileForward;
import com.ADIB.FileSystem.Model.User;
import com.ADIB.FileSystem.dto.request.ForwardFileRequest;
import com.ADIB.FileSystem.dto.response.FileForwardResponse;
import com.ADIB.FileSystem.event.FileForwardedEvent;
import com.ADIB.FileSystem.exception.ResourceNotFoundException;
import com.ADIB.FileSystem.mapper.FileForwardMapper;
import com.ADIB.FileSystem.repository.FileForwardRepo;
import com.ADIB.FileSystem.repository.FileRepo;
import com.ADIB.FileSystem.repository.UserRepo;
import com.ADIB.FileSystem.security.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileForwardService {
    private final FileForwardRepo fileForwardRepo;
    private final FileRepo fileRepo;
    private final UserRepo userRepo;
    private final FileForwardMapper fileForwardMapper;
    private final CurrentUserProvider currentUserProvider;
    private final ApplicationEventPublisher eventPublisher;

    public List<FileForwardResponse> forwardFile(long fileId, ForwardFileRequest request){
        User sender = currentUserProvider.getCurrentUser();

        File file = fileRepo.findById(fileId).orElseThrow(() -> new ResourceNotFoundException("File not found"));
        if(request.getRecipientIds() == null|| request.getRecipientIds().isEmpty()){
            throw new ResourceNotFoundException("Recipients not found");
        }

        List<FileForwardResponse> responses =new ArrayList<>();

        for(Long recipientId : request.getRecipientIds()) {
            if(recipientId.equals(sender.getU_id())){
                continue;
            }
            User recipient = userRepo.findById(recipientId).orElseThrow(() -> new ResourceNotFoundException("User not found"));

            FileForward forward =FileForward.builder()
                    .file(file)
                    .sender(sender)
                    .recipient(recipient)
                    .message(request.getMessage())
                    .isRead(false)
                    .build();

            FileForward saved = fileForwardRepo.save(forward);

            eventPublisher.publishEvent(new FileForwardedEvent(this, saved));

            responses.add(fileForwardMapper.mapToResponse(saved));
        }
        return responses;
    }

    public List<FileForwardResponse> getFilesForwardedByMe(){
        User currentUser = currentUserProvider.getCurrentUser();
        return fileForwardRepo.findBySenderOrderByForwardedAtDesc(currentUser)
                .stream()
                .map(fileForwardMapper::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<FileForwardResponse> getFilesForwardedToMe(){
        User currentUser = currentUserProvider.getCurrentUser();
        return fileForwardRepo.findByRecipientOrderByForwardedAtDesc(currentUser)
                .stream()
                .map(fileForwardMapper::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<FileForwardResponse> getUnreadNotifications(){
        User currentUser = currentUserProvider.getCurrentUser();
        return fileForwardRepo.findByRecipientAndIsReadFalseOrderByForwardedAtDesc(currentUser)
                .stream()
                .map(fileForwardMapper::mapToResponse)
                .collect(Collectors.toList());
    }

    public long getUnreadCount(){
        User currentUser =currentUserProvider.getCurrentUser();
        return fileForwardRepo.countByRecipientAndIsReadFalse(currentUser);
    }

    public FileForwardResponse openForwardedFile(long forwardId){
        User currentUser = currentUserProvider.getCurrentUser();

        FileForward forward = fileForwardRepo.findByIdAndRecipient(forwardId,currentUser).orElseThrow(() -> new ResourceNotFoundException("File not found"));

        if(!Boolean.TRUE.equals(forward.getIsRead())){
            forward.setIsRead(true);
            forward.setReadAt(LocalDateTime.now());
            fileForwardRepo.save(forward);
        }
        return fileForwardMapper.mapToResponse(forward);
    }
}
