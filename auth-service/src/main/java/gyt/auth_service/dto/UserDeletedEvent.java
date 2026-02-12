package gyt.auth_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDeletedEvent {
    private String userUuid;
    private String email;
    private LocalDateTime deletedAt;
}
