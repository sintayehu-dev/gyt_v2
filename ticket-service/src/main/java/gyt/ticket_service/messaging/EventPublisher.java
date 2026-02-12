package gyt.ticket_service.messaging;

import gyt.ticket_service.config.RabbitMQConfig;
import gyt.ticket_service.dto.TicketBookedEvent;
import gyt.ticket_service.dto.TicketCancelledEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishTicketBookedEvent(TicketBookedEvent event) {
        event.setEventId(UUID.randomUUID().toString());
        event.setTimestamp(LocalDateTime.now());

        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.TICKET_EXCHANGE,
                    RabbitMQConfig.TICKET_BOOKED_ROUTING_KEY,
                    event);
            log.info("Published TicketBookedEvent for ticket: {}", event.getTicketUuid());
        } catch (Exception e) {
            log.error("Failed to publish TicketBookedEvent: {}", e.getMessage());
        }
    }

    public void publishTicketCancelledEvent(TicketCancelledEvent event) {
        event.setEventId(UUID.randomUUID().toString());
        event.setTimestamp(LocalDateTime.now());

        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.TICKET_EXCHANGE,
                    RabbitMQConfig.TICKET_CANCELLED_ROUTING_KEY,
                    event);
            log.info("Published TicketCancelledEvent for ticket: {}", event.getTicketUuid());
        } catch (Exception e) {
            log.error("Failed to publish TicketCancelledEvent: {}", e.getMessage());
        }
    }
}
