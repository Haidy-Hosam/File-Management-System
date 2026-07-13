package com.ADIB.FileSystem.dto.request;

import com.ADIB.FileSystem.Enum.FILE_STATUS;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateFileStatusRequest {
    private FILE_STATUS status;

}
