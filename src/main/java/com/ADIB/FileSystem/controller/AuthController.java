package com.ADIB.FileSystem.controller;

import com.ADIB.FileSystem.Business.dto.request.LoginRequest;
import com.ADIB.FileSystem.Business.dto.request.RefreshTokenRequest;
import com.ADIB.FileSystem.Business.dto.request.RegisterRequest;
import com.ADIB.FileSystem.Business.dto.response.AuthResponse;
import com.ADIB.FileSystem.Business.service.AuthService;
import com.ADIB.FileSystem.Business.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {
    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest Request) {
        return ResponseEntity.ok(userService.createUser(Request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest Request) {
        return ResponseEntity.ok(authService.login(Request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshTokenRequest Request) {
        return ResponseEntity.ok(authService.refreshToken(Request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest Request) {
        authService.logout(Request);
        return ResponseEntity.noContent().build();
    }

}
