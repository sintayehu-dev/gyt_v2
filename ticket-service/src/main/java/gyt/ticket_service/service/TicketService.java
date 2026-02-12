package gyt.ticket_service.service;

import gyt.ticket_service.dto.ApiResponse;
import gyt.ticket_service.dto.TicketCreateRequest;
import gyt.ticket_service.dto.TicketDto;
import gyt.ticket_service.dto.TicketPaymentRequest;
import gyt.ticket_service.model.TicketStatus;

public interface TicketService {
    
    TicketDto createTicket(TicketCreateRequest request, String userUuid);
    
    TicketDto getTicketByUuid(String uuid);
    
    ApiResponse.PagedResponse<TicketDto> getAllTickets(int page, int size);
    
    ApiResponse.PagedResponse<TicketDto> getTicketsByStatus(TicketStatus status, int page, int size);
    
    ApiResponse.PagedResponse<TicketDto> getTicketsByScheduleUuid(String scheduleUuid, int page, int size);
    
    ApiResponse.PagedResponse<TicketDto> getTicketsByUserUuid(String userUuid, int page, int size);
    
    TicketDto confirmPayment(String uuid, TicketPaymentRequest request, String userUuid);
    
    TicketDto cancelTicket(String uuid, String userUuid);
    
    void deleteTicket(String uuid);
    
    ApiResponse.PagedResponse<TicketDto> advancedSearch(
        String userUuid,
        String scheduleUuid,
        TicketStatus status,
        String seatNumber,
        java.math.BigDecimal minPrice,
        java.math.BigDecimal maxPrice,
        int page,
        int size
    );
}
