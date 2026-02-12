package gyt.ticket_service.controller;

import gyt.ticket_service.dto.ApiResponse;
import gyt.ticket_service.dto.TicketDto;
import gyt.ticket_service.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/tickets")
@RequiredArgsConstructor
public class InternalTicketController {

    private final TicketService ticketService;

    @GetMapping("/{uuid}")
    public ResponseEntity<TicketDto> getTicketByUuid(@PathVariable String uuid) {
        TicketDto ticket = ticketService.getTicketByUuid(uuid);
        return ResponseEntity.ok(ticket);
    }

    @GetMapping("/schedule/{scheduleUuid}")
    public ResponseEntity<ApiResponse.PagedResponse<TicketDto>> getTicketsByScheduleUuid(
            @PathVariable String scheduleUuid,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {
        
        ApiResponse.PagedResponse<TicketDto> tickets = ticketService.getTicketsByScheduleUuid(scheduleUuid, page, size);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/user/{userUuid}")
    public ResponseEntity<ApiResponse.PagedResponse<TicketDto>> getTicketsByUserUuid(
            @PathVariable String userUuid,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {
        
        ApiResponse.PagedResponse<TicketDto> tickets = ticketService.getTicketsByUserUuid(userUuid, page, size);
        return ResponseEntity.ok(tickets);
    }
}
