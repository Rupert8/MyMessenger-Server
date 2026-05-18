package ua.rupert.messenger.api.security.jwt.http;

import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RefreshTokenFactory implements Function<Authentication, Token> {
    @Setter
    private Duration tokenTtl = Duration.ofDays(10);

    @Override
    public Token apply(Authentication authentication) {
        var authorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .map(authority -> "GRAND_" + authority)
                .collect(Collectors.toCollection(LinkedList::new));
        authorities.add("JWT_REFRESH");
        authorities.add("JWT_LOGOUT");

        var now = Instant.now();

        return new Token(UUID.randomUUID(), authentication.getName(), authorities, now, now.plus(this.tokenTtl));
    }
}
