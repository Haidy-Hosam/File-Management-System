package com.ADIB.FileSystem.listener;

import com.ADIB.FileSystem.Model.FileForward;
import com.ADIB.FileSystem.event.FileForwardedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FileForwardNotificationListener {
    @EventListener
    public void onFileForwarded(FileForwardedEvent event){
        FileForward forward = event.getFileForward();
        log.info(
                "File '{}' (id={}) forwarded by '{}' to '{}'",
                forward.getFile().getName(),
                forward.getFile().getId(),
                forward.getSender().getUsername(),
                forward.getRecipient().getUsername()
        );
    }
}
