package com.ADIB.FileSystem.Business.dto.request;


import com.ADIB.FileSystem.Business.Enum.FILE_STATUS;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileSearchRequest {
    private String name;

    private List<String> owners;
    private List<String> departments;
    private List<String> categories;
    private List<FILE_STATUS> statuses;

    private LocalDate fromDate;
    private LocalDate toDate;
    private LocalDate modifiedFrom;
    private LocalDate modifiedTo;

    private String sortBy;
    private String sortDir;

    private Integer page;
    private Integer size;
}
