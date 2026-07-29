package com.ADIB.FileSystem.controller;

import com.ADIB.FileSystem.repository.FileRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class FileControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FileRepo fileRepo;

    @Autowired
    private ObjectMapper objectMapper;
}
