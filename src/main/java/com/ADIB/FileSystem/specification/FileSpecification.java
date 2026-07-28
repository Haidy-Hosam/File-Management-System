package com.ADIB.FileSystem.specification;

import com.ADIB.FileSystem.Model.Department;
import com.ADIB.FileSystem.Model.File;
import com.ADIB.FileSystem.Model.FileType;
import com.ADIB.FileSystem.Model.User;
import com.ADIB.FileSystem.dto.request.FileSearchRequest;
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

            if (request.getOwner() != null && !request.getOwner().isBlank()) {
                Join<File, User> ownerJoin = root.join("createdBy");
                predicates.add(
                        criteriaBuilder.equal(
                                ownerJoin.get("name"),
                                request.getOwner()
                        )
                );
            }
            if (request.getDepartment() != null && !request.getDepartment().isBlank()) {
                Join<File, Department> departmentJoin = root.join("departments");
                predicates.add(
                        criteriaBuilder.equal(
                                departmentJoin.get("name"),
                                request.getDepartment()
                        )
                );
            }
            if (request.getStatus() != null) {
                predicates.add(
                        criteriaBuilder.equal(
                                root.get("status"),
                                request.getStatus()
                        )
                );
            }
            if (request.getCategory() != null && !request.getCategory().isBlank()) {

                Join<File, FileType> fileTypeJoin = root.join("fileType");
                predicates.add(
                        criteriaBuilder.equal(
                                fileTypeJoin.get("name"),
                                request.getCategory()
                        )
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

            if (request.getModifiedDate() != null) {

                LocalDateTime start = request.getModifiedDate().atStartOfDay();

                LocalDateTime end = request.getModifiedDate().atTime(LocalTime.MAX);

                predicates.add(
                        criteriaBuilder.between(
                                root.get("updatedAt"),
                                start,
                                end
                        )
                );
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }

                ;
    }
}
