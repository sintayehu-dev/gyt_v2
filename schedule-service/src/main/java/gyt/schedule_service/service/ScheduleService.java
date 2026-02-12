package gyt.schedule_service.service;

import java.time.LocalDate;

import gyt.schedule_service.dto.ApiResponse;
import gyt.schedule_service.dto.AvailabilityResponse;
import gyt.schedule_service.dto.ScheduleCreateRequest;
import gyt.schedule_service.dto.ScheduleDto;
import gyt.schedule_service.dto.ScheduleUpdateRequest;

public interface ScheduleService {
    ApiResponse.PagedResponse<ScheduleDto> getAllSchedules(int page, int size);
    ScheduleDto getScheduleByUuid(String uuid);
    ScheduleDto getScheduleById(Long id);
    ApiResponse.PagedResponse<ScheduleDto> getSchedulesByMovieUuid(String movieUuid, int page, int size);
    ApiResponse.PagedResponse<ScheduleDto> getSchedulesByDate(LocalDate date, int page, int size);
    ApiResponse.PagedResponse<ScheduleDto> getSchedulesByCinemaHall(String hall, int page, int size);
    
    ApiResponse.PagedResponse<ScheduleDto> advancedSearch(
        String movieUuid,
        String cinemaHall,
        LocalDate startDate,
        LocalDate endDate,
        java.math.BigDecimal minPrice,
        java.math.BigDecimal maxPrice,
        int page,
        int size
    );
    
    ScheduleDto createSchedule(ScheduleCreateRequest request);
    ScheduleDto updateSchedule(String uuid, ScheduleCreateRequest request);
    ScheduleDto patchSchedule(String uuid, ScheduleUpdateRequest request);
    void deleteSchedule(String uuid);
    
    boolean scheduleExists(String uuid);
    AvailabilityResponse checkAvailability(String uuid);
}
