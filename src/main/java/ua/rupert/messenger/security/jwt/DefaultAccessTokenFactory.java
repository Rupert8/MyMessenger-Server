package ua.rupert.messenger.security.jwt;


import lombok.Setter;

import java.time.Duration;
import java.time.Instant;
import java.util.function.Function;

public class DefaultAccessTokenFactory implements Function<Token, Token> {
    @Setter
    private Duration tokenTtl = Duration.ofMinutes(5);

    @Override
    public Token apply(Token token) {
        var authorities = token.authorities().stream()
                .filter(authority -> authority.startsWith("GRANT_"))
                .map(authority -> authority.replace("GRANT_TYPE", ""))
                .toList();

        Instant now = Instant.now();
        return new Token(token.id(), token.subject(), authorities, now, now.plus(this.tokenTtl) );
    }
}
