package gyt.schedule_service.controller;

import gyt.schedule_service.dto.*;
import gyt.schedule_service.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/admin/schedules")
@RequiredArgsConstructor
@Tag(name = "Admin Schedules")
public class AdminScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping
    @Operation(summary = "Create schedule")
    public ResponseEntity<ApiResponse<ScheduleDto>> createSchedule(@RequestBody ScheduleCreateRequest request) {
        ScheduleDto schedule = scheduleService.createSchedule(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.created(schedule, "Schedule created successfully")
        );
    }

    @PutMapping("/{uuid}")
    @Operation(summary = "Update schedule")
    public ResponseEntity<ApiResponse<ScheduleDto>> updateSchedule(
            @PathVariable String uuid,
            @RequestBody ScheduleCreateRequest request) {
        ScheduleDto schedule = scheduleService.updateSchedule(uuid, request);
        return ResponseEntity.ok(
                ApiResponse.ok(schedule, "Schedule updated successfully")
        );
    }

    @PatchMapping("/{uuid}")
    @Operation(summary = "Patch schedule")
    public ResponseEntity<ApiResponse<ScheduleDto>> patchSchedule(
            @PathVariable String uuid,
            @RequestBody ScheduleUpdateRequest request) {
        ScheduleDto schedule = scheduleService.patchSchedule(uuid, request);
        return ResponseEntity.ok(
                ApiResponse.ok(schedule, "Schedule patched successfully")
        );
    }

    @DeleteMapping("/{uuid}")
    @Operation(summary = "Delete schedule")
    public ResponseEntity<ApiResponse<Object>> deleteSchedule(@PathVariable String uuid) {
        scheduleService.deleteSchedule(uuid);
        return ResponseEntity.ok(
                ApiResponse.ok(null, "Schedule deleted successfully")
        );
    }

    @GetMapping
    @Operation(summary = "Get all schedules (Admin)")
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
    @Operation(summary = "Get schedule by UUID (Admin)")
    public ResponseEntity<ApiResponse<ScheduleDto>> getScheduleByUuid(@PathVariable String uuid) {
        ScheduleDto schedule = scheduleService.getScheduleByUuid(uuid);
        return ResponseEntity.ok(
                ApiResponse.ok(schedule, "Schedule retrieved successfully")
        );
    }
}
