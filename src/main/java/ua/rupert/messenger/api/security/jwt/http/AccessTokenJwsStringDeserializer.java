package ua.rupert.messenger.api.security.jwt.http;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.util.UUID;
import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
public class AccessTokenJwsStringDeserializer implements Function<String, Token> {

    private final JWSVerifier verifier;

    @Override
    public Token apply(String string) {
        try {
            var signedJWt = SignedJWT.parse(string);
            if(signedJWt.verify(verifier)) {
                var claimSet = signedJWt.getJWTClaimsSet();
                return new Token(UUID.fromString(claimSet.getJWTID()),
                        claimSet.getSubject(),
                        claimSet.getStringListClaim("authorities"),
                        claimSet.getIssueTime().toInstant(),
                        claimSet.getExpirationTime().toInstant());
            }
        } catch (ParseException | JOSEException e) {
            log.error("JWT verification failed", e);
        }

        return null;
    }
}
