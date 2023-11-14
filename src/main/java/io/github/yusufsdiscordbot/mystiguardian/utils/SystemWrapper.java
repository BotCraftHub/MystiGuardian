package io.github.yusufsdiscordbot.mystiguardian.utils;

import io.github.yusufsdiscordbot.mystiguardian.errors.ShutdownException;

public class SystemWrapper {
    public void exit(int status) {
        try {
            System.exit(status);
        } catch (SecurityException e) {
            throw new ShutdownException("Error while shutting down", e);
        }
    }
}