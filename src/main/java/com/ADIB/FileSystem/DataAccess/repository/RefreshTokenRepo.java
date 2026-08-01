package com.ADIB.FileSystem.DataAccess.repository;

import com.ADIB.FileSystem.Business.Model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepo extends JpaRepository<RefreshToken,String> {
    Optional<RefreshToken> findByTokenHash(String token);

}
