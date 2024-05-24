package dev.roy.coinkeeper.repository;

import dev.roy.coinkeeper.entity.Budget;
import dev.roy.coinkeeper.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BudgetRepository extends JpaRepository<Budget, Integer> {

    Page<Budget> findByUser(User user, Pageable pageable);
    Page<Budget> findByMembers(List<User> members, Pageable pageable);
}
