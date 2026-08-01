package com.ADIB.FileSystem.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@ConfigurationProperties(prefix = "file-storage")
@Getter
@Setter
public class FileStorageProperties {
    private String uploadDir;
    private String trashDir;

    public Path uploadPath(){
        return Paths.get(uploadDir);
    }
    public Path trashPath(){
        return Paths.get(trashDir);
    }
}
