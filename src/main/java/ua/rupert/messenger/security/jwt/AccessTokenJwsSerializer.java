package ua.rupert.messenger.security.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.function.Function;

@Slf4j
public class AccessTokenJwsSerializer implements Function<Token, String> {
    private final JWSSigner jwsSigner;

    @Setter
    private JWSAlgorithm jwsAlgorithm = JWSAlgorithm.RS256;

    public AccessTokenJwsSerializer(JWSSigner signer, JWSAlgorithm jwsAlgorithm) {
        this.jwsSigner = signer;
        this.jwsAlgorithm = jwsAlgorithm;
    }

    public AccessTokenJwsSerializer(JWSSigner signer) {
        this.jwsSigner = signer;
    }

    @Override
    public String apply(Token token) {
        var jwsHeader = new JWSHeader.Builder(this.jwsAlgorithm)
                .keyID(token.id().toString())
                .build();
        var claimSet = new JWTClaimsSet.Builder()
                .jwtID(token.id().toString())
                .subject(token.subject())
                .issueTime(Date.from(token.createdAt()))
                .expirationTime(Date.from(token.expiresAt()))
                .claim("authorities", token.authorities())
                .build();

        var signerJWT = new SignedJWT(jwsHeader, claimSet);
        try {
            signerJWT.sign(this.jwsSigner);

            return signerJWT.serialize();
        } catch (JOSEException e) {
            log.error(e.getMessage(), e);
        }

        return null;
    }
}
