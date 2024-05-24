package dev.roy.coinkeeper.security.service;

import dev.roy.coinkeeper.dto.*;
import dev.roy.coinkeeper.entity.RefreshToken;
import dev.roy.coinkeeper.entity.Role;
import dev.roy.coinkeeper.entity.User;
import dev.roy.coinkeeper.entity.UserOTP;
import dev.roy.coinkeeper.exception.InvalidCredentialsException;
import dev.roy.coinkeeper.exception.InvalidOTPException;
import dev.roy.coinkeeper.exception.InvalidRefreshTokenException;
import dev.roy.coinkeeper.repository.RefreshTokenRepository;
import dev.roy.coinkeeper.repository.UserOTPRepository;
import dev.roy.coinkeeper.repository.UserRepository;
import dev.roy.coinkeeper.service.MailService;
import dev.roy.coinkeeper.service.UserService;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final AuthenticationManager authManager;
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final UserService userService;
    private final RefreshTokenRepository tokenRepository;
    private final UserOTPRepository otpRepository;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;
    private final Random random = new Random();

    public UserResponseDTO register(UserRequestDTO dto) {
        UserResponseDTO userResponseDTO = userService.addUser(dto);
        Optional<User> savedUser = userRepository.findByEmail(userResponseDTO.email());
        savedUser.ifPresent(user -> {
            Integer otp = generateOTP(user);
            mailService.sendOTPViaEmail(user.getName(), user.getEmail(), otp);
        });
        return userResponseDTO;
    }

    public LoginResponseDTO login(LoginRequestDTO dto) {
        try {
            Authentication auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(dto.email(), dto.password()));
            String jwt = tokenService.generateJWT(auth);

            RefreshToken savedRefreshToken;
            User user = userService.getUser(dto.email());
            // Check if refresh token exists for the user
            Optional<RefreshToken> rtOpt = tokenRepository.findByUser(user);
            RefreshToken refreshToken;
            if (rtOpt.isPresent()) {
                refreshToken = rtOpt.get();
                refreshToken.setToken(UUID.randomUUID().toString());
                refreshToken.setExpiry(LocalDateTime.now().plusMinutes(60));
            } else {
                refreshToken = new RefreshToken();
                refreshToken.setToken(UUID.randomUUID().toString());
                refreshToken.setExpiry(LocalDateTime.now().plusMinutes(60));
                refreshToken.setUser(user);
            }
            savedRefreshToken = tokenRepository.save(refreshToken);
            Set<String> roles = user.getRoles().stream().map(Role::getName).collect(Collectors.toSet());
            return new LoginResponseDTO(jwt, savedRefreshToken, new UserResponseDTO(user.getId(), user.getName(), user.getEmail(), user.getPicture(), roles));
        } catch (AuthenticationException ex) {
            log.error("Authentication failed {}", ex.getMessage());
            throw new InvalidCredentialsException(ex.getMessage());
        }
    }

    public String getNewJwt(Cookie cookie) {
        String token = cookie.getValue();
        Optional<RefreshToken> tokenOpt = tokenRepository.findByToken(token);
        if (tokenOpt.isEmpty()) {
            throw new InvalidRefreshTokenException("Invalid Refresh token");
        }

        if (tokenOpt.get().getExpiry().isBefore(LocalDateTime.now())) {
            throw new InvalidRefreshTokenException("Expired Refresh token");
        }

        User user = userService.getUser(tokenOpt.get().getUser().getId());
        return tokenService.generateJWT(user);
    }

    public void deleteRefreshToken(Cookie cookie) {
        String token = cookie.getValue();
        Optional<RefreshToken> tokenOpt = tokenRepository.findByToken(token);
        if (tokenOpt.isEmpty()) {
            throw new InvalidRefreshTokenException("Invalid Refresh token");
        }
        tokenRepository.delete(tokenOpt.get());
    }

    public void verifyOTP(VerifyOTPRequestDTO dto) {
        User user = userService.getUser(dto.userId());
        Optional<UserOTP> userOTP = otpRepository.findByUser(user);
        if (userOTP.isEmpty()) {
            throw new InvalidOTPException("Invalid OTP");
        }
        if (!dto.otp().equals(userOTP.get().getOtp())) {
            throw new InvalidOTPException("OTP mismatch");
        }
        if (userOTP.get().getExpiry().isBefore(LocalDateTime.now())) {
            throw new InvalidOTPException("OTP expired");
        }
        // activate the user
        user.setActive(true);
        userRepository.save(user);
        // remove existing OTP entry
        otpRepository.delete(userOTP.get());
    }

    public String resendOTP(ResendOTPRequestDTO dto) {
        User user = userService.getUser(dto.userId());
        // Verify if User is not active then resend new OTP
        if (!user.isActive()) {
            Integer otp = generateOTP(user);
            mailService.sendOTPViaEmail(user.getName(), user.getEmail(), otp);
            return "OTP resend successful";
        }
        return "User is already activated, please login";
    }

    public void verifyEmailAndSendOTP(ForgetPasswordRequestDTO dto) {
        User user = userService.getUser(dto.email());
        Integer otp = generateOTP(user);
        mailService.sendOTPViaEmail(user.getName(), user.getEmail(), otp);
    }

    public void resetPassword(ResetPasswordRequestDTO dto) {
        // Verify the OTP of the user
        User user = userService.getUser(dto.email());
        Optional<UserOTP> userOTP = otpRepository.findByUser(user);
        if (userOTP.isEmpty()) {
            throw new InvalidOTPException("Invalid OTP");
        }
        if (!dto.otp().equals(userOTP.get().getOtp())) {
            throw new InvalidOTPException("OTP mismatch");
        }
        if (userOTP.get().getExpiry().isBefore(LocalDateTime.now())) {
            throw new InvalidOTPException("OTP expired");
        }
        user.setPassword(passwordEncoder.encode(dto.password()));
        userRepository.save(user);
        otpRepository.delete(userOTP.get());
    }

    private Integer generateOTP(User user) {
        log.info("Generating OTP for user: {}", user.getEmail());
        Optional<UserOTP> userOTP = otpRepository.findByUser(user);
        final int OTP = random.nextInt(100000, 999999);
        UserOTP tempOTP;
        if (userOTP.isEmpty()) {
            tempOTP = new UserOTP(0, OTP, LocalDateTime.now().plusMinutes(15), user);
        } else {
            tempOTP = userOTP.get();
            tempOTP.setOtp(OTP);
            tempOTP.setExpiry(LocalDateTime.now().plusMinutes(2));
        }
        otpRepository.save(tempOTP);
        return OTP;
    }
}
