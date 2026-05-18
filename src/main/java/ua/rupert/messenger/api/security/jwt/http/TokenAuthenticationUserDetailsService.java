package ua.rupert.messenger.api.security.jwt.http;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import ua.rupert.messenger.store.repository.DeactivatedTokenRepository;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class TokenAuthenticationUserDetailsService implements
        AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {

    private final DeactivatedTokenRepository deactivatedTokenRepository;

    @Override
    public UserDetails loadUserDetails(PreAuthenticatedAuthenticationToken authentication) throws UsernameNotFoundException {
        if(authentication.getPrincipal() instanceof Token token) {
            return new TokenUser(token.name(), "nopassword", true, true,
                    deactivatedTokenRepository.findById(token.id()).isEmpty()
                    && token.expiredAt().isAfter(Instant.now()), true,
                    token.authorities().stream()
                            .map(SimpleGrantedAuthority::new)
                            .toList(), token);
        }

        throw new UsernameNotFoundException("Principal must be of type Token");
    }
}
