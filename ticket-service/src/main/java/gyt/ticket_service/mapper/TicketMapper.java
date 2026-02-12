package gyt.ticket_service.mapper;

import gyt.ticket_service.client.ScheduleClient;
import gyt.ticket_service.client.UserClient;
import gyt.ticket_service.dto.ApiResponse;
import gyt.ticket_service.dto.ScheduleDto;
import gyt.ticket_service.dto.TicketDto;
import gyt.ticket_service.dto.UserDto;
import gyt.ticket_service.model.Ticket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TicketMapper {

    private final UserClient userClient;
    private final ScheduleClient scheduleClient;

    public TicketDto toDto(Ticket ticket) {
        if (ticket == null) {
            return null;
        }
        
        TicketDto dto = new TicketDto();
        dto.setUuid(ticket.getUuid());
        dto.setUserUuid(ticket.getUserUuid());
        dto.setScheduleUuid(ticket.getScheduleUuid());
        dto.setSeatNumber(ticket.getSeatNumber());
        dto.setPrice(ticket.getPrice());
        dto.setStatus(ticket.getStatus());
        dto.setBookingTime(ticket.getBookingTime());
        dto.setPaymentTime(ticket.getPaymentTime());
        dto.setPaymentId(ticket.getPaymentId());
        dto.setCreatedAt(ticket.getCreatedAt());
        dto.setUpdatedAt(ticket.getUpdatedAt());
        
        if (ticket.getUserUuid() != null) {
            try {
                ApiResponse<UserDto> userResponse = userClient.getUserByUuid(ticket.getUserUuid());
                if (userResponse != null && userResponse.getData() != null) {
                    dto.setUser(userResponse.getData());
                } else {
                    dto.setUser(null);
                }
            } catch (Exception e) {
                log.warn("Failed to fetch user data for UUID: {}. Error: {}", 
                    ticket.getUserUuid(), e.getMessage());
                dto.setUser(null);
            }
        }
        
        if (ticket.getScheduleUuid() != null) {
            try {
                ScheduleDto schedule = scheduleClient.getScheduleByUuid(ticket.getScheduleUuid());
                dto.setSchedule(schedule);
            } catch (Exception e) {
                log.warn("Failed to fetch schedule data for UUID: {}. Error: {}", 
                    ticket.getScheduleUuid(), e.getMessage());
                dto.setSchedule(null);
            }
        }
        
        return dto;
    }

    public Ticket toEntity(TicketDto dto) {
        if (dto == null) {
            return null;
        }
        
        Ticket ticket = new Ticket();
        ticket.setUuid(dto.getUuid());
        ticket.setUserUuid(dto.getUserUuid());
        ticket.setScheduleUuid(dto.getScheduleUuid());
        ticket.setSeatNumber(dto.getSeatNumber());
        ticket.setPrice(dto.getPrice());
        ticket.setStatus(dto.getStatus());
        ticket.setBookingTime(dto.getBookingTime());
        ticket.setPaymentTime(dto.getPaymentTime());
        ticket.setPaymentId(dto.getPaymentId());
        
        return ticket;
    }
}
