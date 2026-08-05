package com.ADIB.FileSystem.service;

import com.ADIB.FileSystem.Business.Exceptions.ResourceNotFoundException;
import com.ADIB.FileSystem.Business.Model.Department;
import com.ADIB.FileSystem.Business.Model.RefreshToken;
import com.ADIB.FileSystem.Business.Model.Role;
import com.ADIB.FileSystem.Business.Model.User;
import com.ADIB.FileSystem.Business.dto.request.LoginRequest;
import com.ADIB.FileSystem.Business.dto.request.RefreshTokenRequest;
import com.ADIB.FileSystem.Business.dto.response.AuthResponse;
import com.ADIB.FileSystem.Business.service.AuthService;
import com.ADIB.FileSystem.DataAccess.repository.RefreshTokenRepo;
import com.ADIB.FileSystem.DataAccess.repository.UserRepo;
import com.ADIB.FileSystem.mapper.AuthMapper;
import com.ADIB.FileSystem.security.JWTUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.*;



import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AuthServiceTest {

    @Mock
    private UserRepo userRepo;
    @Mock
    private JWTUtil jwtUtil;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private RefreshTokenRepo  refreshTokenRepo;
    @Mock
    private AuthMapper  authMapper;

    @InjectMocks
    private AuthService authService;

    private User activeUser;
    private Role role ;

    @BeforeEach
    void setUp() {
        role = Role.builder()
                .id(1L)
                .name("EMPLOYEE")
                .build();
        activeUser = User.builder()
                .id(1L)
                .email("jane@b.com")
                .deleted(false)
                .role(role)
                .department(Department.builder().id(5L).build())
                .build();
    }

    @Test
    void login_success_setsLastLoginAndStoreRefreshToken() {
        LoginRequest request = mock(LoginRequest.class);
        when(request.getEmail()).thenReturn("jane@b.com");
        when(request.getPassword()).thenReturn("secret");
        when(request.isRememberMe()).thenReturn(false);

        when(userRepo.findByEmail("jane@b.com")).thenReturn(Optional.of(activeUser));
        when(jwtUtil.generateAccessToken(1L,"jane@b.com","EMPLOYEE",5L)).thenReturn("access-token");
        when(jwtUtil.generateRefreshToken("jane@b.com",false)).thenReturn("refresh-token");
        when(jwtUtil.hashRefreshToken("refresh-token")).thenReturn("hashed-refresh");
        when(authMapper.MapToResponse(eq(activeUser), eq("access-token"), eq("refresh-token")))
                .thenReturn(mock(AuthResponse.class));

        authService.login(request);

        verify(authenticationManager).authenticate(any());
        assertThat(activeUser.getLastLogin()).isNotNull();
        verify(userRepo).save(activeUser);

        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepo).save(captor.capture());
        assertThat(captor.getValue().getTokenHash()).isEqualTo("hashed-refresh");
        assertThat(captor.getValue().getUser()).isEqualTo(activeUser);

        assertThat(captor.getValue().getExpiryDate()).isBefore(LocalDateTime.now().plusDays(8));
        assertThat(captor.getValue().getExpiryDate()).isAfter(LocalDateTime.now().plusDays(6));
    }

    @Test
    void login_rememberMe_setsThirtyDayExpiry(){
        LoginRequest request = mock(LoginRequest.class);
        when(request.getEmail()).thenReturn("jane@b.com");
        when(request.isRememberMe()).thenReturn(true);

        when(userRepo.findByEmail("jane@b.com")).thenReturn(Optional.of(activeUser));
        when(jwtUtil.generateRefreshToken("jane@b.com", true)).thenReturn("refresh-token");
        when(jwtUtil.hashRefreshToken("refresh-token")).thenReturn("hashed-refresh");
        when(authMapper.MapToResponse(any(),any(),any())).thenReturn(mock(AuthResponse.class));

        authService.login(request);

        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepo).save(captor.capture());
        assertThat(captor.getValue().getExpiryDate().isAfter(LocalDateTime.now().plusDays(29)));
        assertThat(captor.getValue().getExpiryDate().isBefore(LocalDateTime.now().plusDays(31)));
    }

    @Test
    void login_deactivatedUser_throwsUnauthorized(){
        User deactivated = User.builder().id(1L).email("jane@b.com").deleted(true).build();
        LoginRequest request = mock(LoginRequest.class);
        when(request.getEmail()).thenReturn("jane@b.com");

        when(userRepo.findByEmail("jane@b.com")).thenReturn(Optional.of(deactivated));

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("statusCode", HttpStatus.UNAUTHORIZED);
        verify(refreshTokenRepo, never()).save(any());
    }

    @Test
    void login_userNotFoundAfterAuth_throwsIllegalState(){
        LoginRequest request = mock(LoginRequest.class);
        when(request.getEmail()).thenReturn("ghost@b.com");
        when(userRepo.findByEmail("ghost@b.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void refreshToken_valid_returnsNewAccessToken(){
        RefreshTokenRequest request = mock(RefreshTokenRequest.class);
        when(request.getRefreshToken()).thenReturn("raw-token");
        when(jwtUtil.hashRefreshToken("raw-token")).thenReturn("hashed-refresh");

        RefreshToken stored = RefreshToken.builder()
                .tokenHash("hashed-token")
                .expiryDate(LocalDateTime.now().plusDays(1))
                .user(activeUser)
                .build();
        when(refreshTokenRepo.findByTokenHash("hashed-token")).thenReturn(Optional.of(stored));
        when(jwtUtil.generateAccessToken(anyLong(), anyString(),anyString(), any())).thenReturn("new-access");
        when(authMapper.MapToResponse(activeUser,"new-access","hashed-token")).thenReturn(mock(AuthResponse.class));

        authService.refreshToken(request);
        verify(jwtUtil).generateAccessToken(1L, "jane@b.com","EMPLOYEE",5L);
    }

    @Test
    void refreshToken_expired_throwsUnauthorized(){
        RefreshTokenRequest request = mock(RefreshTokenRequest.class);
        when(request.getRefreshToken()).thenReturn("raw-token");
        when(jwtUtil.hashRefreshToken("raw-token")).thenReturn("hashed-refresh");

        RefreshToken expired = RefreshToken.builder()
                .tokenHash("hashed-token")
                .expiryDate(LocalDateTime.now().minusDays(1))
                .user(activeUser)
                .build();
        when(refreshTokenRepo.findByTokenHash("hashed-token")).thenReturn(Optional.of(expired));

        assertThatThrownBy(() -> authService.refreshToken(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("statusCode", HttpStatus.UNAUTHORIZED);
    }

    @Test
    void refreshToken_notFound_throwsResourceNotFound(){
        RefreshTokenRequest request = mock(RefreshTokenRequest.class);
        when(request.getRefreshToken()).thenReturn("raw-token");
        when(jwtUtil.hashRefreshToken("raw-token")).thenReturn("hashed-refresh");
        when(refreshTokenRepo.findByTokenHash("hashed-token")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.refreshToken(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void logout_deletesRefreshTokenAndUpdatesLastLogin(){
        RefreshTokenRequest request = mock(RefreshTokenRequest.class);
        when(request.getRefreshToken()).thenReturn("raw-token");
        when(jwtUtil.hashRefreshToken("raw-token")).thenReturn("hashed-refresh");

        RefreshToken stored = RefreshToken.builder()
                .tokenHash("hashed-token")
                .user(activeUser)
                .build();
        when(refreshTokenRepo.findByTokenHash("hashed-token")).thenReturn(Optional.of(stored));

        authService.logout(request);
        verify(userRepo).save(activeUser);
        verify(refreshTokenRepo).delete(stored);
        assertThat(activeUser.getLastLogin()).isNotNull();
    }

    @Test
    void logout_tokenNotFound_throwsResourceNotFound(){
        RefreshTokenRequest request = mock(RefreshTokenRequest.class);
        when(request.getRefreshToken()).thenReturn("raw-token");
        when(jwtUtil.hashRefreshToken("raw-token")).thenReturn("hashed-refresh");
        when(refreshTokenRepo.findByTokenHash("hashed-token")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.logout(request))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(refreshTokenRepo, never()).delete(any());
    }
}
