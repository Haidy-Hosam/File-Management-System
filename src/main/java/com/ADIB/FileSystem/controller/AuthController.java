package com.ADIB.FileSystem.controller;

import com.ADIB.FileSystem.Model.RefreshToken;
import com.ADIB.FileSystem.dto.request.LoginRequest;
import com.ADIB.FileSystem.dto.request.RefreshTokenRequest;
import com.ADIB.FileSystem.dto.request.RegisterRequest;
import com.ADIB.FileSystem.dto.response.AuthResponse;
import com.ADIB.FileSystem.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest Request) {
        return ResponseEntity.ok(authService.register(Request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest Request) {
        return ResponseEntity.ok(authService.login(Request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshTokenRequest Request) {
        return ResponseEntity.ok(authService.refreshToken(Request));
    }

}
