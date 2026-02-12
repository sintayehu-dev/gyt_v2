package gyt.schedule_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieDto {
    private String uuid;
    private String title;
    private String description;
    private Integer duration;
    private LocalDate releaseDate;
    private String language;
    private String country;
    private String posterUrl;
    private String trailerUrl;
    private Boolean fallbackUsed;
}
