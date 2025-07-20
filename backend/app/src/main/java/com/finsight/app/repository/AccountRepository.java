package com.finsight.app.repository;

import com.finsight.app.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
//    Optional<UserModel> findByEmail(String email);
}
