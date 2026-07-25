package com.ADIB.FileSystem.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentDeleteRequest {
    private List<ReassignmentItem> reassignments;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReassignmentItem {
        private Long employeeId;
        private Long targetDepartmentId;
    }
}
