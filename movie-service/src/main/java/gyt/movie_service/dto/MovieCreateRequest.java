package gyt.movie_service.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieCreateRequest {
    private String title;
    private String description;
    private Integer durationMinutes;
    private LocalDate releaseDate;
    private String trailerUrl;
    private String language;
    private List<String> genreUuids;
    private List<String> directorUuids;
    private List<String> starUuids;
}
