package gyt.movie_service.mapper;

import org.springframework.stereotype.Component;

import gyt.movie_service.dto.GenreDto;
import gyt.movie_service.dto.GenreRequest;
import gyt.movie_service.model.Genre;

@Component
public class GenreMapper {

    public GenreDto toDto(Genre genre) {
        if (genre == null) {
            return null;
        }

        return GenreDto.builder()
                .uuid(genre.getUuid())
                .name(genre.getName())
                .description(genre.getDescription())
                .createdAt(genre.getCreatedAt())
                .build();
    }

    public Genre toEntity(GenreRequest request) {
        if (request == null) {
            return null;
        }

        return Genre.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
    }
}
