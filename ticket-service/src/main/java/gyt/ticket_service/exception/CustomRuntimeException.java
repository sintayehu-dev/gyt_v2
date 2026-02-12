package gyt.ticket_service.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomRuntimeException extends RuntimeException {
    private final HttpStatus status;

    public CustomRuntimeException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
