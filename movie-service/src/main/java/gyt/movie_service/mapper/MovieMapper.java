package gyt.movie_service.mapper;

import gyt.movie_service.dto.DirectorDto;
import gyt.movie_service.dto.GenreDto;
import gyt.movie_service.dto.MovieCreateRequest;
import gyt.movie_service.dto.MovieDto;
import gyt.movie_service.dto.StarDto;
import gyt.movie_service.model.Director;
import gyt.movie_service.model.Genre;
import gyt.movie_service.model.Movie;
import gyt.movie_service.model.Star;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MovieMapper {

    private final GenreMapper genreMapper;
    private final DirectorMapper directorMapper;
    private final StarMapper starMapper;

    public MovieDto toDto(Movie movie) {
        if (movie == null) {
            return null;
        }

        return MovieDto.builder()
                .uuid(movie.getUuid())
                .title(movie.getTitle())
                .description(movie.getDescription())
                .durationMinutes(movie.getDurationMinutes())
                .releaseDate(movie.getReleaseDate())
                .posterUrl(movie.getPosterUrl())
                .trailerUrl(movie.getTrailerUrl())
                .language(movie.getLanguage())
                .isActive(movie.getIsActive())
                .genres(mapGenresToDto(movie.getGenres()))
                .directors(mapDirectorsToDto(movie.getDirectors()))
                .stars(mapStarsToDto(movie.getStars()))
                .createdAt(movie.getCreatedAt())
                .updatedAt(movie.getUpdatedAt())
                .build();
    }

    public Movie toEntity(MovieCreateRequest request) {
        if (request == null) {
            return null;
        }

        return Movie.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .durationMinutes(request.getDurationMinutes())
                .releaseDate(request.getReleaseDate())
                .trailerUrl(request.getTrailerUrl())
                .language(request.getLanguage())
                .isActive(true)
                .genres(new ArrayList<>())
                .directors(new ArrayList<>())
                .stars(new ArrayList<>())
                .build();
    }

    private List<GenreDto> mapGenresToDto(List<Genre> genres) {
        if (genres == null)
            return new ArrayList<>();
        return genres.stream()
                .map(genreMapper::toDto)
                .collect(Collectors.toList());
    }

    private List<DirectorDto> mapDirectorsToDto(List<Director> directors) {
        if (directors == null)
            return new ArrayList<>();
        return directors.stream()
                .map(directorMapper::toDto)
                .collect(Collectors.toList());
    }

    private List<StarDto> mapStarsToDto(List<Star> stars) {
        if (stars == null)
            return new ArrayList<>();
        return stars.stream()
                .map(starMapper::toDto)
                .collect(Collectors.toList());
    }
}
