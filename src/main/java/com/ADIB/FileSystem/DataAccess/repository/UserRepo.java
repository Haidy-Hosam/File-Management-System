package com.ADIB.FileSystem.DataAccess.repository;

import com.ADIB.FileSystem.Business.Model.Department;
import com.ADIB.FileSystem.Business.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    @Query("SELECT u FROM users u WHERE u.department.id = :deptId AND u.role.id = 2")
    Optional<User> findDepartmentManager(@Param("deptId") Long deptId);
    Optional<User> findByEmail(String email);

    @Query("SELECT count(*) FROM users u WHERE u.department.id= :deptId AND u.role.id = 2 ")
    int ManagerExistsInDepartment(@Param("deptId")Long deptId);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

    long countByDepartmentId(Long id);

    List<User> findByDepartmentId(Long id);
    List<User> findByDepartmentInAndIdNot(Collection<Department> departments, Long excludedUserId);

    @Query("SELECT u FROM users u WHERE u.department IN :departments AND u.role.id <> 2 AND u.id <> :excludedUserId")
    List<User> findEmployeesByDepartmentInAndIdNot(@Param("departments") Collection<Department> departments, @Param("excludedUserId") Long excludedUserId);

}
