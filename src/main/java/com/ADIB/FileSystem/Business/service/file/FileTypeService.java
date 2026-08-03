package com.ADIB.FileSystem.Business.service.file;

import com.ADIB.FileSystem.Business.Model.FileType;
import com.ADIB.FileSystem.Business.dto.request.FileTypeRequest;
import com.ADIB.FileSystem.Business.dto.response.FileTypeResponse;
import com.ADIB.FileSystem.Business.Exceptions.ResourceNotFoundException;
import com.ADIB.FileSystem.mapper.FileTypeMapper;
import com.ADIB.FileSystem.DataAccess.repository.FileTypeRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileTypeService {
    private final FileTypeRepo fileTypeRepo;
    private final FileTypeMapper fileTypeMapper;

    public FileTypeResponse create(FileTypeRequest request){
        FileType fileType = FileType.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
        FileType savedType = fileTypeRepo.save(fileType);
        return fileTypeMapper.mapToResponse(savedType);
    }

    public List<FileTypeResponse> getAll(){
        return fileTypeRepo.findAll().stream().map(fileTypeMapper::mapToResponse).collect(Collectors.toList());
    }

    public FileTypeResponse update(Long id, FileTypeRequest request){
        FileType type = fileTypeRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("File type not found ."));
        type.setName(request.getName());
        type.setDescription(request.getDescription());
        fileTypeRepo.save(type);
        return fileTypeMapper.mapToResponse(type);
    }

    public void delete(Long id){
        fileTypeRepo.deleteById(id);
    }
}
