package com.ADIB.FileSystem.repository;

import com.ADIB.FileSystem.Model.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepo extends JpaRepository<File, Long> {
}