package dev.roy.coinkeeper.dto;

public record ApiResponse(boolean result, int status, String message, Object data) {
}
