package com.ADIB.FileSystem.mapper;

import com.ADIB.FileSystem.Business.Model.SecurityLevel;
import com.ADIB.FileSystem.Business.dto.response.SecurityLevelResponse;
import org.springframework.stereotype.Component;

@Component
public class SecurityLevelMapper {
    public SecurityLevelResponse mapToResponse(SecurityLevel securityLevel) {
        return SecurityLevelResponse.builder()
                .id(securityLevel.getId())
                .name(securityLevel.getName())
                .build();
    }
}
