package bootcamp.badgeting.infrastructure.http.response;

import bootcamp.badgeting.application.output.LoginOutPut;

public record AuthResponse(String accessToken, String tokenType, long expiresIn) {
    public static AuthResponse from(LoginOutPut loginOutPut) {
        return new AuthResponse(
                loginOutPut.accessToken(),
                loginOutPut.tokenType(),
                loginOutPut.expiresIn());
    }
}
