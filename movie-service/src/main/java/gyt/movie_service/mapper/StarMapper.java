package gyt.movie_service.mapper;

import org.springframework.stereotype.Component;

import gyt.movie_service.dto.StarDto;
import gyt.movie_service.dto.StarRequest;
import gyt.movie_service.model.Star;

@Component
public class StarMapper {

    public StarDto toDto(Star star) {
        if (star == null) {
            return null;
        }

        return StarDto.builder()
                .uuid(star.getUuid())
                .name(star.getName())
                .biography(star.getBiography())
                .createdAt(star.getCreatedAt())
                .build();
    }

    public Star toEntity(StarRequest request) {
        if (request == null) {
            return null;
        }

        return Star.builder()
                .name(request.getName())
                .biography(request.getBiography())
                .build();
    }
}
