package gyt.schedule_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleCreateRequest {
    private String movieUuid;
    private String cinemaHall;
    private LocalDate showDate;
    private LocalTime showTime;
    private LocalTime endTime;
    private Integer totalSeats;
    private BigDecimal price;
}
