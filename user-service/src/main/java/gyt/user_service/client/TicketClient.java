package gyt.user_service.client;

import gyt.user_service.dto.ApiResponse;
import gyt.user_service.dto.TicketDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "ticket-service")
public interface TicketClient {
    
    @GetMapping("/internal/tickets/user/{userUuid}")
    ApiResponse.PagedResponse<TicketDto> getTicketsByUserUuid(
        @PathVariable String userUuid,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "100") int size
    );
}
