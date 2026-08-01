package com.ADIB.FileSystem.DataAccess.repository;

import com.ADIB.FileSystem.Business.Model.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

//will be deleted !!!!!!!!!
@Repository
public interface PageRepo extends JpaRepository<Page, Long> {
    Optional<Page> findByPageName(String pageName);

}
