package gyt.schedule_service.controller;

import gyt.schedule_service.dto.ApiResponse;
import gyt.schedule_service.dto.AvailabilityResponse;
import gyt.schedule_service.dto.ScheduleDto;
import gyt.schedule_service.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/schedules")
@RequiredArgsConstructor
public class InternalScheduleController {

    private final ScheduleService scheduleService;

    @GetMapping("/{uuid}")
    public ResponseEntity<ScheduleDto> getScheduleByUuid(@PathVariable String uuid) {
        return ResponseEntity.ok(scheduleService.getScheduleByUuid(uuid));
    }
    
    @GetMapping("/id/{id}")
    public ResponseEntity<ScheduleDto> getScheduleById(@PathVariable Long id) {
        return ResponseEntity.ok(scheduleService.getScheduleById(id));
    }
    
    @GetMapping("/movie/{movieUuid}")
    public ResponseEntity<ApiResponse.PagedResponse<ScheduleDto>> getSchedulesByMovieUuid(
            @PathVariable String movieUuid,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {
        return ResponseEntity.ok(scheduleService.getSchedulesByMovieUuid(movieUuid, page, size));
    }

    @GetMapping("/{uuid}/exists")
    public ResponseEntity<Boolean> scheduleExists(@PathVariable String uuid) {
        return ResponseEntity.ok(scheduleService.scheduleExists(uuid));
    }

    @GetMapping("/{uuid}/availability")
    public ResponseEntity<AvailabilityResponse> checkAvailability(@PathVariable String uuid) {
        return ResponseEntity.ok(scheduleService.checkAvailability(uuid));
    }
}
