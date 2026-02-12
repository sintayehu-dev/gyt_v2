package gyt.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisteredEvent implements Serializable {
    private String eventId;
    private String uuid;
    private String email;
    private String firstName;
    private String lastName;
    private String name;
    private String phoneNumber;
    private String role;
    private LocalDateTime timestamp;
}
