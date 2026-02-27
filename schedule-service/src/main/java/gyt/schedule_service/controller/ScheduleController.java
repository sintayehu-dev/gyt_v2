package gyt.schedule_service.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gyt.schedule_service.dto.ApiResponse;
import gyt.schedule_service.dto.ScheduleDto;
import gyt.schedule_service.service.ScheduleService;
import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/schedules")
@RequiredArgsConstructor
@Tag(name = "Schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @GetMapping
    @Operation(summary = "Get all schedules")
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
    @Operation(summary = "Get schedule by UUID")
    public ResponseEntity<ApiResponse<ScheduleDto>> getScheduleByUuid(@PathVariable String uuid) {
        ScheduleDto schedule = scheduleService.getScheduleByUuid(uuid);
        return ResponseEntity.ok(
                ApiResponse.ok(schedule, "Schedule retrieved successfully")
        );
    }

    @GetMapping("/movie/{movieUuid}")
    @Operation(summary = "Get schedules by movie UUID")
    public ResponseEntity<ApiResponse<ApiResponse.ListResponse<ScheduleDto>>> getSchedulesByMovieUuid(
            @PathVariable String movieUuid,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        ApiResponse.PagedResponse<ScheduleDto> pagedResponse = scheduleService.getSchedulesByMovieUuid(movieUuid, page, size);
        var listResponse = ApiResponse.ListResponse.of(
                pagedResponse.getContent(),
                pagedResponse.getPageNumber(),
                pagedResponse.getPageSize(),
                pagedResponse.getTotalElements()
        );
        return ResponseEntity.ok(
                ApiResponse.ok(listResponse, "Movie schedules retrieved successfully")
        );
    }

    @GetMapping("/date/{date}")
    @Operation(summary = "Get schedules by date")
    public ResponseEntity<ApiResponse<ApiResponse.ListResponse<ScheduleDto>>> getSchedulesByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        ApiResponse.PagedResponse<ScheduleDto> pagedResponse = scheduleService.getSchedulesByDate(date, page, size);
        var listResponse = ApiResponse.ListResponse.of(
                pagedResponse.getContent(),
                pagedResponse.getPageNumber(),
                pagedResponse.getPageSize(),
                pagedResponse.getTotalElements()
        );
        return ResponseEntity.ok(
                ApiResponse.ok(listResponse, "Schedules by date retrieved successfully")
        );
    }

    @GetMapping("/cinema/{hall}")
    @Operation(summary = "Get schedules by cinema hall")
    public ResponseEntity<ApiResponse<ApiResponse.ListResponse<ScheduleDto>>> getSchedulesByCinemaHall(
            @PathVariable String hall,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        ApiResponse.PagedResponse<ScheduleDto> pagedResponse = scheduleService.getSchedulesByCinemaHall(hall, page, size);
        var listResponse = ApiResponse.ListResponse.of(
                pagedResponse.getContent(),
                pagedResponse.getPageNumber(),
                pagedResponse.getPageSize(),
                pagedResponse.getTotalElements()
        );
        return ResponseEntity.ok(
                ApiResponse.ok(listResponse, "Cinema hall schedules retrieved successfully")
        );
    }

    @GetMapping("/advanced-search")
    @Operation(summary = "Advanced schedule search")
    public ResponseEntity<ApiResponse<ApiResponse.ListResponse<ScheduleDto>>> advancedSearchSchedules(
            @RequestParam(required = false) String movieUuid,
            @RequestParam(required = false) String cinemaHall,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) java.math.BigDecimal minPrice,
            @RequestParam(required = false) java.math.BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        ApiResponse.PagedResponse<ScheduleDto> pagedResponse = scheduleService.advancedSearch(
            movieUuid, cinemaHall, startDate, endDate, minPrice, maxPrice, page, size
        );
        var listResponse = ApiResponse.ListResponse.of(
                pagedResponse.getContent(),
                pagedResponse.getPageNumber(),
                pagedResponse.getPageSize(),
                pagedResponse.getTotalElements()
        );
        return ResponseEntity.ok(
                ApiResponse.ok(listResponse, "Schedules found successfully")
        );
    }
}
