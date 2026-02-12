package gyt.user_service.service;

import gyt.user_service.dto.UserProfileDto;
import gyt.user_service.dto.UserDto;
import gyt.user_service.dto.RatingDto;

import java.util.List;

public interface UserService {

    UserProfileDto updateProfile(String uuid, UserProfileDto dto);

    UserProfileDto bookmarkSchedules(String email, List<String> scheduleUuids);

    UserProfileDto getCurrentUserProfile();

    void deleteUser(String uuid);

    RatingDto addRating(RatingDto ratingDto);

    List<RatingDto> getUserRatings();

    List<String> getBookmarks();

    UserDto getUserByUuid(String uuid);

    UserDto getUserByEmail(String email);
}
