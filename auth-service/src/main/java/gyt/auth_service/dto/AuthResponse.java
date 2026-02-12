package gyt.auth_service.dto;

public record AuthResponse(
                UserDto user,
                String accessToken,
                String refreshToken) {
}
