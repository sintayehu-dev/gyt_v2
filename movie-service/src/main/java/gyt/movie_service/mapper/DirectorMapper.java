package gyt.movie_service.mapper;

import org.springframework.stereotype.Component;

import gyt.movie_service.dto.DirectorDto;
import gyt.movie_service.dto.DirectorRequest;
import gyt.movie_service.model.Director;

@Component
public class DirectorMapper {

    public DirectorDto toDto(Director director) {
        if (director == null) {
            return null;
        }

        return DirectorDto.builder()
                .uuid(director.getUuid())
                .name(director.getName())
                .biography(director.getBiography())
                .createdAt(director.getCreatedAt())
                .build();
    }

    public Director toEntity(DirectorRequest request) {
        if (request == null) {
            return null;
        }

        return Director.builder()
                .name(request.getName())
                .biography(request.getBiography())
                .build();
    }
}
