package ua.rupert.messenger.security.jwt;

public record TokenResponse(String accessToken, String accessTokenExpiry,
                            String refreshToken, String refreshTokenExpiry) {
}
