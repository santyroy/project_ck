package dev.roy.coinkeeper;

import dev.roy.coinkeeper.entity.Role;
import dev.roy.coinkeeper.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    CommandLineRunner runner(RoleRepository roleRepository) {
        return args -> {
            Role adminRole = new Role(1, "ADMIN");
            Role userRole = new Role(2, "USER");
            if (roleRepository.findByName("ADMIN").isEmpty()) {
                roleRepository.save(adminRole);
            }
            if (roleRepository.findByName("USER").isEmpty()) {
                roleRepository.save(userRole);
            }
        };
    }
}
