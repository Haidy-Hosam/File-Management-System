package com.ADIB.FileSystem.repository;

import com.ADIB.FileSystem.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findByEmail(String email);
    //------------ SEARCH BY ID AND RETURN ONLY EMP NAME
    @Query(value = "Select * from User", nativeQuery = true)
    String returnName(Long id);

}
