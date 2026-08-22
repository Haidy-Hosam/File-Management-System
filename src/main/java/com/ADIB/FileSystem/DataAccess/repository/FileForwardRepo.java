package com.ADIB.FileSystem.DataAccess.repository;

import com.ADIB.FileSystem.Business.Model.FileForward;
import com.ADIB.FileSystem.Business.Model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileForwardRepo extends JpaRepository<FileForward, Long> {
    Page<FileForward> findBySenderOrderByForwardedAtDesc(User sender, Pageable pageable);
    Page<FileForward> findByRecipientOrderByForwardedAtDesc(User recipient, Pageable pageable);
    Page<FileForward> findByRecipientAndIsReadFalseOrderByForwardedAtDesc(User recipient, Pageable pageable);
    long countByRecipientAndIsReadFalse(User recipient);

    Optional<FileForward> findByIdAndRecipient(Long id, User recipient);

}
