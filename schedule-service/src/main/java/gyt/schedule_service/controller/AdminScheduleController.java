package gyt.schedule_service.controller;

import gyt.schedule_service.dto.*;
import gyt.schedule_service.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/schedules")
@RequiredArgsConstructor
public class AdminScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping
    public ResponseEntity<ApiResponse<ScheduleDto>> createSchedule(@RequestBody ScheduleCreateRequest request) {
        ScheduleDto schedule = scheduleService.createSchedule(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.created(schedule, "Schedule created successfully")
        );
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<ApiResponse<ScheduleDto>> updateSchedule(
            @PathVariable String uuid,
            @RequestBody ScheduleCreateRequest request) {
        ScheduleDto schedule = scheduleService.updateSchedule(uuid, request);
        return ResponseEntity.ok(
                ApiResponse.ok(schedule, "Schedule updated successfully")
        );
    }

    @PatchMapping("/{uuid}")
    public ResponseEntity<ApiResponse<ScheduleDto>> patchSchedule(
            @PathVariable String uuid,
            @RequestBody ScheduleUpdateRequest request) {
        ScheduleDto schedule = scheduleService.patchSchedule(uuid, request);
        return ResponseEntity.ok(
                ApiResponse.ok(schedule, "Schedule patched successfully")
        );
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<ApiResponse<Object>> deleteSchedule(@PathVariable String uuid) {
        scheduleService.deleteSchedule(uuid);
        return ResponseEntity.ok(
                ApiResponse.ok(null, "Schedule deleted successfully")
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<ApiResponse.ListResponse<ScheduleDto>>> getAllSchedules(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        ApiResponse.PagedResponse<ScheduleDto> pagedResponse = scheduleService.getAllSchedules(page, size);
        var listResponse = ApiResponse.ListResponse.of(
                pagedResponse.getContent(),
                pagedResponse.getPageNumber(),
                pagedResponse.getPageSize(),
                pagedResponse.getTotalElements()
        );
        return ResponseEntity.ok(
                ApiResponse.ok(listResponse, "Schedules retrieved successfully")
        );
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<ApiResponse<ScheduleDto>> getScheduleByUuid(@PathVariable String uuid) {
        ScheduleDto schedule = scheduleService.getScheduleByUuid(uuid);
        return ResponseEntity.ok(
                ApiResponse.ok(schedule, "Schedule retrieved successfully")
        );
    }
}
