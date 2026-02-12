package gyt.user_service.mapper;

import gyt.user_service.dto.RatingDto;
import gyt.user_service.model.UserProfile;
import gyt.user_service.model.UserRating;
import org.springframework.stereotype.Component;

@Component
public class UserRatingMapper {

    public RatingDto toDto(UserRating entity) {
        if (entity == null) {
            return null;
        }
        return RatingDto.builder()
                .id(entity.getUuid())
                .movieUuid(entity.getMovieUuid())
                .rating(entity.getRating())
                .review(entity.getReview())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public UserRating toEntity(RatingDto dto, UserProfile user) {
        if (dto == null) {
            return null;
        }
        return UserRating.builder()
                .user(user)
                .movieUuid(dto.getMovieUuid())
                .rating(dto.getRating())
                .review(dto.getReview())
                .build();
    }
}
