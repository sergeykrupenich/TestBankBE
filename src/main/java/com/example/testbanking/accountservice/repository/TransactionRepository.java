package com.example.testbanking.accountservice.repository;

import com.example.testbanking.accountservice.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
//    List<Transaction> findAllBySourceAccountNumberOrTargetAccountNumberOrderByTimestampDesc(
//            String source, String target);
}
