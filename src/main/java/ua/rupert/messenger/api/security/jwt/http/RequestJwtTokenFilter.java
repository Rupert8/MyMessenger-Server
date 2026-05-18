package ua.rupert.messenger.api.security.jwt.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.Objects;
import java.util.function.Function;

@Setter
public class RequestJwtTokenFilter extends OncePerRequestFilter {
    private final RequestMatcher requestMatcher = new AntPathRequestMatcher("/jwt/json_token", HttpMethod.POST.name());

    private SecurityContextRepository contextRepository = new RequestAttributeSecurityContextRepository();

    private Function<Token, Token> accessTokenFactory = new AccessTokenFactory();

    private Function<Authentication, Token> refreshTokenFactory = new RefreshTokenFactory();

    private Function<Token, String> accessTokenStringSerializer = Objects::toString;

    private Function<Token, String> refreshTokenStringSerializer = Objects::toString;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (requestMatcher.matches(request)) {
            if (this.contextRepository.containsContext(request)) {
                var context = this.contextRepository.loadDeferredContext(request).get();
                if (context != null && !(context instanceof PreAuthenticatedAuthenticationToken)) {
                    var refreshToken = this.refreshTokenFactory.apply(context.getAuthentication());
                    var accessToken = this.accessTokenFactory.apply(refreshToken);

                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    this.objectMapper.writeValue(response.getWriter(),
                            new JsonToken(this.accessTokenStringSerializer.apply(accessToken),
                                    accessToken.expiredAt().toString(),
                                    this.refreshTokenStringSerializer.apply(refreshToken),
                                    refreshToken.expiredAt().toString()));

                    return;
                }
            }

            throw new AccessDeniedException("User must be authenticated");
        }

        filterChain.doFilter(request, response);
    }
}
