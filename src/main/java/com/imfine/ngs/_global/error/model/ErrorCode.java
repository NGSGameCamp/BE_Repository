package com.imfine.ngs._global.error.model;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // Common
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", " Invalid Input Value"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C002", " Invalid Input Value"),
    ENTITY_NOT_FOUND(HttpStatus.BAD_REQUEST, "C003", " Entity Not Found"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C004", "Server Error"),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "C005", " Invalid Type Value"),
    HANDLE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "C006", "Access is Denied"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "C007", "Unauthorized"),

    // Order
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "O001", "Order not found"),
    GAME_ALREADY_IN_CART(HttpStatus.BAD_REQUEST, "O002", "Game is already in the cart"),
    GAME_NOT_IN_CART(HttpStatus.NOT_FOUND, "O003", "Game is not in the cart"),

    // Payment
    PAYMENT_AMOUNT_MISMATCH(HttpStatus.BAD_REQUEST, "P001", "Payment amount does not match"),
    PAYMENT_ALREADY_COMPLETED(HttpStatus.BAD_REQUEST, "P002", "Payment has already been completed"),
    PAYMENT_NOT_COMPLETED(HttpStatus.BAD_REQUEST, "P003", "Payment has not been completed"),
    PORTONE_API_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "P004", "Error communicating with PortOne API");


    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
