package gyt.ticket_service.client;

import gyt.ticket_service.dto.ApiResponse;
import gyt.ticket_service.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserClient {
    
    @GetMapping("/internal/users/uuid/{uuid}")
    ApiResponse<UserDto> getUserByUuid(@PathVariable String uuid);
    
    @GetMapping("/internal/users/email/{email}")
    ApiResponse<UserDto> getUserByEmail(@PathVariable String email);
}
