package dev.roy.coinkeeper.repository;

import dev.roy.coinkeeper.entity.Budget;
import dev.roy.coinkeeper.entity.Transaction;
import dev.roy.coinkeeper.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    Page<Transaction> findTransactionByBudget(Budget budget, Pageable pageable);

    List<Transaction> findTransactionByBudget(Budget budget);

    Page<Transaction> findTransactionByUser(User user, PageRequest pageRequest);

    List<Transaction> findTransactionByUser(User user);
}
