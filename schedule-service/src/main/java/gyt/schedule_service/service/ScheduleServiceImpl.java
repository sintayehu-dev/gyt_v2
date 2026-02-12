package gyt.schedule_service.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gyt.schedule_service.client.MovieClient;
import gyt.schedule_service.client.TicketClient;
import gyt.schedule_service.dto.ApiResponse;
import gyt.schedule_service.dto.AvailabilityResponse;
import gyt.schedule_service.dto.MovieDto;
import gyt.schedule_service.dto.ScheduleCreateRequest;
import gyt.schedule_service.dto.ScheduleDto;
import gyt.schedule_service.dto.ScheduleUpdateRequest;
import gyt.schedule_service.dto.TicketDto;
import gyt.schedule_service.exception.CustomRuntimeException;
import gyt.schedule_service.mapper.ScheduleMapper;
import gyt.schedule_service.model.Schedule;
import gyt.schedule_service.repository.ScheduleRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ScheduleMapper scheduleMapper;
    private final MovieClient movieClient;
    private final TicketClient ticketClient;

    @Override
    public ApiResponse.PagedResponse<ScheduleDto> getAllSchedules(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Schedule> schedulePage = scheduleRepository.findByIsActiveTrue(pageable);

        return buildPagedResponse(schedulePage);
    }

    @Override
    public ScheduleDto getScheduleByUuid(String uuid) {
        Schedule schedule = findScheduleByUuid(uuid);

        if (!schedule.getIsActive()) {
            throw new CustomRuntimeException("Schedule is not active", HttpStatus.NOT_FOUND);
        }

        return scheduleMapper.toDto(schedule);
    }

    @Override
    public ApiResponse.PagedResponse<ScheduleDto> getSchedulesByMovieUuid(String movieUuid, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Schedule> schedulePage = scheduleRepository.findByMovieUuid(movieUuid, pageable);

        return buildPagedResponse(schedulePage);
    }

    @Override
    public ApiResponse.PagedResponse<ScheduleDto> getSchedulesByDate(LocalDate date, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Schedule> schedulePage = scheduleRepository.findByShowDate(date, pageable);

        return buildPagedResponse(schedulePage);
    }

    @Override
    public ApiResponse.PagedResponse<ScheduleDto> getSchedulesByCinemaHall(String hall, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Schedule> schedulePage = scheduleRepository.findByCinemaHall(hall, pageable);

        return buildPagedResponse(schedulePage);
    }

    @Override
    @Transactional
    public ScheduleDto createSchedule(ScheduleCreateRequest request) {
        validateScheduleRequest(request);
        
        validateMovieExists(request.getMovieUuid());

        Schedule schedule = scheduleMapper.toEntity(request);
        Schedule savedSchedule = scheduleRepository.save(schedule);

        return scheduleMapper.toDto(savedSchedule);
    }

    @Retry(name = "movieServiceClient", fallbackMethod = "validateMovieExistsFallback")
    @CircuitBreaker(name = "movieServiceClient", fallbackMethod = "validateMovieExistsFallback")
    private void validateMovieExists(String movieUuid) {
        log.info("Validating movie existence for uuid: {}", movieUuid);
        MovieDto movie = movieClient.getMovieByUuid(movieUuid);
        log.info("Movie validation successful for uuid: {}, title: {}", movieUuid, movie.getTitle());
    }

    private void validateMovieExistsFallback(String movieUuid, Exception e) {
        log.warn("Movie service fallback triggered for uuid: {}. Exception type: {}, Message: {}", 
                movieUuid, e.getClass().getSimpleName(), e.getMessage());
        throw new CustomRuntimeException("Movie service temporarily unavailable. Please try again later.", HttpStatus.SERVICE_UNAVAILABLE);
    }

    @Override
    @Transactional
    public ScheduleDto updateSchedule(String uuid, ScheduleCreateRequest request) {
        Schedule schedule = findScheduleByUuid(uuid);
        validateScheduleRequest(request);

        schedule.setMovieUuid(request.getMovieUuid());
        schedule.setCinemaHall(request.getCinemaHall());
        schedule.setShowDate(request.getShowDate());
        schedule.setShowTime(request.getShowTime());
        schedule.setEndTime(request.getEndTime());
        schedule.setTotalSeats(request.getTotalSeats());
        schedule.setPrice(request.getPrice());

        Schedule updatedSchedule = scheduleRepository.save(schedule);
        return scheduleMapper.toDto(updatedSchedule);
    }

    @Override
    @Transactional
    public ScheduleDto patchSchedule(String uuid, ScheduleUpdateRequest request) {
        Schedule schedule = findScheduleByUuid(uuid);

        if (request.getShowDate() != null) {
            schedule.setShowDate(request.getShowDate());
        }
        if (request.getShowTime() != null) {
            schedule.setShowTime(request.getShowTime());
        }
        if (request.getEndTime() != null) {
            schedule.setEndTime(request.getEndTime());
        }
        if (request.getCinemaHall() != null) {
            schedule.setCinemaHall(request.getCinemaHall());
        }
        if (request.getTotalSeats() != null) {
            if (request.getTotalSeats() < schedule.getTotalSeats() - schedule.getAvailableSeats()) {
                throw new CustomRuntimeException(
                        "Cannot reduce total seats below already booked seats",
                        HttpStatus.BAD_REQUEST);
            }
            int bookedSeats = schedule.getTotalSeats() - schedule.getAvailableSeats();
            schedule.setTotalSeats(request.getTotalSeats());
            schedule.setAvailableSeats(request.getTotalSeats() - bookedSeats);
        }
        if (request.getPrice() != null) {
            schedule.setPrice(request.getPrice());
        }

        Schedule updatedSchedule = scheduleRepository.save(schedule);
        return scheduleMapper.toDto(updatedSchedule);
    }

    @Override
    @Transactional
    public void deleteSchedule(String uuid) {
        Schedule schedule = findScheduleByUuid(uuid);
        scheduleRepository.delete(schedule);
    }

    @Override
    public boolean scheduleExists(String uuid) {
        return scheduleRepository.findByUuid(uuid)
                .map(Schedule::getIsActive)
                .orElse(false);
    }

    @Override
    public AvailabilityResponse checkAvailability(String uuid) {
        Schedule schedule = findScheduleByUuid(uuid);
        int bookedSeatsCount = getBookedSeatsCount(schedule.getUuid());
        int actualAvailableSeats = schedule.getTotalSeats() - bookedSeatsCount;

        return AvailabilityResponse.builder()
                .scheduleUuid(schedule.getUuid())
                .availableSeats(actualAvailableSeats)
                .totalSeats(schedule.getTotalSeats())
                .isAvailable(actualAvailableSeats > 0 && schedule.getIsActive())
                .fallbackUsed(false)
                .build();
    }

    @Retry(name = "ticketServiceClient", fallbackMethod = "getBookedSeatsCountFallback")
    @CircuitBreaker(name = "ticketServiceClient", fallbackMethod = "getBookedSeatsCountFallback")
    private int getBookedSeatsCount(String scheduleUuid) {
        log.info("Fetching booked seats count for schedule: {}", scheduleUuid);
        ApiResponse.PagedResponse<TicketDto> tickets = ticketClient.getTicketsByScheduleUuid(scheduleUuid, 0, 1000);
        int count = (int) tickets.getContent().stream()
                .filter(ticket -> "PENDING".equals(ticket.getStatus()) || "CONFIRMED".equals(ticket.getStatus()))
                .count();
        log.info("Successfully fetched booked seats count for schedule: {}, count: {}", scheduleUuid, count);
        return count;
    }

    private int getBookedSeatsCountFallback(String scheduleUuid, Exception e) {
        log.warn("Ticket service fallback triggered for schedule: {}. Exception type: {}, Message: {}. Returning default count: 0", 
                scheduleUuid, e.getClass().getSimpleName(), e.getMessage());
        return 0;
    }

    private Schedule findScheduleByUuid(String uuid) {
        return scheduleRepository.findByUuid(uuid)
                .orElseThrow(() -> new CustomRuntimeException(
                        "Schedule not found with uuid: " + uuid,
                        HttpStatus.NOT_FOUND));
    }

    @Override
    public ApiResponse.PagedResponse<ScheduleDto> advancedSearch(
            String movieUuid,
            String cinemaHall,
            LocalDate startDate,
            LocalDate endDate,
            java.math.BigDecimal minPrice,
            java.math.BigDecimal maxPrice,
            int page,
            int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Schedule> schedulePage = scheduleRepository.searchSchedules(
            movieUuid, cinemaHall, startDate, endDate, minPrice, maxPrice, pageable
        );

        List<ScheduleDto> content = schedulePage.getContent().stream()
                .map(scheduleMapper::toDto)
                .collect(Collectors.toList());

        ApiResponse.PagedResponse<ScheduleDto> response = new ApiResponse.PagedResponse<>();
        response.setContent(content);
        response.setPageNumber(schedulePage.getNumber());
        response.setPageSize(schedulePage.getSize());
        response.setTotalElements(schedulePage.getTotalElements());
        response.setTotalPages(schedulePage.getTotalPages());
        response.setLast(schedulePage.isLast());
        return response;
    }

    private void validateScheduleRequest(ScheduleCreateRequest request) {
        if (request.getMovieUuid() == null) {
            throw new CustomRuntimeException("Movie UUID is required", HttpStatus.BAD_REQUEST);
        }
        if (request.getCinemaHall() == null || request.getCinemaHall().trim().isEmpty()) {
            throw new CustomRuntimeException("Cinema hall is required", HttpStatus.BAD_REQUEST);
        }
        if (request.getShowDate() == null) {
            throw new CustomRuntimeException("Show date is required", HttpStatus.BAD_REQUEST);
        }
        if (request.getShowTime() == null) {
            throw new CustomRuntimeException("Show time is required", HttpStatus.BAD_REQUEST);
        }
        if (request.getTotalSeats() == null || request.getTotalSeats() <= 0) {
            throw new CustomRuntimeException("Total seats must be positive", HttpStatus.BAD_REQUEST);
        }
        if (request.getPrice() == null || request.getPrice().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new CustomRuntimeException("Price must be positive", HttpStatus.BAD_REQUEST);
        }
    }

    private ApiResponse.PagedResponse<ScheduleDto> buildPagedResponse(Page<Schedule> schedulePage) {
        List<ScheduleDto> content = schedulePage.getContent().stream()
                .map(scheduleMapper::toDto)
                .collect(Collectors.toList());

        ApiResponse.PagedResponse<ScheduleDto> response = new ApiResponse.PagedResponse<>();
        response.setContent(content);
        response.setPageNumber(schedulePage.getNumber());
        response.setPageSize(schedulePage.getSize());
        response.setTotalElements(schedulePage.getTotalElements());
        response.setTotalPages(schedulePage.getTotalPages());
        response.setLast(schedulePage.isLast());
        return response;
    }
    
    @Override
    public ScheduleDto getScheduleById(Long id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new CustomRuntimeException(
                        "Schedule not found with id: " + id,
                        HttpStatus.NOT_FOUND));
        
        if (!schedule.getIsActive()) {
            throw new CustomRuntimeException("Schedule is not active", HttpStatus.NOT_FOUND);
        }
        
        return scheduleMapper.toDto(schedule);
    }
}
