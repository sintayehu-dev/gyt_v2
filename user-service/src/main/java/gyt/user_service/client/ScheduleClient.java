package gyt.user_service.client;

import gyt.user_service.dto.ScheduleDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "schedule-service")
public interface ScheduleClient {
    
    @GetMapping("/internal/schedules/{uuid}")
    ScheduleDto getScheduleByUuid(@PathVariable String uuid);
}
