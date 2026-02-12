package gyt.user_service.controller;

import gyt.user_service.dto.ApiResponse;
import gyt.user_service.dto.UserDto;
import gyt.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/users")
@RequiredArgsConstructor
public class InternalUserController {

    private final UserService userService;

    @GetMapping("/uuid/{uuid}")
    public ResponseEntity<ApiResponse<UserDto>> getUserByUuid(@PathVariable String uuid) {
        UserDto user = userService.getUserByUuid(uuid);
        return ResponseEntity.ok(
                ApiResponse.ok(user, "User retrieved successfully")
        );
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<UserDto>> getUserByEmail(@PathVariable String email) {
        UserDto user = userService.getUserByEmail(email);
        return ResponseEntity.ok(
                ApiResponse.ok(user, "User retrieved successfully")
        );
    }
}
