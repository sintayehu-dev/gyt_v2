package gyt.user_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_ratings")
public class UserRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserProfile user;

    @Column(name = "movie_uuid", nullable = false)
    private String movieUuid;

    @Column(nullable = false)
    private Integer rating;

    @Column(length = 500)
    private String review;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Column(unique = true, updatable = false)
    private String uuid;

    @PrePersist
    public void generateUuid() {
        if (uuid == null) {
            uuid = java.util.UUID.randomUUID().toString();
        }
    }
}
