package bootcamp.badgeting.application.output;

public record LoginOutPut(String accessToken, String tokenType, long expiresIn) {
}
