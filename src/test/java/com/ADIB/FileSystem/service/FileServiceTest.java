package com.ADIB.FileSystem.service;

import com.ADIB.FileSystem.Business.Enum.FILE_STATUS;
import com.ADIB.FileSystem.Business.Model.Department;
import com.ADIB.FileSystem.Business.Model.File;
import com.ADIB.FileSystem.Business.Model.FileType;
import com.ADIB.FileSystem.Business.Model.User;
import com.ADIB.FileSystem.Business.dto.request.FileRequest;
import com.ADIB.FileSystem.Business.dto.response.FileResponse;
import com.ADIB.FileSystem.Business.event.FileUploadedEvent;
import com.ADIB.FileSystem.Business.service.file.FileEncryptionService;
import com.ADIB.FileSystem.Business.service.file.FileService;
import com.ADIB.FileSystem.DataAccess.repository.*;
import com.ADIB.FileSystem.config.FileStorageProperties;
import com.ADIB.FileSystem.mapper.FileMapper;
import com.ADIB.FileSystem.security.CurrentUserProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class FileServiceTest {
    @Mock
    private FileStorageProperties storageProperties;
    @Mock
    private FileRepo fileRepo;
    @Mock
    private FileTypeRepo  fileTypeRepo;
    @Mock
    private FileForwardRepo fileForwardRepo;
    @Mock
    private DepartmentRepo  departmentRepo;
    @Mock
    private UserRepo  userRepo;
    @Mock
    private CurrentUserProvider currentUserProvider;
    @Mock
    private FileMapper fileMapper;
    @Mock
    private FileEncryptionService  fileEncryptionService;
    @Mock
    private org.springframework.context.ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private FileService fileService;

    @TempDir
    Path tempDir;

    private User uploader;
    private Department dept;
    private FileType fileType;

    @BeforeEach
    public void setUp() {
        Path uploaderPath = tempDir.resolve("uploads");
        Path trashPath = tempDir.resolve("trash");
        lenient().when(storageProperties.uploadPath()).thenReturn(uploaderPath);
        lenient().when(storageProperties.trashPath()).thenReturn(trashPath);

        uploader= User.builder()
                .id(1L)
                .name("Uploader")
                .build();
        dept = Department.builder()
                .id(10L)
                .name("Finance")
                .build();
        fileType = FileType.builder().id(20L).name("Invoice").build();

        lenient().when(currentUserProvider.getCurrentUser()).thenReturn(uploader);
    }

    @Test
    void uploadFile_success_encryptsAndSavesFile() throws Exception{
        Files.createDirectories(storageProperties.uploadPath());

        MultipartFile multipartFile = mock(MultipartFile.class);
        when(multipartFile.getOriginalFilename()).thenReturn("report.pdf");
        when(multipartFile.getBytes()).thenReturn("hello".getBytes(StandardCharsets.UTF_8));
        when(multipartFile.getSize()).thenReturn(5L);

        FileRequest request = mock(FileRequest.class);
        when(request.getDepartment_ids()).thenReturn(List.of(10L));
        when(request.getFileType_id()).thenReturn(20L);
        when(request.getFile()).thenReturn(multipartFile);

        when(departmentRepo.findAllById(List.of(10L))).thenReturn(List.of(dept));
        when(fileTypeRepo.findById(20L)).thenReturn(Optional.of(fileType));
        when(fileEncryptionService.encrypt(any())).thenReturn("encrypted".getBytes(StandardCharsets.UTF_8));

        when(fileRepo.save(any(File.class))).thenAnswer(inv -> inv.getArgument(0));
        when(fileMapper.mapToResponse(any(File.class))).thenReturn(mock(FileResponse.class));

        FileResponse response = fileService.uploadFile(request);

        assertThat(response).isNotNull();

        ArgumentCaptor<File> captor = ArgumentCaptor.forClass(File.class);
        verify(fileRepo).save(captor.capture());
        File saved = captor.getValue();
        assertThat(saved.getName()).isEqualTo("report.pdf");
        assertThat(saved.getExtension()).isEqualTo("pdf");
        assertThat(saved.getStatus()).isEqualTo(FILE_STATUS.PENDING);
        assertThat(saved.getIsDeleted()).isFalse();
        assertThat(saved.getDepartments()).containsExactly(dept);

        verify(eventPublisher).publishEvent(any(FileUploadedEvent.class));

        Path storedPath = Path.of(saved.getPath());
        assertThat(Files.exists(storedPath)).isTrue();

    }


}
