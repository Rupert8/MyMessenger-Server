package ua.rupert.messenger.api.security.jwt.http;

import lombok.Setter;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AccessTokenFactory implements Function<Token, Token> {
    @Setter
    private Duration tokenTtl = Duration.ofMinutes(5);

    @Override
    public Token apply(Token token) {
        var authorities = token.authorities()
                .stream()
                .filter(authority -> authority.startsWith("GRAND_"))
                .map(authority -> authority.replace("GRAND_", ""))
                .collect(Collectors.toCollection(LinkedList::new));

        var now = Instant.now();
        return new Token(token.id(), token.name(), authorities, now, now.plus(this.tokenTtl));
    }
}
