package dev.roy.coinkeeper.service;

import dev.roy.coinkeeper.dto.UserRequestDTO;
import dev.roy.coinkeeper.dto.UserResponseDTO;
import dev.roy.coinkeeper.entity.Role;
import dev.roy.coinkeeper.entity.User;
import dev.roy.coinkeeper.exception.UserEmailAlreadyExistsException;
import dev.roy.coinkeeper.exception.UserNotFoundException;
import dev.roy.coinkeeper.exception.UserRoleNotFoundException;
import dev.roy.coinkeeper.repository.RoleRepository;
import dev.roy.coinkeeper.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponseDTO addUser(UserRequestDTO dto) {
        // Check if USER role exists
        Optional<Role> roleOpt = roleRepository.findByName("USER");
        if (roleOpt.isEmpty()) {
            throw new UserRoleNotFoundException("USER role not found");
        }

        // Check if email already exists
        String email = dto.email();
        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserEmailAlreadyExistsException("User email: " + email + " already exists");
        }

        User user = new User(0,
                dto.name(), dto.email(), passwordEncoder.encode(dto.password()), dto.picture(),
                LocalDateTime.now(), false, Set.of(roleOpt.get()), null, null);
        User savedUser = userRepository.save(user);
        log.info("User: {} with email: {} added to database", savedUser.getName(), savedUser.getEmail());
        return new UserResponseDTO(savedUser.getId(), savedUser.getName(), savedUser.getEmail(), savedUser.getPicture(), getRolesOfUser(savedUser));
    }

    public UserResponseDTO findUserById(Integer userId) {
        User user = getUser(userId);
        return new UserResponseDTO(user.getId(), user.getName(), user.getEmail(), user.getPicture(), getRolesOfUser(user));
    }

    public void deleteUserById(Integer userId) {
        User user = getUser(userId);
        userRepository.delete(user);
    }

    public UserResponseDTO updateUserById(Integer userId, UserRequestDTO dto) {
        User existingUser = getUser(userId);
        if (null != dto.name() && !dto.name().isBlank()) {
            log.info("Updating name");
            existingUser.setName(dto.name());
        }
        if (null != dto.email() && !dto.email().isBlank()) {
            // Check if a user has not given another user's email
            Optional<User> userOpt = userRepository.findByEmail(dto.email());
            if (userOpt.isPresent()) {
                throw new UserEmailAlreadyExistsException("User email: " + dto.email() + " already exists");
            }
            log.info("Updating email");
            existingUser.setEmail(dto.email());
        }
        if (null != dto.picture() && !dto.picture().isBlank()) {
            log.info("Updating picture");
            existingUser.setPicture(dto.picture());
        }
        if (null != dto.password() && !dto.password().isBlank()) {
            log.info("Updating password");
            existingUser.setPassword(passwordEncoder.encode(dto.password()));
        }

        User updateddUser = userRepository.save(existingUser);
        return new UserResponseDTO(updateddUser.getId(), updateddUser.getName(), updateddUser.getEmail(), updateddUser.getPicture(), getRolesOfUser(updateddUser));
    }

    public Page<UserResponseDTO> findAllUsers(int pageNo, int pageSize) {
        PageRequest page = PageRequest.of(pageNo, pageSize);
        Page<User> users = userRepository.findAll(page);
        return users.map(user -> new UserResponseDTO(user.getId(), user.getName(), user.getEmail(), user.getPicture(), getRolesOfUser(user)));
    }

    public User getUser(Integer userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new UserNotFoundException("User with ID: " + userId + " not found");
        }
        return userOpt.get();
    }

    public User getUser(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new UserNotFoundException("User with email: " + email + " not found");
        }
        return userOpt.get();
    }

    private Set<String> getRolesOfUser(User user) {
        return user.getRoles().stream().map(Role::getName).collect(Collectors.toSet());
    }
}
