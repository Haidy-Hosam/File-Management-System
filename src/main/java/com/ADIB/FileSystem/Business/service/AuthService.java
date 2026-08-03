package com.ADIB.FileSystem.Business.service;

import com.ADIB.FileSystem.Business.Model.RefreshToken;
import com.ADIB.FileSystem.Business.Model.User;
import com.ADIB.FileSystem.Business.dto.request.LoginRequest;
import com.ADIB.FileSystem.Business.dto.request.RefreshTokenRequest;
import com.ADIB.FileSystem.Business.dto.response.AuthResponse;
import com.ADIB.FileSystem.Business.Exceptions.ResourceNotFoundException;
import com.ADIB.FileSystem.DataAccess.repository.*;
import com.ADIB.FileSystem.mapper.AuthMapper;
import com.ADIB.FileSystem.security.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepo userRepository;
    private final JWTUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepo refreshTokenRepo;
    private final AuthMapper authMapper;

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalStateException(
                        "Authenticated user not found in DB — should be unreachable"));

        rejectIfDeactivated(user);

        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        boolean rememberMe = request.isRememberMe();
        String accessToken = generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail(), rememberMe);

        storeRefreshToken(user, refreshToken, rememberMe);

        return authMapper.MapToResponse(user, accessToken, refreshToken);
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String tokenHash = jwtUtil.hashRefreshToken(request.getRefreshToken());

        RefreshToken refreshToken = refreshTokenRepo.findByTokenHash(tokenHash)
                .orElseThrow(() -> new ResourceNotFoundException("RefreshToken not found"));

        if(refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token has expired");
        }

        User user = refreshToken.getUser();
        rejectIfDeactivated(user);

        String accessToken = generateAccessToken(user);

        return authMapper.MapToResponse(user, accessToken, refreshToken.getTokenHash());
    }

    public void logout(RefreshTokenRequest request){
        String tokenHash = jwtUtil.hashRefreshToken(request.getRefreshToken());

        RefreshToken refreshToken = refreshTokenRepo.findByTokenHash(tokenHash)
                .orElseThrow(() -> new ResourceNotFoundException("Refresh Token not found"));
        User user = refreshToken.getUser();
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        refreshTokenRepo.delete(refreshToken);
    }

   private void rejectIfDeactivated(User user) {
        if(Boolean.TRUE.equals(user.getDeleted())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "This account has been deactivated. Contact an administrator.");
        }
   }

   private String generateAccessToken(User user) {
        Long deptId = user.getDepartment() != null ? user.getDepartment().getId() : null;
        return jwtUtil.generateAccessToken(user.getId(), user.getEmail(), user.getRole().getName(), deptId);
   }

   private void storeRefreshToken(User user, String refreshToken, boolean rememberMe) {
        LocalDateTime expiryDate = rememberMe ? LocalDateTime.now().plusDays(30) : LocalDateTime.now().plusDays(7);
        RefreshToken storedRefreshToken = RefreshToken.builder()
                .tokenHash(jwtUtil.hashRefreshToken(refreshToken))
                .expiryDate(expiryDate)
                .user(user)
                .build();
        refreshTokenRepo.save(storedRefreshToken);
   }
}
