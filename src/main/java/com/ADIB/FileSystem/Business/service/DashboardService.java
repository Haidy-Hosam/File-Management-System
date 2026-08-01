package com.ADIB.FileSystem.Business.service;

import com.ADIB.FileSystem.Business.Enum.FILE_STATUS;
import com.ADIB.FileSystem.Business.Model.Department;
import com.ADIB.FileSystem.Business.Model.User;
import com.ADIB.FileSystem.Business.dto.response.DashboardStatisticsResponse;
import com.ADIB.FileSystem.DataAccess.repository.DepartmentRepo;
import com.ADIB.FileSystem.DataAccess.repository.FileRepo;
import com.ADIB.FileSystem.DataAccess.repository.UserRepo;
import com.ADIB.FileSystem.security.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final FileRepo fileRepo;
    private final DepartmentRepo departmentRepo;
    private final UserRepo userRepo;
    private final CurrentUserProvider currentUserProvider;

    public DashboardStatisticsResponse getStatistics() {
        User currentUser = currentUserProvider.getCurrentUser();

        if (currentUser.getRole().getName().equals("ADMIN")) {
            return DashboardStatisticsResponse.builder()
                    .totalDocuments(fileRepo.count())
                    .pendingReviews(fileRepo.countByStatus(FILE_STATUS.PENDING))
                    .approvedArchives(fileRepo.countByStatus(FILE_STATUS.APPROVED))
                    .activeDepartments(departmentRepo.countByIsActiveTrue())
                    .build();
        }


        Department department = currentUser.getDepartment();

        return DashboardStatisticsResponse.builder()
                .totalDocuments(fileRepo.countByDepartmentsContains(department))
                .pendingReviews(
                        fileRepo.countByDepartmentsContainsAndStatus(
                                department,
                                FILE_STATUS.PENDING
                        )
                )
                .approvedArchives(
                        fileRepo.countByDepartmentsContainsAndStatus(
                                department,
                                FILE_STATUS.APPROVED
                        )
                )
                .activeDepartments(departmentRepo.countByIsActiveTrue())
                .build();
    }
}
