package com.ADIB.FileSystem.dto.request;


import com.ADIB.FileSystem.Enum.FILE_STATUS;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileSearchRequest {
    private String owner;

    private String department;

    private String category;

    private FILE_STATUS status;

    private LocalDate fromDate;

    private LocalDate toDate;

    private LocalDate modifiedDate;
}
