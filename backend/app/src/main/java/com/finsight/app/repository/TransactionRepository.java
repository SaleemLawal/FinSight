package com.finsight.app.repository;

import com.finsight.app.model.Transaction;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {
  List<Transaction> findByUserId(String id);
List<Transaction> findByAccountId(String accountId);

    List<Transaction> findByAccountIdOrderByDateDesc(String accountId);

    @Query("SELECT t FROM Transaction t WHERE t.accountId = :accountId AND t.date >= :startDate AND t.date <= :endDate AND t.isPending = false ORDER BY t.date")
    List<Transaction> findSettledTransactionsByAccountAndDateRange(@Param("accountId") String accountId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.accountId = :accountId AND t.date > :startDate AND t.date <= :endDate AND t.isPending = false")
    Double sumTransactionAmountsByAccountAfterDate(@Param("accountId") String accountId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT MIN(t.date) FROM Transaction t WHERE t.accountId = :accountId AND t.isPending = false")
    LocalDateTime findEarliestTransactionDateByAccount(@Param("accountId") String accountId);
}
