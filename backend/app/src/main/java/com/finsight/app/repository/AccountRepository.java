package com.finsight.app.repository;

import com.finsight.app.model.Account;
import java.util.List;

import com.finsight.app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
    List<Account> findByUserId(String userId);
    boolean existsByUserAndLastFourAndInstitutionId(User user, String lastFour, String institutionId);
}
