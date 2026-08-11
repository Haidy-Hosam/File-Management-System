package com.ADIB.FileSystem.DataAccess.repository;

import com.ADIB.FileSystem.Business.Model.SecurityLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SecurityLevelRepo extends JpaRepository<SecurityLevel, Long> {
    SecurityLevel findByName(String name);
    SecurityLevel findById(long id);
}
