package com.powerfit.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

/**
 * Respuesta estándar para todos los endpoints.
 * Éxito:  { "success": true,  "data": {...},  "message": "OK" }
 * Error:  { "success": false, "error": "...", "code": 400   }
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final boolean success;
    private final T       data;
    private final String  message;
    private final String  error;
    private final Integer code;

    private ApiResponse(boolean success, T data, String message, String error, Integer code) {
        this.success = success;
        this.data    = data;
        this.message = message;
        this.error   = error;
        this.code    = code;
    }

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, data, "OK", null, null);
    }

    public static <T> ApiResponse<T> ok(T data, String message) {
        return new ApiResponse<>(true, data, message, null, null);
    }

    public static <T> ApiResponse<T> error(String error, int code) {
        return new ApiResponse<>(false, null, null, error, code);
    }
}
