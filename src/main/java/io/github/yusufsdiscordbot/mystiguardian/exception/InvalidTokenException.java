package io.github.yusufsdiscordbot.mystiguardian.exception;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException() {
        super("Invalid token provided.");
    }
}
