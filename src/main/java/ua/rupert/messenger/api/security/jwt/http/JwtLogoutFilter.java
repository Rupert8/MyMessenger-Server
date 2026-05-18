package ua.rupert.messenger.api.security.jwt.http;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.Date;

@RequiredArgsConstructor
public class JwtLogoutFilter extends OncePerRequestFilter {
    private final RequestMatcher matcher = new AntPathRequestMatcher("/jwt/logout", HttpMethod.POST.name());

    private final SecurityContextRepository contextRepository = new RequestAttributeSecurityContextRepository();

    private final JdbcTemplate jdbcTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (this.matcher.matches(request)) {
            if (this.contextRepository.containsContext(request)) {
                var context = this.contextRepository.loadDeferredContext(request).get();
                if (context.getAuthentication() instanceof PreAuthenticatedAuthenticationToken
                        && context.getAuthentication().getAuthorities().contains(new SimpleGrantedAuthority("JWT_LOGOUT"))
                        && context.getAuthentication().getPrincipal() instanceof TokenUser user) {
                    this.jdbcTemplate.update("insert into deactivated_token (id, keep_until) values (?, ?)",
                            user.getToken().id(), Date.from(user.getToken().expiredAt()));

                    response.setStatus(HttpServletResponse.SC_NO_CONTENT);

                    return;
                }
            }

            throw new AccessDeniedException("User must be authenticated");
        }

        filterChain.doFilter(request, response);
    }
}
