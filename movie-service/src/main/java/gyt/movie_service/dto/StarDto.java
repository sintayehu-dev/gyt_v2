package gyt.movie_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StarDto {
    private String uuid;
    private String name;
    private String biography;
    private LocalDateTime createdAt;
}
