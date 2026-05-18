package ua.rupert.messenger.api.security.jwt.http;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record Token(UUID id,
                   String name,
                   List<String> authorities,
                   Instant createdAt,
                   Instant expiredAt) {
}
