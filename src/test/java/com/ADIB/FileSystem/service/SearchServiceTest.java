package com.ADIB.FileSystem.service;

import com.ADIB.FileSystem.Model.File;
import com.ADIB.FileSystem.dto.request.FileSearchRequest;
import com.ADIB.FileSystem.repository.FileRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import java.util.List;

@ExtendWith(MockitoExtension.class)
public class SearchServiceTest {
    @Mock
    private FileRepo fileRepo;

    @InjectMocks
    private FileService fileService;

    @Test
    void shouldReturnFilesWhenSearchIsCalled() {
        FileSearchRequest request = new FileSearchRequest();
        File file1 = new File();
        File file2 = new File();
        List<File> files = List.of(file1, file2);

        when(fileRepo.findAll(any(Specification.class))).thenReturn(files);

        List<File> result = fileService.search(request);
        assertEquals(2, result.size());

        verify(fileRepo).findAll(any(Specification.class));


    }

}
