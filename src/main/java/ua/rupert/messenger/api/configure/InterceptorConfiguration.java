package ua.rupert.messenger.api.configure;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ua.rupert.messenger.api.security.jwt.http.AccessTokenJwsStringDeserializer;
import ua.rupert.messenger.api.security.jwt.http.TokenAuthenticationUserDetailsService;
import ua.rupert.messenger.api.security.jwt.websocket.JwtChannelInterceptor;

import java.text.ParseException;

@Configuration
public class InterceptorConfiguration {
    @Bean
    public JwtChannelInterceptor jwtChannelInterceptor(
            @Value("${jwt.access-token-key}") String accessToken,
            TokenAuthenticationUserDetailsService userDetailsService
    ) throws JOSEException, ParseException {
        return JwtChannelInterceptor.builder()
                .accessTokenStringDeserializer(new AccessTokenJwsStringDeserializer(
                        new MACVerifier(OctetSequenceKey.parse(accessToken))
                ))
                .userDetailsService(userDetailsService)
                .build();
    }
}
