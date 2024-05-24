package dev.roy.coinkeeper.dto;

import dev.roy.coinkeeper.entity.RefreshToken;

public record LoginResponseDTO(String jwt, RefreshToken refreshToken, UserResponseDTO userDTO) {
}
