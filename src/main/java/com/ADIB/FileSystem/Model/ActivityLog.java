package com.ADIB.FileSystem.Model;

import com.ADIB.FileSystem.Enum.FILE_ACTION;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long a_id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FILE_ACTION action;

    @CreationTimestamp
    private LocalDateTime actionDate;

    @ManyToOne
    @JoinColumn(name="user_id",nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name="file_id",nullable = false)
    private File file;
}
