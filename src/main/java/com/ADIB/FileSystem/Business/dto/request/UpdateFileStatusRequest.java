package com.ADIB.FileSystem.Business.dto.request;

import com.ADIB.FileSystem.Business.Enum.FILE_STATUS;
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
