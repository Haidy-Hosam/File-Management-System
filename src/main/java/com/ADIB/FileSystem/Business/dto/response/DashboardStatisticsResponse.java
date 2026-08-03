package com.ADIB.FileSystem.Business.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatisticsResponse {

    private long totalDocuments;
    private long pendingReviews;
    private long approvedArchives;
    private long activeDepartments;

}