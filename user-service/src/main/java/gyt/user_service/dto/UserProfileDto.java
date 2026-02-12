package gyt.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto {
    private String uuid;
    private String email;
    private String fullName;
    private List<String> bookmarkedScheduleUuids;
    private LocalDateTime createdAt;
}
