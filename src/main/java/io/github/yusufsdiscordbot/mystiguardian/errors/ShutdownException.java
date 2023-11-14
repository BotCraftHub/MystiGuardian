package io.github.yusufsdiscordbot.mystiguardian.errors;

public class ShutdownException extends RuntimeException {
    public ShutdownException(String message, SecurityException e) {
        super(message, e);
    }
}