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
public class FileForward {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "file_id", nullable = false)
    private File file;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="sender_id", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    private String message;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isRead = false;

    private LocalDateTime readAt;

    @CreationTimestamp
    private LocalDateTime forwardedAt;



}
