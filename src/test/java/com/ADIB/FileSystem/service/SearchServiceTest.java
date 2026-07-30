package com.ADIB.FileSystem.service;

import com.ADIB.FileSystem.dto.request.FileSearchRequest;
import com.ADIB.FileSystem.mapper.FileMapper;
import com.ADIB.FileSystem.repository.FileRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;



import java.util.List;

@ExtendWith(MockitoExtension.class)
public class SearchServiceTest {
    @Mock
    private FileRepo fileRepo;
    @Mock
    private FileMapper fileMapper;

    @InjectMocks
    private FileService fileService;

    @Test
    void search_appliesPagination(){
        FileSearchRequest request = FileSearchRequest.builder()
                .page(1)
                .size(5)
                .build();
        when(fileRepo.findAll(any(Specification.class), any(Pageable.class))).thenReturn(Page.empty());

        fileService.search(request);

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(fileRepo).findAll(any(Specification.class), captor.capture());
        assertThat(captor.getValue().getPageNumber()).isEqualTo(1);
        assertThat(captor.getValue().getPageSize()).isEqualTo(5);
    }



}
