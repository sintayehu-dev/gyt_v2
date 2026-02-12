package gyt.movie_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieDto {
    private String uuid;
    private String title;
    private String description;
    private Integer durationMinutes;
    private LocalDate releaseDate;
    private String posterUrl;
    private String trailerUrl;
    private String language;
    private Boolean isActive;
    private List<GenreDto> genres;
    private List<DirectorDto> directors;
    private List<StarDto> stars;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
