package dev.roy.coinkeeper.security.service;

import dev.roy.coinkeeper.entity.User;
import dev.roy.coinkeeper.repository.UserRepository;
import dev.roy.coinkeeper.security.model.SecureUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class SecureUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new UsernameNotFoundException("User with email: " + email + " not found");
        }
        return new SecureUser(userOpt.get());
    }
}
