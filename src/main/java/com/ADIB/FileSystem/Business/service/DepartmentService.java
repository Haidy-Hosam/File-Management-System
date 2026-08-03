package com.ADIB.FileSystem.Business.service;

import com.ADIB.FileSystem.Business.Model.Department;
import com.ADIB.FileSystem.Business.Model.File;
import com.ADIB.FileSystem.Business.Model.User;
import com.ADIB.FileSystem.Business.dto.request.DepartmentDeleteRequest;
import com.ADIB.FileSystem.Business.dto.request.DepartmentRequest;
import com.ADIB.FileSystem.Business.dto.response.DepartmentResponse;
import com.ADIB.FileSystem.Business.Exceptions.ResourceAlreadyExistsException;
import com.ADIB.FileSystem.Business.Exceptions.ResourceNotFoundException;
import com.ADIB.FileSystem.Business.service.Permissions.PagePermissionService;
import com.ADIB.FileSystem.DataAccess.repository.UserRepo;
import com.ADIB.FileSystem.mapper.DepartmentMapper;
import com.ADIB.FileSystem.DataAccess.repository.DepartmentRepo;
import com.ADIB.FileSystem.DataAccess.repository.FileRepo;
import com.ADIB.FileSystem.security.CurrentUserProvider;
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
    private final UserRepo userRepo;
    private final FileRepo fileRepo;
    private final DepartmentRepo departmentRepo;
    private final DepartmentMapper departmentMapper;
    private final CurrentUserProvider currentUserProvider;
    private final PagePermissionService pagePermissionService;

    private static final long MANAGER_ROLE_ID = 2L;

    public List<DepartmentResponse> getAllDepartments(){
        if (pagePermissionService.hasFullReadAccess("Departments")) {
            return departmentRepo.findAll().stream().map(this::mapToSummaryResponse).toList();
        }
        Department mine = currentUserProvider.getCurrentUser().getDepartment();
        return mine == null ? List.of() : List.of(mapToSummaryResponse(mine));
    }

    public DepartmentResponse getDepartmentDetails(Long id){
        if (!pagePermissionService.hasFullReadAccess("Departments")) {
            Department mine = currentUserProvider.getCurrentUser().getDepartment();
            if (mine == null || !mine.getId().equals(id)) {
                throw new ResourceNotFoundException("Department not found with id " + id);
            }
        }

        Department department = getDepartmentOrThrow(id);
        String managerName = getManagerName(id);
        List<User> employees = userRepo.findByDepartmentId(department.getId());
        long fileCount = fileRepo.countFilesByDepartment(department.getId());
        long storageUsed = getStorageUsed(id);
        List<File> files = fileRepo.findByDepartmentId(department.getId(), Pageable.unpaged())
                .getContent();
        return  departmentMapper.MapToDetailResponse(department,managerName,employees, fileCount, storageUsed, files);
    }

    public DepartmentResponse createDepartment(DepartmentRequest request){
        if (departmentRepo.existsByNameIgnoreCase(request.getName())) {
            throw new ResourceAlreadyExistsException("Department with name " + request.getName() + " already exists");
        }

        Department department = Department.builder()
                .name(request.getName())
                .isActive(request.getIsActive())
                .build();
        departmentRepo.save(department);
        return  departmentMapper.MapToDepartmentResponse(department);
    }

    public DepartmentResponse updateDepartment(Long id,DepartmentRequest request){
        Department department = getDepartmentOrThrow(id);

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

    @Transactional
    public void activateDepartment(Long id){
        Department department = getDepartmentOrThrow(id);
        if(department.getIsActive()){
            throw new IllegalStateException("Department is already inactive");
        }
        department.setIsActive(true);
        departmentRepo.save(department);
    }

    @Transactional
    public void deleteDepartment(Long id,List<DepartmentDeleteRequest.ReassignmentItem> reassignments){
        Department department = departmentRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Department not found with id " + id));

        if (!department.getIsActive()) {
            throw new IllegalStateException("Department is already inactive");
        }

        List<User> employees = userRepo.findByDepartmentId(id);

        if(!employees.isEmpty()){
            reassignEmployees(id,department,employees, reassignments);
        }

        department.setIsActive(false);
        departmentRepo.save(department);
    }

    private void reassignEmployees(Long departmentId, Department department, List<User> employees, List<DepartmentDeleteRequest.ReassignmentItem> reassignments){
        if(reassignments == null || reassignments.size() != employees.size()){
            throw new IllegalStateException("A target department is required to reassign existing employees");
        }

        Map<Long, Long> assignmentMap = reassignments.stream()
                .collect(Collectors.toMap(
                        DepartmentDeleteRequest.ReassignmentItem::getEmployeeId,
                        DepartmentDeleteRequest.ReassignmentItem::getTargetDepartmentId));

        for (User employee : employees) {
            Long targetId = assignmentMap.get(employee.getId());
            if (targetId == null) {
                throw new IllegalArgumentException("Missing target department for employee " + employee.getId());
            }
            reassignEmployee(employee, department, departmentId, targetId);
        }

        userRepo.saveAll(employees);
    }

    private void reassignEmployee(User employee, Department currentDepartment, Long currentDepartmentId, Long targetId) {
        if(targetId.equals(currentDepartmentId)){
            throw new IllegalArgumentException("Cannot reassign employees to current deleted department");
        }

        Department targetDepartment = getDepartmentOrThrow(targetId);
        if(!targetDepartment.getIsActive()){
            throw new IllegalArgumentException("Cannot reassign employees to an inactive department");
        }

        boolean isManager = employee.getRole() != null && employee.getRole().getId() == MANAGER_ROLE_ID;
        if (isManager && userRepo.findDepartmentManager(targetId).isPresent()) {
            throw new IllegalArgumentException(
                    "Target department '" + targetDepartment.getName() + "' already has a manager");
        }

        employee.setPreviousDepartment(currentDepartment);
        employee.setDepartment(targetDepartment);
    }

    private Department getDepartmentOrThrow(Long id) {
        return departmentRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id " + id));
    }

    private String getManagerName(Long departmentId) {
        return userRepo.findDepartmentManager(departmentId).map(User::getName).orElse(null);
    }

    private long getStorageUsed(Long departmentId) {
        Long totalStorage = fileRepo.getTotalDepartmentStorage(departmentId);
        return totalStorage != null ? totalStorage : 0L;
    }

    private DepartmentResponse mapToSummaryResponse(Department department) {
        String managerName = getManagerName(department.getId());
        long employeeCount = userRepo.countByDepartmentId(department.getId());
        long fileCount = fileRepo.countFilesByDepartment(department.getId());
        long storageUsed = getStorageUsed(department.getId());

        return departmentMapper.MapToSummaryResponse(department, managerName, employeeCount, fileCount, storageUsed);
    }

}






