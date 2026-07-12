package com.ADIB.FileSystem.repository;

import com.ADIB.FileSystem.Model.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepo extends JpaRepository<Department, Long> {
}