package ua.rupert.messenger.api.security.jwt.http;

import jakarta.servlet.http.HttpServletResponse;
import lombok.Builder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.csrf.CsrfFilter;
import ua.rupert.messenger.api.service.AuthenticationUserDetailsService;
import ua.rupert.messenger.store.repository.DeactivatedTokenRepository;

import java.util.function.Function;

@Builder
public class JwtAuthenticationConfigurer extends AbstractHttpConfigurer<JwtAuthenticationConfigurer, HttpSecurity> {
    private Function<Token, String> accessTokenStringSerializer;

    private Function<Token, String> refreshTokenStringSerializer;

    private Function<String, Token> accessTokenDeserializer;

    private Function<String, Token> refreshTokenDeserializer;

    private DeactivatedTokenRepository deactivatedTokenRepository;

    private JdbcTemplate jdbcTemplate;

    private AuthenticationUserDetailsService authenticationUserDetailsService;

    @Override
    public void configure(HttpSecurity builder) throws Exception {
        var requestJwtTokenFilter = new RequestJwtTokenFilter();
        requestJwtTokenFilter.setAccessTokenStringSerializer(this.accessTokenStringSerializer);
        requestJwtTokenFilter.setRefreshTokenStringSerializer(this.refreshTokenStringSerializer);

        var jwtAuthenticationFilter = new AuthenticationFilter(builder.getSharedObject(AuthenticationManager.class),
                new JwtAuthenticationConverter(this.accessTokenDeserializer, this.refreshTokenDeserializer));
        jwtAuthenticationFilter
                .setSuccessHandler((request, response, authentication) -> CsrfFilter.skipRequest(request));
        jwtAuthenticationFilter.
                setFailureHandler((request, response, exception) -> response.setStatus(HttpServletResponse.SC_FORBIDDEN));

        var preAuthenticatedAuthenticationProvider = new PreAuthenticatedAuthenticationProvider();
        preAuthenticatedAuthenticationProvider.setPreAuthenticatedUserDetailsService(new TokenAuthenticationUserDetailsService(this.deactivatedTokenRepository));
        builder.authenticationProvider(preAuthenticatedAuthenticationProvider);

        var daoAuthenticationProvider = new DaoAuthenticationProvider(this.authenticationUserDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(new BCryptPasswordEncoder());
        builder.authenticationProvider(daoAuthenticationProvider);

        var refreshTokenFilter = new RefreshTokenFilter();
        refreshTokenFilter.setAccessTokenStringSerializer(this.accessTokenStringSerializer);

        var jwtLogoutFilter = new JwtLogoutFilter(this.jdbcTemplate);

        builder.addFilterAfter(requestJwtTokenFilter, ExceptionTranslationFilter.class)
                .addFilterAfter(jwtAuthenticationFilter, CsrfFilter.class)
                .addFilterAfter(refreshTokenFilter, ExceptionTranslationFilter.class)
                .addFilterAfter(jwtLogoutFilter, ExceptionTranslationFilter.class);
    }
}
