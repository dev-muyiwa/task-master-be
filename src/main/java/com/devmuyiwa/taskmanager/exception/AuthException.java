package com.devmuyiwa.taskmanager.exception;

public class AuthException extends RuntimeException {
    public AuthException(String message) {
        super(message);
    }

    public static class EmailAlreadyExistsException extends AuthException {
        public EmailAlreadyExistsException(String email) {
            super("An account already exists with this email");
        }
    }

    public static class WeakPasswordException extends AuthException {
        public WeakPasswordException() {
            super("Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one number, and one special character");
        }
    }
} 