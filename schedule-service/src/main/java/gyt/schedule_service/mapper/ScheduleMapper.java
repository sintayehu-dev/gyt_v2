package gyt.schedule_service.mapper;

import org.springframework.stereotype.Component;

import gyt.schedule_service.client.MovieClient;
import gyt.schedule_service.dto.MovieDto;
import gyt.schedule_service.dto.ScheduleCreateRequest;
import gyt.schedule_service.dto.ScheduleDto;
import gyt.schedule_service.model.Schedule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduleMapper {
    
    private final MovieClient movieClient;
    
    public ScheduleDto toDto(Schedule schedule) {
        ScheduleDto.ScheduleDtoBuilder builder = ScheduleDto.builder()
                .uuid(schedule.getUuid())
                .cinemaHall(schedule.getCinemaHall())
                .showDate(schedule.getShowDate())
                .showTime(schedule.getShowTime())
                .endTime(schedule.getEndTime())
                .totalSeats(schedule.getTotalSeats())
                .availableSeats(schedule.getAvailableSeats())
                .price(schedule.getPrice())
                .isActive(schedule.getIsActive())
                .createdAt(schedule.getCreatedAt())
                .updatedAt(schedule.getUpdatedAt());
        
        try {
            MovieDto movie = movieClient.getMovieByUuid(schedule.getMovieUuid());
            builder.movie(movie);
        } catch (Exception e) {
            log.warn("Failed to fetch movie details for uuid: {}", schedule.getMovieUuid(), e);
        }
        
        return builder.build();
    }
    
    public Schedule toEntity(ScheduleCreateRequest request) {
        return Schedule.builder()
                .movieUuid(request.getMovieUuid())
                .cinemaHall(request.getCinemaHall())
                .showDate(request.getShowDate())
                .showTime(request.getShowTime())
                .endTime(request.getEndTime())
                .totalSeats(request.getTotalSeats())
                .availableSeats(request.getTotalSeats())
                .price(request.getPrice())
                .isActive(true)
                .build();
    }
}
