package gyt.schedule_service.client;

import gyt.schedule_service.dto.ApiResponse;
import gyt.schedule_service.dto.TicketDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "ticket-service")
public interface TicketClient {
    
    @GetMapping("/internal/tickets/schedule/{scheduleUuid}")
    ApiResponse.PagedResponse<TicketDto> getTicketsByScheduleUuid(
        @PathVariable String scheduleUuid,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "100") int size
    );
}
