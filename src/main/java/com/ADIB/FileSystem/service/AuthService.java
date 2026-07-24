package com.ADIB.FileSystem.service;

import com.ADIB.FileSystem.Model.*;
import com.ADIB.FileSystem.dto.request.LoginRequest;
import com.ADIB.FileSystem.dto.request.PageRequest;
import com.ADIB.FileSystem.dto.request.RefreshTokenRequest;
import com.ADIB.FileSystem.dto.request.RegisterRequest;
import com.ADIB.FileSystem.dto.response.AuthResponse;
import com.ADIB.FileSystem.dto.response.PageResponse;
import com.ADIB.FileSystem.exception.ResourceAlreadyExistsException;
import com.ADIB.FileSystem.exception.ResourceNotFoundException;
import com.ADIB.FileSystem.repository.*;
import com.ADIB.FileSystem.security.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepo userRepository;
    private final RoleRepo roleRepository;
    private final DepartmentRepo departmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepo refreshTokenRepo;
    private final PageRepo  pageRepo;  //will be deletet!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1

//    public AuthResponse register(RegisterRequest request) {
//        if (userRepository.existsByEmail(request.getEmail())) {
//            throw new ResourceAlreadyExistsException("Email already exists");
//        }
//
//        Role role = roleRepository.findById(request.getRoleId())
//                .orElseThrow(() -> new ResourceNotFoundException("Role not found with ID: " + request.getRoleId()));
//
//        Department department = null;
//        if (request.getDepartmentId() != null) {
//            department = departmentRepository.findById(request.getDepartmentId())
//                    .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + request.getDepartmentId()));
//        }
//
//        User user = User.builder()
//                .name(request.getName())
//                .username(request.getUsername())
//                .email(request.getEmail())
//                .password(passwordEncoder.encode(request.getPassword()))
//                .role(role)
//                .department(department)
//                .deleted(request.isDeleted())
//                .build();
//
//        userRepository.save(user);
//
//        return AuthResponse.builder()
//                .name(user.getUsername())
//                .email(user.getEmail())
//                .role(user.getRole().getName())
//                .departmentName(user.getDepartment().getName())
//                .isDeleted(false)
//                .build();
//    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()));


        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Long deptId = user.getDepartment() != null ? user.getDepartment().getId() : null;

        boolean rememberMe = Boolean.TRUE.equals(request.isRememberMe());
        String accessToken = jwtUtil.generateAccessToken(
                user.getId(),
                user.getEmail(),
                user.getRole().getName(),
                deptId
        );
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail(), rememberMe);

        LocalDateTime expiryDate = request.isRememberMe()? LocalDateTime.now().plusDays(30): LocalDateTime.now().plusDays(7);
        RefreshToken storedRefreshToken = RefreshToken.builder()
                .token(refreshToken)
                .expiryDate(expiryDate)
                .user(user)
                .build();

        refreshTokenRepo.save(storedRefreshToken);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .name(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().getName())
                .departmentName(user.getDepartment().getName())
                .isDeleted(false)
                .build();
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepo.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new ResourceNotFoundException("RefreshToken not found with ID: " + request.getRefreshToken()));

        if(refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("RefreshToken has expired");
        }

        String email = jwtUtil.extractEmail(refreshToken.getToken());
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String accessToken = jwtUtil.generateAccessToken(
                user.getId(),
                user.getEmail(),
                user.getRole().getName(),
                user.getDepartment().getId());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .name(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().getName())
                .departmentName(user.getDepartment().getName())
                .isDeleted(false)
                .build();
    }
    public void logout(RefreshTokenRequest request){

        RefreshToken refreshToken = refreshTokenRepo
                .findByToken(request.getRefreshToken())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Refresh Token not found"));

        refreshTokenRepo.delete(refreshToken);
    }

    //will be deleted !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//    public PageResponse addPage (PageRequest request){
//        Page page = Page.builder()
//                .pageName(request.getPageName())
//                .route(request.getRoute())
//                .build();
//
//        pageRepo.save(page);
//
//        return PageResponse.builder()
//                .name(page.getPageName())
//                .route(page.getRoute())
//                .build();
//    }
}
