package com.ADIB.FileSystem.controller;

import com.ADIB.FileSystem.dto.request.LoginRequest;
import com.ADIB.FileSystem.dto.request.PageRequest;
import com.ADIB.FileSystem.dto.request.RefreshTokenRequest;
import com.ADIB.FileSystem.dto.response.AuthResponse;
import com.ADIB.FileSystem.dto.response.PageResponse;
import com.ADIB.FileSystem.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
//@PreAuthorize("@permissionService.hasPage('Login')")
public class AuthController {
    private final AuthService authService;
//    @PostMapping("/register")
//    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest Request) {
//        return ResponseEntity.ok(authService.register(Request));
//    }

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

    //will be deleted !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1
//    @PostMapping("/pages")
//    public ResponseEntity<PageResponse> addPage(@RequestBody PageRequest Request) {
//        return ResponseEntity.ok((authService.addPage(Request)));
//    }
}
