package com.bread.userservice.exception;

public class CustomGraphQLException extends RuntimeException {
    private final String code;

    public CustomGraphQLException(String message, String code) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static CustomGraphQLException userAlreadyExists() {
        return new CustomGraphQLException("User already exists", "AUTH001");
    }

    public static CustomGraphQLException invalidCredentials() {
        return new CustomGraphQLException("Invalid email or password", "AUTH003");
    }

    public static CustomGraphQLException tokenInvalid() {
        return new CustomGraphQLException("Invalid or expired token", "AUTH004");
    }

    public static CustomGraphQLException accountCreationFailed() {
        return new CustomGraphQLException("Account could not be created", "AUTH002");
    }

    public static CustomGraphQLException notFound(String resource) {
        return new CustomGraphQLException(resource + " not found", "AUTH005");
    }
} 
