package gyt.ticket_service.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gyt.ticket_service.client.ScheduleClient;
import gyt.ticket_service.dto.ApiResponse;
import gyt.ticket_service.dto.ScheduleDto;
import gyt.ticket_service.dto.TicketBookedEvent;
import gyt.ticket_service.dto.TicketCancelledEvent;
import gyt.ticket_service.dto.TicketCreateRequest;
import gyt.ticket_service.dto.TicketDto;
import gyt.ticket_service.dto.TicketPaymentRequest;
import gyt.ticket_service.exception.CustomRuntimeException;
import gyt.ticket_service.mapper.TicketMapper;
import gyt.ticket_service.messaging.EventPublisher;
import gyt.ticket_service.model.Ticket;
import gyt.ticket_service.model.TicketStatus;
import gyt.ticket_service.repository.TicketRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final TicketMapper ticketMapper;
    private final ScheduleClient scheduleClient;
    private final EventPublisher eventPublisher;

    @Override
    @Transactional
    public TicketDto createTicket(TicketCreateRequest request, String userUuid) {
        ScheduleDto schedule = validateScheduleAvailability(request.getScheduleUuid());
        
        if (schedule.getFallbackUsed() != null && schedule.getFallbackUsed()) {
            throw new CustomRuntimeException("Schedule service temporarily unavailable. Please try again later.", HttpStatus.SERVICE_UNAVAILABLE);
        }
        
        if (schedule.getAvailableSeats() <= 0) {
            throw new CustomRuntimeException("No seats available for this schedule", HttpStatus.BAD_REQUEST);
        }
        
        List<Ticket> existingTickets = ticketRepository.findByScheduleUuidAndSeatNumber(
                request.getScheduleUuid(), request.getSeatNumber());
        
        boolean seatTaken = existingTickets.stream()
                .anyMatch(t -> t.getStatus() == TicketStatus.PENDING || t.getStatus() == TicketStatus.CONFIRMED);
        
        if (seatTaken) {
            throw new CustomRuntimeException("Seat " + request.getSeatNumber() + " is already booked", HttpStatus.CONFLICT);
        }

        Ticket ticket = new Ticket();
        ticket.setUserUuid(userUuid);
        ticket.setScheduleUuid(request.getScheduleUuid());
        ticket.setSeatNumber(request.getSeatNumber());
        ticket.setPrice(request.getPrice());
        ticket.setStatus(TicketStatus.PENDING);
        ticket.setBookingTime(LocalDateTime.now());

        Ticket savedTicket = ticketRepository.save(ticket);
        log.info("Ticket created successfully: {}", savedTicket.getUuid());
        
        TicketBookedEvent event = new TicketBookedEvent();
        event.setTicketUuid(savedTicket.getUuid());
        event.setScheduleUuid(savedTicket.getScheduleUuid());
        event.setUserUuid(savedTicket.getUserUuid());
        event.setNumberOfSeats(1);
        eventPublisher.publishTicketBookedEvent(event);
        
        return ticketMapper.toDto(savedTicket);
    }

    @Retry(name = "scheduleServiceClient", fallbackMethod = "validateScheduleAvailabilityFallback")
    @CircuitBreaker(name = "scheduleServiceClient", fallbackMethod = "validateScheduleAvailabilityFallback")
    private ScheduleDto validateScheduleAvailability(String scheduleUuid) {
        log.info("Validating schedule availability for uuid: {}", scheduleUuid);
        ScheduleDto schedule = scheduleClient.getScheduleByUuid(scheduleUuid);
        log.info("Schedule validation successful for uuid: {}, available seats: {}", scheduleUuid, schedule.getAvailableSeats());
        return schedule;
    }

    private ScheduleDto validateScheduleAvailabilityFallback(String scheduleUuid, Exception e) {
        log.warn("Schedule service fallback triggered for uuid: {}. Exception type: {}, Message: {}. Returning safe default response", 
                scheduleUuid, e.getClass().getSimpleName(), e.getMessage());
        
        ScheduleDto fallbackSchedule = new ScheduleDto();
        fallbackSchedule.setUuid(scheduleUuid);
        fallbackSchedule.setAvailableSeats(0);
        fallbackSchedule.setTotalSeats(0);
        fallbackSchedule.setIsActive(false);
        fallbackSchedule.setFallbackUsed(true);
        
        return fallbackSchedule;
    }

    @Override
    public TicketDto getTicketByUuid(String uuid) {
        Ticket ticket = ticketRepository.findByUuid(uuid)
                .orElseThrow(() -> new CustomRuntimeException("Ticket not found", HttpStatus.NOT_FOUND));
        return ticketMapper.toDto(ticket);
    }

    @Override
    public ApiResponse.PagedResponse<TicketDto> getAllTickets(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Ticket> ticketPage = ticketRepository.findAll(pageable);
        return mapToPagedResponse(ticketPage);
    }

    @Override
    public ApiResponse.PagedResponse<TicketDto> getTicketsByStatus(TicketStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Ticket> ticketPage = ticketRepository.findByStatus(status, pageable);
        return mapToPagedResponse(ticketPage);
    }

    @Override
    @Transactional
    public TicketDto confirmPayment(String uuid, TicketPaymentRequest request, String userUuid) {
        Ticket ticket = ticketRepository.findByUuidAndUserUuid(uuid, userUuid)
                .orElseThrow(() -> new CustomRuntimeException("Ticket not found or unauthorized", HttpStatus.NOT_FOUND));

        if (ticket.getStatus() != TicketStatus.PENDING) {
            throw new CustomRuntimeException("Ticket is not in pending status", HttpStatus.BAD_REQUEST);
        }

        ticket.setStatus(TicketStatus.CONFIRMED);
        ticket.setPaymentId(request.getPaymentId());
        ticket.setPaymentTime(LocalDateTime.now());

        Ticket updatedTicket = ticketRepository.save(ticket);
        return ticketMapper.toDto(updatedTicket);
    }

    @Override
    @Transactional
    public TicketDto cancelTicket(String uuid, String userUuid) {
        Ticket ticket = ticketRepository.findByUuidAndUserUuid(uuid, userUuid)
                .orElseThrow(() -> new CustomRuntimeException("Ticket not found or unauthorized", HttpStatus.NOT_FOUND));

        if (ticket.getStatus() == TicketStatus.CANCELLED) {
            throw new CustomRuntimeException("Ticket is already cancelled", HttpStatus.BAD_REQUEST);
        }

        ticket.setStatus(TicketStatus.CANCELLED);
        Ticket updatedTicket = ticketRepository.save(ticket);
        
        TicketCancelledEvent event = new TicketCancelledEvent();
        event.setTicketUuid(updatedTicket.getUuid());
        event.setScheduleUuid(updatedTicket.getScheduleUuid());
        event.setUserUuid(updatedTicket.getUserUuid());
        event.setNumberOfSeats(1);
        eventPublisher.publishTicketCancelledEvent(event);
        
        return ticketMapper.toDto(updatedTicket);
    }

    @Override
    @Transactional
    public void deleteTicket(String uuid) {
        Ticket ticket = ticketRepository.findByUuid(uuid)
                .orElseThrow(() -> new CustomRuntimeException("Ticket not found", HttpStatus.NOT_FOUND));
        ticketRepository.delete(ticket);
    }

    @Override
    public ApiResponse.PagedResponse<TicketDto> advancedSearch(
            String userUuid,
            String scheduleUuid,
            TicketStatus status,
            String seatNumber,
            java.math.BigDecimal minPrice,
            java.math.BigDecimal maxPrice,
            int page,
            int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("bookingTime").descending());
        Page<Ticket> ticketPage = ticketRepository.searchTickets(
            userUuid, scheduleUuid, status, seatNumber, minPrice, maxPrice, pageable
        );
        return mapToPagedResponse(ticketPage);
    }

    @Override
    public ApiResponse.PagedResponse<TicketDto> getTicketsByScheduleUuid(String scheduleUuid, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("seatNumber").ascending());
        Page<Ticket> ticketPage = ticketRepository.findByScheduleUuid(scheduleUuid, pageable);
        return mapToPagedResponse(ticketPage);
    }
    
    @Override
    public ApiResponse.PagedResponse<TicketDto> getTicketsByUserUuid(String userUuid, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Ticket> ticketPage = ticketRepository.findByUserUuid(userUuid, pageable);
        return mapToPagedResponse(ticketPage);
    }

    private ApiResponse.PagedResponse<TicketDto> mapToPagedResponse(Page<Ticket> ticketPage) {
        List<TicketDto> content = ticketPage.getContent().stream()
                .map(ticketMapper::toDto)
                .collect(Collectors.toList());

        return new ApiResponse.PagedResponse<>(
                content,
                ticketPage.getNumber(),
                ticketPage.getSize(),
                ticketPage.getTotalElements(),
                ticketPage.getTotalPages(),
                ticketPage.isLast()
        );
    }
}
