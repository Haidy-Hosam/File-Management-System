package com.ADIB.FileSystem.listener;

import com.ADIB.FileSystem.Model.FileForward;
import com.ADIB.FileSystem.event.FileForwardedEvent;
import com.ADIB.FileSystem.event.FileUploadedEvent;
import com.ADIB.FileSystem.mapper.FileForwardMapper;
import com.ADIB.FileSystem.service.FileForwardService;
import com.ADIB.FileSystem.service.NotificationSseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileForwardNotificationListener {
    private final NotificationSseService sseService;
    private final FileForwardService fileForwardService;
    private final FileForwardMapper fileForwardMapper;

    @EventListener
    public void onFileForwarded(FileForwardedEvent event){
        FileForward forward = event.getFileForward();
        log.debug("Notification created: file={} sender={} recipient={} type={}",
                forward.getFile().getId(),
                forward.getSender().getUsername(),
                forward.getRecipient().getUsername(),
                forward.getType());

        sseService.push(forward.getRecipient().getId(), fileForwardMapper.mapToResponse(forward));


    }

    @EventListener
    public void onFileUploaded(FileUploadedEvent event){
        fileForwardService.notifyDepartmentsOnUpload(event.getFile(), event.getUploader());
    }
}
