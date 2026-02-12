package gyt.ticket_service.client;

import gyt.ticket_service.dto.ScheduleDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "schedule-service")
public interface ScheduleClient {
    
    @GetMapping("/internal/schedules/{uuid}")
    ScheduleDto getScheduleByUuid(@PathVariable String uuid);
}
