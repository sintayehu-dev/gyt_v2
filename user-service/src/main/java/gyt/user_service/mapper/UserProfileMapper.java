package gyt.user_service.mapper;

import gyt.user_service.dto.UserProfileDto;
import gyt.user_service.model.UserProfile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class UserProfileMapper {

    public UserProfileDto toDto(UserProfile entity) {
        if (entity == null) {
            return null;
        }
        return UserProfileDto.builder()
                .uuid(entity.getUuid())
                .email(entity.getEmail())
                .fullName(entity.getFullName())
                .bookmarkedScheduleUuids(new ArrayList<>(entity.getBookmarkedScheduleUuids()))
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public UserProfile toEntity(UserProfileDto dto) {
        if (dto == null) {
            return null;
        }
        return UserProfile.builder()
                .uuid(dto.getUuid())
                .email(dto.getEmail())
                .fullName(dto.getFullName())
                .bookmarkedScheduleUuids(
                        dto.getBookmarkedScheduleUuids() != null ? dto.getBookmarkedScheduleUuids() : new ArrayList<>())
                .createdAt(dto.getCreatedAt())
                .build();
    }
}
