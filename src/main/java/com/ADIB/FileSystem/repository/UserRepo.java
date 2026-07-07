package com.ADIB.FileSystem.repository;

import com.ADIB.FileSystem.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    //------------ SEARCH BY ID AND RETURN ONLY EMP NAME
    @Query(value = "SELECT username FROM users WHERE id = :id", nativeQuery = true)
    String returnName(@Param("id") Long id);

}
