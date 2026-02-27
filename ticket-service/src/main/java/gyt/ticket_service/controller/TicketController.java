package gyt.ticket_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gyt.ticket_service.dto.ApiResponse;
import gyt.ticket_service.dto.TicketCreateRequest;
import gyt.ticket_service.dto.TicketDto;
import gyt.ticket_service.dto.TicketPaymentRequest;
import gyt.ticket_service.security.UserPrincipal;
import gyt.ticket_service.service.TicketService;
import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
@Tag(name = "Tickets")
public class TicketController {

    private final TicketService ticketService;

    @PostMapping
    @Operation(summary = "Create ticket")
    public ResponseEntity<ApiResponse<TicketDto>> createTicket(
            @RequestBody TicketCreateRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        
        String userUuid = principal.getUuid();
        
        if (userUuid == null) {
            throw new RuntimeException("User UUID not found in JWT token. Please login again to get a new token with UUID.");
        }
        
        TicketDto ticket = ticketService.createTicket(request, userUuid);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.created(ticket, "Ticket created successfully")
        );
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Get ticket by UUID")
    public ResponseEntity<ApiResponse<TicketDto>> getTicketByUuid(@PathVariable String uuid) {
        TicketDto ticket = ticketService.getTicketByUuid(uuid);
        return ResponseEntity.ok(
                ApiResponse.ok(ticket, "Ticket retrieved successfully")
        );
    }

    @GetMapping("/my-tickets")
    @Operation(summary = "Get my tickets")
    public ResponseEntity<ApiResponse<ApiResponse.ListResponse<TicketDto>>> getMyTickets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserPrincipal principal) {
        
        String userUuid = principal.getUuid();
        
        ApiResponse.PagedResponse<TicketDto> pagedResponse = ticketService.getTicketsByUserUuid(userUuid, page, size);
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

    @PutMapping("/{uuid}/confirm-payment")
    @Operation(summary = "Confirm payment")
    public ResponseEntity<ApiResponse<TicketDto>> confirmPayment(
            @PathVariable String uuid,
            @RequestBody TicketPaymentRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        
        String userUuid = principal.getUuid();
        
        TicketDto ticket = ticketService.confirmPayment(uuid, request, userUuid);
        return ResponseEntity.ok(
                ApiResponse.ok(ticket, "Payment confirmed successfully")
        );
    }

    @PutMapping("/{uuid}/cancel")
    @Operation(summary = "Cancel ticket")
    public ResponseEntity<ApiResponse<TicketDto>> cancelTicket(
            @PathVariable String uuid,
            @AuthenticationPrincipal UserPrincipal principal) {
        
        String userUuid = principal.getUuid();
        
        TicketDto ticket = ticketService.cancelTicket(uuid, userUuid);
        return ResponseEntity.ok(
                ApiResponse.ok(ticket, "Ticket cancelled successfully")
        );
    }

    @GetMapping("/search")
    @Operation(summary = "Search tickets")
    public ResponseEntity<ApiResponse<ApiResponse.ListResponse<TicketDto>>> searchTickets(
            @RequestParam(required = false) String scheduleUuid,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String seatNumber,
            @RequestParam(required = false) java.math.BigDecimal minPrice,
            @RequestParam(required = false) java.math.BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserPrincipal principal) {
        
        String userUuid = principal.getUuid();
        gyt.ticket_service.model.TicketStatus ticketStatus = status != null ? 
            gyt.ticket_service.model.TicketStatus.valueOf(status.toUpperCase()) : null;
        
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
