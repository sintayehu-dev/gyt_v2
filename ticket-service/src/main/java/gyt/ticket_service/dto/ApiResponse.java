package gyt.ticket_service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

/**
 * Unified API Response
 * All REST API endpoints MUST return responses using this structure
 * All response-related classes are consolidated in this file
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    
    private boolean success;
    private int status;
    private String message;
    private T data;
    private List<ErrorDetail> errors;
    private String timestamp;

    /**
     * Success response with data
     */
    public static <T> ApiResponse<T> success(T data, String message, int status) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = true;
        response.status = status;
        response.message = message;
        response.data = data;
        response.errors = null;
        response.timestamp = Instant.now().toString();
        return response;
    }

    /**
     * Success response with 200 OK
     */
    public static <T> ApiResponse<T> ok(T data, String message) {
        return success(data, message, 200);
    }

    /**
     * Success response for resource creation with 201 CREATED
     */
    public static <T> ApiResponse<T> created(T data, String message) {
        return success(data, message, 201);
    }

    /**
     * Success response with no data (e.g., DELETE operations)
     */
    public static <T> ApiResponse<T> noContent(String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = true;
        response.status = 204;
        response.message = message;
        response.data = null;
        response.errors = null;
        response.timestamp = Instant.now().toString();
        return response;
    }

    public static <T> ApiResponse<T> error(String message, int status, List<ErrorDetail> errors) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = false;
        response.status = status;
        response.message = message;
        response.data = null;
        response.errors = errors;
        response.timestamp = Instant.now().toString();
        return response;
    }

    public static <T> ApiResponse<T> error(String message, int status) {
        return error(message, status, null);
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ListResponse<T> {
        private List<T> items;
        private PaginationMetadata pagination;

        public static <T> ListResponse<T> of(List<T> items, int page, int size, long totalItems) {
            int totalPages = (int) Math.ceil((double) totalItems / size);
            return ListResponse.<T>builder()
                    .items(items)
                    .pagination(PaginationMetadata.builder()
                            .page(page)
                            .size(size)
                            .totalItems(totalItems)
                            .totalPages(totalPages)
                            .build())
                    .build();
        }

        public static <T> ListResponse<T> of(List<T> items) {
            return ListResponse.<T>builder()
                    .items(items)
                    .pagination(PaginationMetadata.builder()
                            .page(0)
                            .size(items.size())
                            .totalItems(items.size())
                            .totalPages(1)
                            .build())
                    .build();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaginationMetadata {
        private int page;
        private int size;
        private long totalItems;
        private int totalPages;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorDetail {
        private String code;
        private String field;
        private String message;

        public ErrorDetail(String code, String message) {
            this.code = code;
            this.message = message;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PagedResponse<T> {
        private List<T> content;
        private int pageNumber;
        private int pageSize;
        private long totalElements;
        private int totalPages;
        private boolean last;
    }
}
