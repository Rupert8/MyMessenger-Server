package ua.rupert.messenger.api.security.jwt.http;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.util.function.Function;

@AllArgsConstructor
public class JwtAuthenticationConverter implements AuthenticationConverter {
    private Function<String, Token> accessTokenStringDeserializer;

    private Function<String, Token> refreshTokenStringDeserializer;

    @Override
    public Authentication convert(HttpServletRequest request) {
        var authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization != null && authorization.startsWith("Bearer ")) {
            var token = authorization.replace("Bearer ", "");
            var accessToken = accessTokenStringDeserializer.apply(token);
            if(accessToken != null){
                return new PreAuthenticatedAuthenticationToken(accessToken, token);
            }

            var refreshToken = refreshTokenStringDeserializer.apply(token);
            if(refreshToken != null){
                return new PreAuthenticatedAuthenticationToken(refreshToken, token);
            }
        }

        return null;
    }
}
