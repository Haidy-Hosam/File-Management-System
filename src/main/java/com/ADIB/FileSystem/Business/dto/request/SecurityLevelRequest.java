package com.ADIB.FileSystem.Business.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class SecurityLevelRequest {

    private long Id;

    @NotBlank(message = "Name is required")
    private String name;

}
