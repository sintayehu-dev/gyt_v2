package gyt.user_service.service;

import gyt.user_service.dto.MovieRatedEvent;
import gyt.user_service.dto.RatingDto;
import gyt.user_service.dto.UserDeletedEvent;
import gyt.user_service.dto.UserDto;
import gyt.user_service.dto.UserProfileDto;
import gyt.user_service.dto.UserProfileUpdatedEvent;
import gyt.user_service.exception.CustomRuntimeException;
import gyt.user_service.mapper.UserProfileMapper;
import gyt.user_service.mapper.UserRatingMapper;
import gyt.user_service.messaging.EventPublisher;
import gyt.user_service.model.UserProfile;
import gyt.user_service.model.UserRating;
import gyt.user_service.repository.UserProfileRepository;
import gyt.user_service.repository.UserRatingRepository;
import gyt.user_service.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserProfileRepository userProfileRepository;
    private final UserProfileMapper userProfileMapper;
    private final UserRatingRepository userRatingRepository;
    private final UserRatingMapper userRatingMapper;
    private final EventPublisher eventPublisher;

    @Override
    @Transactional
    public UserProfileDto updateProfile(String uuid, UserProfileDto dto) {
        UserProfile profile = userProfileRepository.findByUuid(uuid)
                .orElseThrow(() -> new CustomRuntimeException(
                        "User profile not found for uuid: " + uuid, HttpStatus.NOT_FOUND));

        UserPrincipal currentUser = getCurrentUserPrincipal();
        if (!currentUser.isAdmin() && !profile.getEmail().equals(currentUser.getEmail())) {
            throw new CustomRuntimeException("You can only update your own profile",
                    HttpStatus.FORBIDDEN);
        }

        boolean needsSync = false;
        String newEmail = null;
        String newName = null;

        if (dto.getEmail() != null && !dto.getEmail().equals(profile.getEmail())) {
            newEmail = dto.getEmail();
            profile.setEmail(dto.getEmail());
            needsSync = true;
        }

        if (dto.getFullName() != null && !dto.getFullName().equals(profile.getFullName())) {
            newName = dto.getFullName();
            needsSync = true;
        }

        if (needsSync) {
            UserProfileUpdatedEvent event = new UserProfileUpdatedEvent();
            event.setUserUuid(uuid);
            event.setEmail(newEmail);
            event.setFullName(newName);
            eventPublisher.publishUserProfileUpdatedEvent(event);
        }

        if (dto.getFullName() != null) {
            profile.setFullName(dto.getFullName());
        }
        if (dto.getBookmarkedScheduleUuids() != null) {
            profile.setBookmarkedScheduleUuids(dto.getBookmarkedScheduleUuids());
        }

        UserProfile savedProfile = userProfileRepository.save(profile);
        return userProfileMapper.toDto(savedProfile);
    }

    @Override
    @Transactional
    public UserProfileDto bookmarkSchedules(String email, List<String> scheduleUuids) {
        UserProfile profile = userProfileRepository.findByEmail(email)
                .orElseThrow(() -> new CustomRuntimeException(
                        "User profile not found for email: " + email, HttpStatus.NOT_FOUND));

        if (scheduleUuids != null) {
            for (String uuid : scheduleUuids) {
                if (!profile.getBookmarkedScheduleUuids().contains(uuid)) {
                    profile.getBookmarkedScheduleUuids().add(uuid);
                }
            }
        }

        UserProfile savedProfile = userProfileRepository.save(profile);
        return userProfileMapper.toDto(savedProfile);
    }

    @Override
    public UserProfileDto getCurrentUserProfile() {
        String email = getCurrentUserEmail();
        UserProfile profile = userProfileRepository.findByEmail(email)
                .orElseThrow(() -> new CustomRuntimeException(
                        "User profile not found for email: " + email, HttpStatus.NOT_FOUND));
        return userProfileMapper.toDto(profile);
    }

    private UserPrincipal getCurrentUserPrincipal() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserPrincipal userPrincipal) {
            return userPrincipal;
        }
        throw new RuntimeException("Invalid authentication principal");
    }

    private String getCurrentUserEmail() {
        return getCurrentUserPrincipal().getEmail();
    }

    @Override
    @Transactional
    public void deleteUser(String uuid) {
        UserProfile profile = userProfileRepository.findByUuid(uuid)
                .orElseThrow(() -> new CustomRuntimeException(
                        "User profile not found for uuid: " + uuid, HttpStatus.NOT_FOUND));

        UserPrincipal currentUser = getCurrentUserPrincipal();
        if (!currentUser.isAdmin() && !profile.getEmail().equals(currentUser.getEmail())) {
            throw new CustomRuntimeException("You can only delete your own profile",
                    HttpStatus.FORBIDDEN);
        }

        List<UserRating> userRatings = userRatingRepository.findByUserEmail(profile.getEmail());
        if (!userRatings.isEmpty()) {
            userRatingRepository.deleteAll(userRatings);
            log.info("Deleted {} ratings for user: {}", userRatings.size(), uuid);
        }

        userProfileRepository.delete(profile);
        log.info("Deleted user profile: {}", uuid);

        UserDeletedEvent event = new UserDeletedEvent();
        event.setUserUuid(uuid);
        event.setEmail(profile.getEmail());
        event.setDeletedAt(LocalDateTime.now());
        eventPublisher.publishUserDeletedEvent(event);
        log.info("Published UserDeletedEvent for user: {}", uuid);
    }

    @Override
    @Transactional
    public RatingDto addRating(RatingDto ratingDto) {
        String email = getCurrentUserEmail();
        UserProfile profile = userProfileRepository.findByEmail(email)
                .orElseThrow(() -> new CustomRuntimeException(
                        "User profile not found", HttpStatus.NOT_FOUND));

        UserRating rating = userRatingMapper.toEntity(ratingDto, profile);
        UserRating savedRating = userRatingRepository.save(rating);

        MovieRatedEvent event = new MovieRatedEvent();
        event.setRatingUuid(savedRating.getUuid());
        event.setMovieUuid(savedRating.getMovieUuid());
        event.setUserUuid(profile.getUuid());
        event.setRating(savedRating.getRating());
        event.setReview(savedRating.getReview());
        eventPublisher.publishMovieRatedEvent(event);

        return userRatingMapper.toDto(savedRating);
    }

    @Override
    public List<RatingDto> getUserRatings() {
        String email = getCurrentUserEmail();
        List<UserRating> ratings = userRatingRepository.findByUserEmail(email);
        return ratings.stream()
                .map(userRatingMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getBookmarks() {
        String email = getCurrentUserEmail();
        UserProfile profile = userProfileRepository.findByEmail(email)
                .orElseThrow(() -> new CustomRuntimeException(
                        "User profile not found", HttpStatus.NOT_FOUND));
        return profile.getBookmarkedScheduleUuids();
    }

    @Override
    public UserDto getUserByUuid(String uuid) {
        UserProfile profile = userProfileRepository.findByUuid(uuid)
                .orElseThrow(() -> new CustomRuntimeException(
                        "User not found with uuid: " + uuid, HttpStatus.NOT_FOUND));

        return new UserDto(
                profile.getUuid(),
                profile.getEmail(),
                profile.getFullName(),
                null, // role
                null // lastLogin
        );
    }

    @Override
    public UserDto getUserByEmail(String email) {
        UserProfile profile = userProfileRepository.findByEmail(email)
                .orElseThrow(() -> new CustomRuntimeException(
                        "User not found with email: " + email, HttpStatus.NOT_FOUND));

        return new UserDto(
                profile.getUuid(),
                profile.getEmail(),
                profile.getFullName(),
                null,
                null
        );
    }
}
