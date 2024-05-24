package dev.roy.coinkeeper.repository;

import dev.roy.coinkeeper.entity.User;
import dev.roy.coinkeeper.entity.UserOTP;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserOTPRepository extends JpaRepository<UserOTP, Integer> {

    Optional<UserOTP> findByUser(User user);
}
