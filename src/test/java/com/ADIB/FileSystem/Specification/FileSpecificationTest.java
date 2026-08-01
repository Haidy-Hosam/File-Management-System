package com.ADIB.FileSystem.Specification;

import com.ADIB.FileSystem.Business.Enum.FILE_STATUS;
import com.ADIB.FileSystem.Business.Model.File;
import com.ADIB.FileSystem.Business.Model.FileType;
import com.ADIB.FileSystem.Business.Model.Role;
import com.ADIB.FileSystem.Business.Model.User;
import com.ADIB.FileSystem.Business.dto.request.FileSearchRequest;
import com.ADIB.FileSystem.DataAccess.repository.FileRepo;
import com.ADIB.FileSystem.security.JpaAuditConfig;
import com.ADIB.FileSystem.DataAccess.specification.FileSpecification;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@ActiveProfiles("test")
@Import(JpaAuditConfig.class)
class FileSpecificationTest {
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private FileRepo fileRepo;

    @Test
    void searchByStatus_returnsMatchingFiles(){
        Role role = Role.builder()
                .name("EMPLOYEE")
                .build();
        testEntityManager.persist(role);

        User user = User.builder()
                .name("Test User")
                .email("user@test.com")
                .password("password")
                .role(role)
                .deleted(false)
                .build();
        testEntityManager.persist(user);

        FileType fileType = FileType.builder()
                .name("PDF FILE")
                .build();
        testEntityManager.persist(fileType);

        File file = File.builder()
                .name("Invoice_2026.pdf")
                .extension("pdf")
                .path("test/path.pdf")
                .status(FILE_STATUS.APPROVED)
                .isDeleted(false)
                .fileType(fileType)
                .size(100L)
                .build();

        file.setCreatedBy(user);
        file.setUpdatedBy(user);
        file.setCreatedAt(LocalDateTime.now());
        file.setUpdatedAt(LocalDateTime.now());


        testEntityManager.persist(file);
        testEntityManager.flush();

        FileSearchRequest request = FileSearchRequest.builder()
                .status(FILE_STATUS.APPROVED)
                .build();
        Page<File> result = fileRepo.findAll(FileSpecification.search(request), PageRequest.of(0,10));

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void searchByStatus_returnsNoMatchingFiles(){
        FileSearchRequest req = FileSearchRequest.builder().status(FILE_STATUS.PENDING).build();
        Page<File> result = fileRepo.findAll(FileSpecification.search(req), PageRequest.of(0,10));
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }

    @Test
    void search_fromDateOnly_matchesFromDateToNow(){
        File file = File.builder()
                .name("test.pdf").extension("pdf")
                .status(FILE_STATUS.PENDING)
                .isDeleted(false)
                .size(50L)
                .build();

        testEntityManager.persist(file);
        testEntityManager.flush();

        FileSearchRequest request = FileSearchRequest.builder()
                .fromDate(java.time.LocalDate.now().minusDays(1))
                .build();
        Page<File> result = fileRepo.findAll(FileSpecification.search(request), PageRequest.of(0,10));
        assertThat(result.getContent()).hasSize(1);
    }
}
