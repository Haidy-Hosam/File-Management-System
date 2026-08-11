package com.ADIB.FileSystem.Business.Model;

import com.ADIB.FileSystem.Business.Enum.FILE_STATUS;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileDepartmentApproval {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id", nullable = false)
    private File file;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private User manager;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private FILE_STATUS status = FILE_STATUS.PENDING;

    private LocalDateTime decidedAt;

    @Builder.Default
    private Long currentApprovalOrder = 0L;


}
