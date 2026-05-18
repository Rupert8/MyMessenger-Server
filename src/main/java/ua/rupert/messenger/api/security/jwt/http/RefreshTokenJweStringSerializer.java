package ua.rupert.messenger.api.security.jwt.http;

import com.nimbusds.jose.*;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
@AllArgsConstructor
public class RefreshTokenJweStringSerializer implements Function<Token, String> {
    private final JWEEncrypter jweEncrypter;

    @Setter
    private JWEAlgorithm jweAlgorithm = JWEAlgorithm.DIR;

    @Setter
    private EncryptionMethod encryptionMethod = EncryptionMethod.A256GCM;


    @Override
    public String apply(Token token) {
        var jweHeader = new JWEHeader.Builder(jweAlgorithm, encryptionMethod)
                .keyID(token.id().toString())
                .build();

        var claimSet = new JWTClaimsSet.Builder()
                .jwtID(token.id().toString())
                .subject(token.name())
                .issueTime(Date.from(token.createdAt()))
                .expirationTime(Date.from(token.expiredAt()))
                .claim("authorities", token.authorities())
                .build();

        var encryptedJWT = new EncryptedJWT(jweHeader, claimSet);

        try {
            encryptedJWT.encrypt(jweEncrypter);

            return encryptedJWT.serialize();
        } catch (JOSEException e) {
            log.error(e.getMessage(), e);
        }

        return null;
    }
}
