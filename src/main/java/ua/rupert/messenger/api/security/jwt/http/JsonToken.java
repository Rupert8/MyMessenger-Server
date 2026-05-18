package ua.rupert.messenger.api.security.jwt.http;

public record JsonToken(String accessToken, String accessTokenExpiry,
                        String refreshToken, String refreshTokenExpiry) {
}
