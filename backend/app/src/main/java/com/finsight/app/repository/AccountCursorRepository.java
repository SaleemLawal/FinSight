package com.finsight.app.repository;

import com.finsight.app.model.AccountCursor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountCursorRepository extends JpaRepository<AccountCursor, String> {
  Optional<AccountCursor> findByUserIdAndInstitutionIdAndLastFour(
      String userId, String institutionId, String lastFour);
}
