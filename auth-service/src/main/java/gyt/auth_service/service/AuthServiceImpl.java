package gyt.auth_service.service;

import gyt.auth_service.dto.AuthResponse;
import gyt.auth_service.dto.LoginRequest;
import gyt.auth_service.dto.RegisterRequest;
import gyt.auth_service.dto.RefreshTokenRequest;
import gyt.auth_service.dto.SuccessResponse;
import gyt.auth_service.dto.UserDto;
import gyt.auth_service.model.Role;
import gyt.auth_service.model.User;
import gyt.auth_service.repository.UserRepository;
import gyt.auth_service.utils.JwtUtil;
import gyt.auth_service.mapper.UserMapper;
import gyt.auth_service.messaging.EventPublisher;
import gyt.auth_service.dto.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import gyt.auth_service.exception.CustomRuntimeException;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;
    private final EventPublisher eventPublisher;

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(
                        () -> new CustomRuntimeException("Invalid email or password", HttpStatus.UNAUTHORIZED));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new CustomRuntimeException("Invalid email or password", HttpStatus.UNAUTHORIZED);
        }

        user.setLastLogin(LocalDateTime.now());
        User savedUser = userRepository.save(user);

        String accessToken = jwtUtil.generateAccessToken(user.getUuid(), user.getEmail(), user.getName(),
                user.getRole());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUuid(), user.getEmail(), user.getName(),
                user.getRole());

        return new AuthResponse(userMapper.toDto(savedUser), accessToken, refreshToken);
    }

    @Override
    @Transactional
    public SuccessResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new CustomRuntimeException("Email already exists", HttpStatus.CONFLICT);
        }

        User user = userMapper.toEntity(request, passwordEncoder.encode(request.password()));

        User savedUser = userRepository.save(user);

        UserRegisteredEvent event = new UserRegisteredEvent();
        event.setUuid(savedUser.getUuid());
        event.setEmail(savedUser.getEmail());
        event.setName(savedUser.getName());
        event.setRole(savedUser.getRole().name());

        eventPublisher.publishUserRegisteredEvent(event);

        return new SuccessResponse("User registered successfully");
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.refreshToken();
        
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new CustomRuntimeException("Refresh token is required", HttpStatus.BAD_REQUEST);
        }

        if (!jwtUtil.validateToken(refreshToken)) {
            throw new CustomRuntimeException("Invalid or expired refresh token", HttpStatus.UNAUTHORIZED);
        }

        String email = jwtUtil.extractEmail(refreshToken);
        String uuid = jwtUtil.extractUuid(refreshToken);
        
        User user = userRepository.findByUuid(uuid)
                .orElseThrow(() -> new CustomRuntimeException("User not found", HttpStatus.NOT_FOUND));

        if (!user.getEmail().equals(email)) {
            throw new CustomRuntimeException("Token email mismatch", HttpStatus.UNAUTHORIZED);
        }

        String newAccessToken = jwtUtil.generateAccessToken(user.getUuid(), user.getEmail(), user.getName(),
                user.getRole());
        String newRefreshToken = jwtUtil.generateRefreshToken(user.getUuid(), user.getEmail(), user.getName(),
                user.getRole());

        return new AuthResponse(userMapper.toDto(user), newAccessToken, newRefreshToken);
    }

    @Override
    @Transactional
    public UserDto updateUser(String uuid, String email, String name) {
        User user = userRepository.findByUuid(uuid)
                .orElseThrow(
                        () -> new CustomRuntimeException("User not found for uuid: " + uuid, HttpStatus.NOT_FOUND));

        if (email != null && !email.equals(user.getEmail())) {
            if (userRepository.existsByEmail(email)) {
                throw new CustomRuntimeException("Email already exists", HttpStatus.CONFLICT);
            }
            user.setEmail(email);
        }

        if (name != null) {
            user.setName(name);
        }

        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Override
    @Transactional
    public UserDto deleteUser(String uuid) {
        User user = userRepository.findByUuid(uuid)
                .orElseThrow(
                        () -> new CustomRuntimeException("User not found for uuid: " + uuid, HttpStatus.NOT_FOUND));
        userRepository.delete(user);
        return userMapper.toDto(user);
    }
}
