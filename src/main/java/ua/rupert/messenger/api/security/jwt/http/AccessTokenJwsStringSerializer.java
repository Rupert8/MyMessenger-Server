package ua.rupert.messenger.api.security.jwt.http;

import com.nimbusds.jose.*;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
@AllArgsConstructor
public class AccessTokenJwsStringSerializer implements Function<Token, String> {
    private final JWSSigner signer;

    @Setter
    private JWSAlgorithm algorithm = JWSAlgorithm.HS256;

    @Override
    public String apply(Token token) {
        var jwsHeader = new JWSHeader.Builder(JWSAlgorithm.HS256)
                .keyID(token.id().toString())
                .build();

        var claimSet = new JWTClaimsSet.Builder()
                .jwtID(token.id().toString())
                .subject(token.name())
                .issueTime(Date.from(token.createdAt()))
                .expirationTime(Date.from(token.expiredAt()))
                .claim("authorities", token.authorities())
                .build();

        var signedJws = new SignedJWT(jwsHeader, claimSet);

        try {
            signedJws.sign(signer);

            return signedJws.serialize();
        } catch (JOSEException e) {
            log.error(e.getMessage(), e);
        }

        return null;
    }
}
