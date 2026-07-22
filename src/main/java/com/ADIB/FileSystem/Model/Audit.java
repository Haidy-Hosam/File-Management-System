package com.ADIB.FileSystem.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class Audit {

    @CreatedDate
    @CreationTimestamp
    private LocalDateTime createdAt;

<<<<<<< HEAD
    //    @CreatedBy
=======
    @CreatedBy
>>>>>>> 1d0b70806fc82561d5fa81973667ed928b264a45
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @LastModifiedDate
    @UpdateTimestamp
    private LocalDateTime updatedAt;

<<<<<<< HEAD
    //    @LastModifiedBy
=======
    @LastModifiedBy
>>>>>>> 1d0b70806fc82561d5fa81973667ed928b264a45
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private User updatedBy;

}