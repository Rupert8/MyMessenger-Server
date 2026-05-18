package ua.rupert.messenger.api.security.jwt.http;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jwt.EncryptedJWT;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.util.UUID;
import java.util.function.Function;

@Slf4j
@AllArgsConstructor
@RequiredArgsConstructor
public class RefreshTokenJweStringDeserializer implements Function<String, Token> {
    private JWEDecrypter jweDecrypter;

    @Override
    public Token apply(String string) {
        try {
            var encryptedJWT = EncryptedJWT.parse(string);
            encryptedJWT.decrypt(jweDecrypter);
            var claimSet = encryptedJWT.getJWTClaimsSet();
            return new Token(UUID.fromString(claimSet.getJWTID()),
                    claimSet.getSubject(),
                    claimSet.getStringListClaim("authorities"),
                    claimSet.getIssueTime().toInstant(),
                    claimSet.getExpirationTime().toInstant());
        } catch (ParseException | JOSEException e) {
            log.error(e.getMessage(), e);
        }

        return null;
    }
}
