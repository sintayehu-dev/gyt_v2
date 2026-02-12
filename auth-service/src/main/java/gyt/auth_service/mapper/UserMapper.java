package gyt.auth_service.mapper;

import gyt.auth_service.dto.RegisterRequest;
import gyt.auth_service.dto.UserDto;
import gyt.auth_service.model.Role;
import gyt.auth_service.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDto toDto(User user) {
        if (user == null) {
            return null;
        }
        return new UserDto(
                user.getUuid(),
                user.getEmail(),
                user.getName(),
                user.getRole(),
                user.getLastLogin());
    }

    public User toEntity(RegisterRequest request, String encodedPassword) {
        if (request == null) {
            return null;
        }

        Role userRole = Role.USER;
        if (request.role() != null && !request.role().isEmpty()) {
            try {
                userRole = Role.valueOf(request.role().toUpperCase());
            } catch (IllegalArgumentException e) {
                userRole = Role.USER;
            }
        }

        User user = User.builder()
                .email(request.email())
                .name(request.name())
                .password(encodedPassword)
                .role(userRole)
                .build();

        return user;
    }

}
