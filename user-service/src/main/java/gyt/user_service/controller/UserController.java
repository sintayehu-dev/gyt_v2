package gyt.user_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gyt.user_service.dto.ApiResponse;
import gyt.user_service.dto.UserProfileDto;
import gyt.user_service.dto.RatingDto;
import gyt.user_service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "User Management")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Get current user profile")
    public ResponseEntity<ApiResponse<UserProfileDto>> getCurrentProfile() {
        UserProfileDto profile = userService.getCurrentUserProfile();
        return ResponseEntity.ok(
                ApiResponse.ok(profile, "User profile retrieved successfully")
        );
    }

    @PatchMapping("/{uuid}")
    @Operation(summary = "Update user profile")
    public ResponseEntity<ApiResponse<UserProfileDto>> updateProfile(
            @PathVariable String uuid, 
            @RequestBody UserProfileDto dto) {
        UserProfileDto updated = userService.updateProfile(uuid, dto);
        return ResponseEntity.ok(
                ApiResponse.ok(updated, "User profile updated successfully")
        );
    }

    @DeleteMapping("/{uuid}")
    @Operation(summary = "Delete user account")
    public ResponseEntity<ApiResponse<Object>> deleteUser(@PathVariable String uuid) {
        userService.deleteUser(uuid);
        return ResponseEntity.ok(
                ApiResponse.ok(null, "User deleted successfully")
        );
    }

    @PostMapping("/bookmarks")
    @Operation(summary = "Bookmark schedules")
    public ResponseEntity<ApiResponse<UserProfileDto>> bookmarkSchedules(
            @RequestBody List<String> scheduleUuids) {
        String email = userService.getCurrentUserProfile().getEmail();
        UserProfileDto updated = userService.bookmarkSchedules(email, scheduleUuids);
        return ResponseEntity.ok(
                ApiResponse.ok(updated, "Bookmarks updated successfully")
        );
    }

    @GetMapping("/bookmarks")
    @Operation(summary = "Get bookmarks")
    public ResponseEntity<ApiResponse<ApiResponse.ListResponse<String>>> getBookmarks() {
        List<String> bookmarks = userService.getBookmarks();
        ApiResponse.ListResponse<String> listResponse = ApiResponse.ListResponse.of(bookmarks);
        return ResponseEntity.ok(
                ApiResponse.ok(listResponse, "Bookmarks retrieved successfully")
        );
    }

    @PostMapping("/ratings")
    @Operation(summary = "Add movie rating")
    public ResponseEntity<ApiResponse<Object>> addRating(@RequestBody RatingDto ratingDto) {
        userService.addRating(ratingDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.created(null, "Rating added successfully")
        );
    }

    @GetMapping("/ratings")
    @Operation(summary = "Get user ratings")
    public ResponseEntity<ApiResponse<ApiResponse.ListResponse<RatingDto>>> getRatings() {
        List<RatingDto> ratings = userService.getUserRatings();
        ApiResponse.ListResponse<RatingDto> listResponse = ApiResponse.ListResponse.of(ratings);
        return ResponseEntity.ok(
                ApiResponse.ok(listResponse, "Ratings retrieved successfully")
        );
    }
}
