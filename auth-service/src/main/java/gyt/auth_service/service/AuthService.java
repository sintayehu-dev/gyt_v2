package gyt.auth_service.service;

import gyt.auth_service.dto.AuthResponse;
import gyt.auth_service.dto.LoginRequest;
import gyt.auth_service.dto.RegisterRequest;
import gyt.auth_service.dto.RefreshTokenRequest;
import gyt.auth_service.dto.SuccessResponse;
import gyt.auth_service.dto.UserDto;

public interface AuthService {
    AuthResponse login(LoginRequest request);

    SuccessResponse register(RegisterRequest request);

    AuthResponse refreshToken(RefreshTokenRequest request);

    UserDto updateUser(String uuid, String email, String name);

    UserDto deleteUser(String uuid);
}
