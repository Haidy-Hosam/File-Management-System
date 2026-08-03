package com.ADIB.FileSystem.DataAccess.specification;

import com.ADIB.FileSystem.Business.Model.Department;
import com.ADIB.FileSystem.Business.Model.File;
import com.ADIB.FileSystem.Business.Model.FileType;
import com.ADIB.FileSystem.Business.Model.User;
import com.ADIB.FileSystem.Business.dto.request.FileSearchRequest;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class FileSpecification {
    public static Specification<File> search(FileSearchRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            boolean needsDistinct = false;

            if(request.getName() != null && !request.getName().isBlank()) {
                predicates.add(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + request.getName().toLowerCase() + "%")
                );
            }

            if (request.getOwners() != null && !request.getOwners().isEmpty()) {
                Join<File, User> ownerJoin = root.join("createdBy");
                predicates.add(ownerJoin.get("name").in(request.getOwners())
                );
            }

            if (request.getDepartments() != null && !request.getDepartments().isEmpty()) {
                Join<File, Department> departmentJoin = root.join("departments");
                predicates.add(
                        departmentJoin.get("name").in(request.getDepartments())
                );
                needsDistinct = true;
            }

            if (request.getStatuses() != null && !request.getStatuses().isEmpty()) {
                predicates.add(
                        root.get("status").in(request.getStatuses())
                );
            }

            if (request.getCategories() != null && !request.getCategories().isEmpty()) {
                Join<File, FileType> fileTypeJoin = root.join("fileType");
                predicates.add(
                        fileTypeJoin.get("name").in(request.getCategories())
                );
            }

            if (request.getFromDate() != null && request.getToDate() != null) {
                LocalDateTime from = request.getFromDate().atStartOfDay();
                LocalDateTime to = request.getToDate().atTime(LocalTime.MAX);
                predicates.add(
                        criteriaBuilder.between(
                                root.get("createdAt"),
                                from,
                                to
                        )
                );
            }
            if (request.getFromDate() != null) {
                predicates.add(
                        criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), request.getFromDate().atStartOfDay())
                );
            }
            if (request.getToDate() != null) {
                predicates.add(
                        criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), request.getToDate().atTime(LocalTime.MAX))
                );
            }

            if (request.getModifiedFrom() != null) {
                predicates.add(
                        criteriaBuilder.greaterThanOrEqualTo(root.get("updatedAt"), request.getModifiedFrom().atStartOfDay())
                );
            }
            if (request.getModifiedTo() != null) {
                predicates.add(
                        criteriaBuilder.lessThanOrEqualTo(root.get("updatedAt"), request.getModifiedTo().atTime(LocalTime.MAX))
                );
            }
            if (needsDistinct) {
                query.distinct(true);
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
