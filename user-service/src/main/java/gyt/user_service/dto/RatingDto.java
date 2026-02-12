package gyt.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingDto {
    private String id;
    private String movieUuid;
    private Integer rating;
    private String review;
    private LocalDateTime createdAt;
}
