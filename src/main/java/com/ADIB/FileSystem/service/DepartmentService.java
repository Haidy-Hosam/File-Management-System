package com.ADIB.FileSystem.service;

import com.ADIB.FileSystem.Model.Department;
import com.ADIB.FileSystem.Model.File;
import com.ADIB.FileSystem.Model.User;
import com.ADIB.FileSystem.dto.request.DepartmentDeleteRequest;
import com.ADIB.FileSystem.dto.request.DepartmentRequest;
import com.ADIB.FileSystem.dto.response.DepartmentResponse;
import com.ADIB.FileSystem.exception.ResourceAlreadyExistsException;
import com.ADIB.FileSystem.exception.ResourceNotFoundException;
import com.ADIB.FileSystem.mapper.DepartmentMapper;
import com.ADIB.FileSystem.mapper.FileMapper;
import com.ADIB.FileSystem.mapper.UserMapper;
import com.ADIB.FileSystem.repository.DepartmentRepo;
import com.ADIB.FileSystem.repository.FileRepo;
import com.ADIB.FileSystem.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    private final DepartmentRepo departmentRepo;
    private final DepartmentMapper departmentMapper;
    private final UserRepo userRepo;
    private final FileRepo fileRepo;


    public DepartmentResponse createDepartment(DepartmentRequest request){
        boolean isExist = departmentRepo.existsByNameIgnoreCase(request.getName());
        if(isExist){
            throw new ResourceAlreadyExistsException("Department with name " + request.getName() + " already exists");
        }
        Department department = Department.builder()
                .name(request.getName())
                .isActive(request.getIsActive())
                .build();
        departmentRepo.save(department);
        return  departmentMapper.MapToDepartmentResponse(department);
    }

    public List<DepartmentResponse> getAllDepartments(){
        List<Department> departments = departmentRepo.findAll();

        return  departments.stream()
                .map(dept -> {
                    String managerName = userRepo.findDepartmentManager(dept.getId()).map(User::getName).orElse(null);
                    long employeeCount = userRepo.countByDepartmentId(dept.getId());
                    long fileCount = fileRepo.countFilesByDepartment(dept.getId());

                    Long totalStorage = fileRepo.getTotalDepartmentStorage(dept.getId());
                    long storageUsed = totalStorage != null ? totalStorage : 0L;

                    return departmentMapper.MapToSummaryResponse(dept, managerName, employeeCount, fileCount, storageUsed);
                })
                .toList();

    }

    public DepartmentResponse getDepartmentDetails(Long id){
        Department department = departmentRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Department not found with id " + id));

        String managerName = userRepo.findDepartmentManager(department.getId()).map(User::getName).orElse(null);
        long employeeCount = userRepo.countByDepartmentId(department.getId());
        List<User> employees = userRepo.findByDepartmentId(department.getId());
        long fileCount = fileRepo.countFilesByDepartment(department.getId());
        Long totalStorage = fileRepo.getTotalDepartmentStorage(department.getId());
        long storageUsed = totalStorage != null ? totalStorage : 0L;
        List<File> files = fileRepo.findByDepartmentId(department.getId(), Pageable.unpaged())
                .getContent();
        return  departmentMapper.MapToDetailResponse(department,managerName,employees, fileCount, storageUsed, files);
    }


    @Transactional
    public void deleteDepartment(Long id,List<DepartmentDeleteRequest.ReassignmentItem> reassignments){
        Department department = departmentRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Department not found with id " + id));

        if (!department.getIsActive()) {
            throw new IllegalStateException("Department is already inactive");
        }

        List<User> employees = userRepo.findByDepartmentId(id);

        if(!employees.isEmpty()){
            if(reassignments == null || reassignments.size() != employees.size()){
                throw new IllegalStateException("A target department is required to reassign existing employees");
            }

            Map<Long, Long> assignmentMap = reassignments.stream()
                    .collect(Collectors.toMap(
                            DepartmentDeleteRequest.ReassignmentItem::getEmployeeId,
                            DepartmentDeleteRequest.ReassignmentItem::getTargetDepartmentId));

            for (User employee : employees) {
                Long targetId = assignmentMap.get(employee.getId());
                if(targetId == null){
                    throw new IllegalArgumentException("Missing target department for employee " + employee.getId());
                }
                if(targetId.equals(id)){
                    throw new IllegalArgumentException("Cannot reassign employees to the department being deleted");
                }
                Department targetDepartment = departmentRepo.findById(targetId)
                        .orElseThrow(() -> new ResourceNotFoundException("Target department not found: " + targetId));

                if (!targetDepartment.getIsActive()) {
                    throw new IllegalArgumentException("Cannot reassign employees to an inactive department");
                }
                boolean isManager = employee.getRole() != null && employee.getRole().getId() == 2 ;

                if(isManager){
                    boolean targetHasManager = userRepo.findDepartmentManager(targetId).isPresent();
                    if(targetHasManager){
                        throw new IllegalArgumentException(
                                "Target department '" + targetDepartment.getName() + "' already has a manager");
                    }
                }
                employee.setPreviousDepartment(department);
                employee.setDepartment(targetDepartment);
            }
            userRepo.saveAll(employees);
        }

        department.setIsActive(false);
        departmentRepo.save(department);
        }

    @Transactional
    public void activateDepartment(Long id){

        Department department = departmentRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Department not found with id " + id));
        if(department.getIsActive()){
            throw new IllegalStateException("Department is already inactive");
        }
        department.setIsActive(true);
        departmentRepo.save(department);
    }

    public DepartmentResponse updateDepartment(Long id,DepartmentRequest request){
        Department department = departmentRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Department not found with id " + id));

        boolean nameTaken = departmentRepo.findByNameIgnoreCase(request.getName())
                .filter(existing -> !existing.getId().equals(id))
                .isPresent();

        if(nameTaken){
            throw new ResourceAlreadyExistsException("Department with the same name already exists");
        }
        department.setName(request.getName());
        departmentRepo.save(department);
        return departmentMapper.MapToDepartmentResponse(department);
    }

    }






