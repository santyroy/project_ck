package dev.roy.coinkeeper.controller;

import dev.roy.coinkeeper.dto.ApiResponse;
import dev.roy.coinkeeper.dto.UserRequestDTO;
import dev.roy.coinkeeper.dto.UserResponseDTO;
import dev.roy.coinkeeper.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse> addUser(@Valid @RequestBody UserRequestDTO dto) {
        log.info("Adding of new user started");
        UserResponseDTO userResponseDTO = userService.addUser(dto);
        log.info("New user successfully added");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse(true, 201, "User created", userResponseDTO));
    }

    @GetMapping("{userId}")
    public ResponseEntity<ApiResponse> findUserById(@PathVariable Integer userId) {
        log.info("Searching user started");
        UserResponseDTO userResponseDTO = userService.findUserById(userId);
        log.info("Searching user completed");
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse(true, 200, "User found", userResponseDTO));
    }

    @DeleteMapping("{userId}")
    public ResponseEntity<ApiResponse> deleteUserById(@PathVariable Integer userId) {
        log.info("Deletion of user started");
        userService.deleteUserById(userId);
        log.info("Deletion of user completed");
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse(true, 200, "User deleted", null));
    }

    @PutMapping("{userId}")
    public ResponseEntity<ApiResponse> updateUserById(@PathVariable Integer userId, @RequestBody UserRequestDTO dto) {
        log.info("Updating user started");
        UserResponseDTO userResponseDTO = userService.updateUserById(userId, dto);
        log.info("Updating user completed");
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse(true, 200, "User updated", userResponseDTO));
    }

    @GetMapping
    public ResponseEntity<ApiResponse> findAllUsers(@RequestParam(required = false, defaultValue = "0") int page,
                                                    @RequestParam(required = false, defaultValue = "5") int size) {
        log.info("Fetching all users started");
        Page<UserResponseDTO> allUsers = userService.findAllUsers(page, size);
        log.info("Fetching all users completed");
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse(true, 200, "Users fetched", allUsers));
    }

    @GetMapping("/authorities")
    public ResponseEntity<ApiResponse> getAuthorities(Authentication authentication) {
        Set<String> roles = authentication.getAuthorities().stream()
                .map(role -> role.getAuthority().split("_")[1]).collect(Collectors.toSet());
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse(true, 200, "roles", roles));
    }
}
