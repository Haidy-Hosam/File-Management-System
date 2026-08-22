package com.ADIB.FileSystem.Business.service.file;

import com.ADIB.FileSystem.Business.Model.Department;
import com.ADIB.FileSystem.Business.Model.File;
import com.ADIB.FileSystem.Business.Model.FileForward;
import com.ADIB.FileSystem.Business.Model.User;
import com.ADIB.FileSystem.Business.dto.request.ForwardFileRequest;
import com.ADIB.FileSystem.Business.dto.response.FileForwardResponse;
import com.ADIB.FileSystem.Business.event.FileForwardedEvent;
import com.ADIB.FileSystem.Business.Exceptions.ResourceNotFoundException;
import com.ADIB.FileSystem.Business.service.Permissions.PagePermissionService;
import com.ADIB.FileSystem.DataAccess.repository.UserRepo;
import com.ADIB.FileSystem.mapper.FileForwardMapper;
import com.ADIB.FileSystem.DataAccess.repository.FileForwardRepo;
import com.ADIB.FileSystem.DataAccess.repository.FileRepo;
import com.ADIB.FileSystem.security.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private final PagePermissionService pagePermissionService;

    public List<FileForwardResponse> forwardFile(long fileId, ForwardFileRequest request){
        User sender = currentUserProvider.getCurrentUser();

        File file = fileRepo.findById(fileId).orElseThrow(() -> new ResourceNotFoundException("File not found"));
        if(request.getRecipientIds() == null|| request.getRecipientIds().isEmpty()){
            throw new ResourceNotFoundException("Recipients not found");
        }

        List<FileForwardResponse> responses =new ArrayList<>();

        for(Long recipientId : request.getRecipientIds()) {
            if(recipientId.equals(sender.getId())){
                continue;
            }
            System.out.println("recipient :" + recipientId);
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

    public Page<FileForwardResponse> getSentForwards(Long userId ,int page, int size){
        User target = resolveTargetUser(userId);
        Pageable pageable = PageRequest.of(page, size);
        return fileForwardRepo.findBySenderOrderByForwardedAtDesc(target, pageable)
                .map(fileForwardMapper::mapToResponse);
    }

    public List<FileForwardResponse> getReceivedForwards(Long userId , int page, int size){
        User target = resolveTargetUser(userId);
        Pageable pageable = PageRequest.of(page, size);
        return fileForwardRepo.findByRecipientOrderByForwardedAtDesc(target, pageable)
                .stream()
                .map(fileForwardMapper::mapToResponse)
                .collect(Collectors.toList());
    }

    public Page<FileForwardResponse> getUnreadNotifications(int page, int size){
        User currentUser = currentUserProvider.getCurrentUser();
        Pageable pageable = PageRequest.of(page, size);
        return fileForwardRepo.findByRecipientAndIsReadFalseOrderByForwardedAtDesc(currentUser, pageable)
                .map(fileForwardMapper::mapToResponse);
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

    private User resolveTargetUser(Long userId){
        User currentUser = currentUserProvider.getCurrentUser();
        if(userId == null || userId.equals(currentUser.getId())){
            return currentUser;
        }
        User target = userRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (pagePermissionService.hasFullReadAccess("Users")) {
            return target;
        }

        Department myDept = currentUser.getDepartment();
        boolean sameDept = myDept != null && target.getDepartment() != null && myDept.getId().equals(target.getDepartment().getId());
        if(sameDept){
            return target;
        }
        throw new ResourceNotFoundException("User not found");
    }
}
