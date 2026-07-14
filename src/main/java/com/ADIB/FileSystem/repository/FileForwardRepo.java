package com.ADIB.FileSystem.repository;

import com.ADIB.FileSystem.Model.File;
import com.ADIB.FileSystem.Model.FileForward;
import com.ADIB.FileSystem.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileForwardRepo extends JpaRepository<FileForward, Long> {
    List<FileForward> findBySenderOrderByForwardedAtDesc(User sender);
    List<FileForward> findByRecipientOrderByForwardedAtDesc(User recipient);
    List<FileForward> findByRecipientAndIsReadFalseOrderByForwardedAtDesc(User recipient);
    long countByRecipientAndIsReadFalse(User recipient);

    Optional<FileForward> findByIdAndRecipient(Long id, User recipient);

}
