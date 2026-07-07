package com.ADIB.FileSystem.Model;

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
public class FileAccess {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "file_id", nullable = false)
    private File file;

    @Column(nullable = false)
    private boolean canRead;

    @Column(nullable = false)
    private boolean canWrite;

    @Column(nullable = false)
    private boolean canDelete;

    @Column(nullable = false)
    private boolean canDownload;

    @CreationTimestamp
    private LocalDateTime grantedDate;
}

