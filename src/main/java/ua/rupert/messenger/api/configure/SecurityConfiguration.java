package ua.rupert.messenger.api.configure;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import ua.rupert.messenger.api.security.jwt.http.*;
import ua.rupert.messenger.api.service.AuthenticationUserDetailsService;
import ua.rupert.messenger.store.repository.DeactivatedTokenRepository;

import java.text.ParseException;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    @Bean
    public JwtAuthenticationConfigurer jwtAuthenticationConfigurer(
            @Value("${jwt.access-token-key}") String accessToken,
            @Value("${jwt.refresh-token-key}") String refreshToken,
            DeactivatedTokenRepository repository,
            JdbcTemplate jdbcTemplate,
            AuthenticationUserDetailsService userDetailsService
    ) throws ParseException, JOSEException {

        return JwtAuthenticationConfigurer.builder()
                .accessTokenStringSerializer(new AccessTokenJwsStringSerializer(
                        new MACSigner(OctetSequenceKey.parse(accessToken))
                ))
                .refreshTokenStringSerializer(new RefreshTokenJweStringSerializer(
                        new DirectEncrypter(OctetSequenceKey.parse(refreshToken))
                ))
                .accessTokenDeserializer(new AccessTokenJwsStringDeserializer(
                        new MACVerifier(OctetSequenceKey.parse(accessToken))
                ))
                .refreshTokenDeserializer(new RefreshTokenJweStringDeserializer(
                        new DirectDecrypter(OctetSequenceKey.parse(refreshToken))
                ))
                .deactivatedTokenRepository(repository)
                .jdbcTemplate(jdbcTemplate)
                .authenticationUserDetailsService(userDetailsService)
                .build();
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationConfigurer configurer) throws Exception {
        http.apply(configurer);

        return http
                .csrf(CsrfConfigurer::disable)
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(sessionManager ->
                        sessionManager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/error").permitAll()
                                .requestMatchers("/auth/registration").permitAll()
                                .requestMatchers("/uploads/**").permitAll()
                                .anyRequest().authenticated())
                .build();

    }
}
