package ua.rupert.messenger.api.security.jwt.websocket;

import lombok.Builder;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import ua.rupert.messenger.api.security.jwt.http.Token;
import ua.rupert.messenger.api.security.jwt.http.TokenAuthenticationUserDetailsService;

import java.util.function.Function;

@Builder
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class JwtChannelInterceptor implements ChannelInterceptor {
    private TokenAuthenticationUserDetailsService userDetailsService;

    private Function<String, Token> accessTokenStringDeserializer;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        var accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if(accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String bearerToken = accessor.getFirstNativeHeader("Authorization");
            if(bearerToken != null && bearerToken.startsWith("Bearer ")) {
                String tokenString =  bearerToken.substring(7);
                Token token = this.accessTokenStringDeserializer.apply(tokenString);

                var auth = new PreAuthenticatedAuthenticationToken(token,tokenString);
                var userDetailService = this.userDetailsService.loadUserDetails(auth);

                var user = new UsernamePasswordAuthenticationToken(
                        userDetailService.getUsername(),null, userDetailService.getAuthorities());

                accessor.setUser(user);
            }
        }

        return message;
    }
}
