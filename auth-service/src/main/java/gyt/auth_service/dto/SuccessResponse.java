package gyt.auth_service.dto;

import java.time.LocalDateTime;

public record SuccessResponse(
        int status,
        String message,
        LocalDateTime timestamp
) {
    public SuccessResponse(String message) {
        this(200, message, LocalDateTime.now());
    }
}
