package com.ADIB.FileSystem.controller;

import com.ADIB.FileSystem.dto.request.FileSearchRequest;
import com.ADIB.FileSystem.repository.FileRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class FileControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @Test
    @WithMockUser(authorities = "Files:READ")
void search_withInvalidDateRange_returns400() throws Exception {
    FileSearchRequest request = FileSearchRequest.builder()
            .fromDate(LocalDate.of(2026,6,1))
            .toDate(LocalDate.of(2026,1,1))
            .build();

    mockMvc.perform(post("/api/files/search")
            .contentType(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }
}