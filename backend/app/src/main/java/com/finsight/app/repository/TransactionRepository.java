package com.finsight.app.repository;

import com.finsight.app.model.Transaction;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {
  List<Transaction> findByUserId(String id);
  //    List<Account> findByUserId(String userId);
}
