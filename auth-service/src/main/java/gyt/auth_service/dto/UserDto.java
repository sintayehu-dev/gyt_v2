package gyt.auth_service.dto;

import java.time.LocalDateTime;

public record UserDto(
        String uuid,
        String email,
        String name,
        Object role,
        LocalDateTime lastLogin) {
}
