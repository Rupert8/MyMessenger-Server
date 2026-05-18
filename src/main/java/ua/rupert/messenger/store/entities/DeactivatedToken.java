package ua.rupert.messenger.store.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Check;

import java.time.Instant;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "deactivated_token")
public class DeactivatedToken {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "keep_until", nullable = false)
    @Check(constraints = "keep_until > now()")
    private Instant keepUntil = Instant.now();
}
