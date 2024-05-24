package dev.roy.coinkeeper.dto;

import java.util.Set;

public record UserResponseDTO(Integer userId, String name, String email, String picture, Set<String> roles) {
}
