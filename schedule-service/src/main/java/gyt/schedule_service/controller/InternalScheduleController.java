package gyt.schedule_service.controller;

import gyt.schedule_service.dto.ApiResponse;
import gyt.schedule_service.dto.AvailabilityResponse;
import gyt.schedule_service.dto.ScheduleDto;
import gyt.schedule_service.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/internal/schedules")
@RequiredArgsConstructor
@Tag(name = "Internal Schedules")
public class InternalScheduleController {

    private final ScheduleService scheduleService;

    @GetMapping("/{uuid}")
    @Operation(summary = "Get schedule by UUID (Internal)")
    public ResponseEntity<ScheduleDto> getScheduleByUuid(@PathVariable String uuid) {
        return ResponseEntity.ok(scheduleService.getScheduleByUuid(uuid));
    }
    
    @GetMapping("/id/{id}")
    @Operation(summary = "Get schedule by ID (Internal)")
    public ResponseEntity<ScheduleDto> getScheduleById(@PathVariable Long id) {
        return ResponseEntity.ok(scheduleService.getScheduleById(id));
    }
    
    @GetMapping("/movie/{movieUuid}")
    @Operation(summary = "Get schedules by movie UUID (Internal)")
    public ResponseEntity<ApiResponse.PagedResponse<ScheduleDto>> getSchedulesByMovieUuid(
            @PathVariable String movieUuid,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {
        return ResponseEntity.ok(scheduleService.getSchedulesByMovieUuid(movieUuid, page, size));
    }

    @GetMapping("/{uuid}/exists")
    @Operation(summary = "Check if schedule exists (Internal)")
    public ResponseEntity<Boolean> scheduleExists(@PathVariable String uuid) {
        return ResponseEntity.ok(scheduleService.scheduleExists(uuid));
    }

    @GetMapping("/{uuid}/availability")
    @Operation(summary = "Check schedule availability (Internal)")
    public ResponseEntity<AvailabilityResponse> checkAvailability(@PathVariable String uuid) {
        return ResponseEntity.ok(scheduleService.checkAvailability(uuid));
    }
}
