package com.ADIB.FileSystem.repository;

import com.ADIB.FileSystem.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    User findByname(String name);
    User findByUsername(String username);
    Optional<User> findByEmail(String email);
    //------------ SEARCH BY ID AND RETURN ONLY EMP NAME
    @Query(value = "SELECT username FROM users WHERE id = :1 and name=:2", nativeQuery = true)
    String returnName(@Param("i") Long id, @Param("2") String name);


}
