package com.ADIB.FileSystem.repository;

import com.ADIB.FileSystem.Model.FileType;
import com.ADIB.FileSystem.dto.response.FileResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileTypeRepo extends JpaRepository<FileType, Long> {

}
