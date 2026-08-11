package com.ADIB.FileSystem.Business.service;

import com.ADIB.FileSystem.Business.Exceptions.ResourceAlreadyExistsException;
import com.ADIB.FileSystem.Business.Exceptions.ResourceNotFoundException;
import com.ADIB.FileSystem.Business.Model.SecurityLevel;
import com.ADIB.FileSystem.Business.dto.response.SecurityLevelResponse;
import com.ADIB.FileSystem.DataAccess.repository.SecurityLevelRepo;
import com.ADIB.FileSystem.mapper.SecurityLevelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SecurityLevelService {
    private final SecurityLevelMapper securityLevelMapper;
    private final SecurityLevelRepo securityLevelRepo;

    public List<SecurityLevelResponse> getSecurityLevels(){
        List<SecurityLevel> securityLevels = securityLevelRepo.findAll();
        return  securityLevels.stream()
                .map(securityLevelMapper::mapToResponse)
                .sorted(Comparator.comparing(SecurityLevelResponse::getId))
                .toList();
    }

    public SecurityLevelResponse createSecurityLevel(String name){
        if(securityLevelRepo.findByName(name) != null){
            throw new ResourceAlreadyExistsException("Security Level already exists");
        }

        SecurityLevel securityLevel = new SecurityLevel();
        securityLevel.setName(name);
        securityLevelRepo.save(securityLevel);
        return securityLevelMapper.mapToResponse(securityLevel);
    }

    public SecurityLevelResponse updateSecurityLevel(long id, String name){
        SecurityLevel updatedSecLevel = securityLevelRepo.findById(id);

        updatedSecLevel.setName(name);
        securityLevelRepo.save(updatedSecLevel);
        return securityLevelMapper.mapToResponse(updatedSecLevel);
    }

    public SecurityLevelResponse deleteSecurityLevel(long id){
        SecurityLevel securityLevel = securityLevelRepo.findById(id);
        if(securityLevel == null){
            throw new ResourceNotFoundException("Security Level not found");
        }
        securityLevelRepo.delete(securityLevel);
        return securityLevelMapper.mapToResponse(securityLevel);
    }
}
