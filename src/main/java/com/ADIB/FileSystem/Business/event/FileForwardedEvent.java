package com.ADIB.FileSystem.Business.event;


import com.ADIB.FileSystem.Business.Model.FileForward;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class FileForwardedEvent extends ApplicationEvent {
    private final FileForward fileForward;
    public FileForwardedEvent(Object source, FileForward fileForward) {
        super(source);
        this.fileForward = fileForward;
    }
}
