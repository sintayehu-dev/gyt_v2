package gyt.ticket_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gyt.ticket_service.dto.ApiResponse;
import gyt.ticket_service.dto.TicketDto;
import gyt.ticket_service.model.TicketStatus;
import gyt.ticket_service.service.TicketService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/tickets")
@RequiredArgsConstructor
public class AdminTicketController {

    private final TicketService ticketService;

    @GetMapping
    public ResponseEntity<ApiResponse<ApiResponse.ListResponse<TicketDto>>> getAllTickets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        ApiResponse.PagedResponse<TicketDto> pagedResponse = ticketService.getAllTickets(page, size);
        var listResponse = ApiResponse.ListResponse.of(
                pagedResponse.getContent(),
                pagedResponse.getPageNumber(),
                pagedResponse.getPageSize(),
                pagedResponse.getTotalElements()
        );
        return ResponseEntity.ok(
                ApiResponse.ok(listResponse, "Tickets retrieved successfully")
        );
    }

    @GetMapping("/user/{userUuid}")
    public ResponseEntity<ApiResponse<ApiResponse.ListResponse<TicketDto>>> getTicketsByUserUuid(
            @PathVariable String userUuid,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        ApiResponse.PagedResponse<TicketDto> pagedResponse = ticketService.getTicketsByUserUuid(userUuid, page, size);
        var listResponse = ApiResponse.ListResponse.of(
                pagedResponse.getContent(),
                pagedResponse.getPageNumber(),
                pagedResponse.getPageSize(),
                pagedResponse.getTotalElements()
        );
        return ResponseEntity.ok(
                ApiResponse.ok(listResponse, "User tickets retrieved successfully")
        );
    }

    @GetMapping("/schedule/{scheduleUuid}")
    public ResponseEntity<ApiResponse<ApiResponse.ListResponse<TicketDto>>> getTicketsByScheduleUuid(
            @PathVariable String scheduleUuid,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        ApiResponse.PagedResponse<TicketDto> pagedResponse = ticketService.getTicketsByScheduleUuid(scheduleUuid, page, size);
        var listResponse = ApiResponse.ListResponse.of(
                pagedResponse.getContent(),
                pagedResponse.getPageNumber(),
                pagedResponse.getPageSize(),
                pagedResponse.getTotalElements()
        );
        return ResponseEntity.ok(
                ApiResponse.ok(listResponse, "Schedule tickets retrieved successfully")
        );
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<ApiResponse.ListResponse<TicketDto>>> getTicketsByStatus(
            @PathVariable TicketStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        ApiResponse.PagedResponse<TicketDto> pagedResponse = ticketService.getTicketsByStatus(status, page, size);
        var listResponse = ApiResponse.ListResponse.of(
                pagedResponse.getContent(),
                pagedResponse.getPageNumber(),
                pagedResponse.getPageSize(),
                pagedResponse.getTotalElements()
        );
        return ResponseEntity.ok(
                ApiResponse.ok(listResponse, "Tickets by status retrieved successfully")
        );
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<ApiResponse<Object>> deleteTicket(@PathVariable String uuid) {
        ticketService.deleteTicket(uuid);
        return ResponseEntity.ok(
                ApiResponse.ok(null, "Ticket deleted successfully")
        );
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<ApiResponse.ListResponse<TicketDto>>> searchTickets(
            @RequestParam(required = false) String userUuid,
            @RequestParam(required = false) String scheduleUuid,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String seatNumber,
            @RequestParam(required = false) java.math.BigDecimal minPrice,
            @RequestParam(required = false) java.math.BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        TicketStatus ticketStatus = status != null ? TicketStatus.valueOf(status.toUpperCase()) : null;
        
        ApiResponse.PagedResponse<TicketDto> pagedResponse = ticketService.advancedSearch(
            userUuid, scheduleUuid, ticketStatus, seatNumber, minPrice, maxPrice, page, size
        );
        var listResponse = ApiResponse.ListResponse.of(
                pagedResponse.getContent(),
                pagedResponse.getPageNumber(),
                pagedResponse.getPageSize(),
                pagedResponse.getTotalElements()
        );
        return ResponseEntity.ok(
                ApiResponse.ok(listResponse, "Tickets found successfully")
        );
    }
}
