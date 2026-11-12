package io.github.clfundev.better.common;

import lombok.Getter;

@Getter
public enum ResultCode {
    SUCCESS(200, "Success"),
    PARAM_ERROR(400, "Parameter validation failed"),
    UNAUTHORIZED(401, "Unauthorized or token expired"),
    FORBIDDEN(403, "Access denied"),
    NOT_FOUND(404, "Resource not found"),
    METHOD_NOT_ALLOWED(405, "HTTP method not supported"),
    INTERNAL_SERVER_ERROR(500, "Internal server error");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
