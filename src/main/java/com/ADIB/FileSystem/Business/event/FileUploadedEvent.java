package com.ADIB.FileSystem.Business.event;

import com.ADIB.FileSystem.Business.Model.File;
import com.ADIB.FileSystem.Business.Model.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class FileUploadedEvent  extends ApplicationEvent {
    private final File file;
    private final User uploader;

    public FileUploadedEvent(Object source, File file, User uploader) {
        super(source);
        this.file = file;
        this.uploader = uploader;
    }
}
