package gyt.schedule_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String TICKET_EXCHANGE = "ticket.exchange";

    public static final String TICKET_BOOKED_QUEUE = "ticket.booked.queue";
    public static final String TICKET_CANCELLED_QUEUE = "ticket.cancelled.queue";

    public static final String TICKET_BOOKED_ROUTING_KEY = "ticket.booked";
    public static final String TICKET_CANCELLED_ROUTING_KEY = "ticket.cancelled";

    @Bean
    public TopicExchange ticketExchange() {
        return new TopicExchange(TICKET_EXCHANGE);
    }

    @Bean
    public Queue ticketBookedQueue() {
        return QueueBuilder.durable(TICKET_BOOKED_QUEUE)
                .withArgument("x-dead-letter-exchange", "dlx.exchange")
                .build();
    }

    @Bean
    public Queue ticketCancelledQueue() {
        return QueueBuilder.durable(TICKET_CANCELLED_QUEUE)
                .withArgument("x-dead-letter-exchange", "dlx.exchange")
                .build();
    }

    @Bean
    public Binding ticketBookedBinding() {
        return BindingBuilder
                .bind(ticketBookedQueue())
                .to(ticketExchange())
                .with(TICKET_BOOKED_ROUTING_KEY);
    }

    @Bean
    public Binding ticketCancelledBinding() {
        return BindingBuilder
                .bind(ticketCancelledQueue())
                .to(ticketExchange())
                .with(TICKET_CANCELLED_ROUTING_KEY);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}
