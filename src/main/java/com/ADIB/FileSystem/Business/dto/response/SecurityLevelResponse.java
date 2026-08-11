package com.ADIB.FileSystem.Business.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecurityLevelResponse {
    @Getter
    private long id;
    private String name;
}
