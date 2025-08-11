package com.finsight.app.repository;

import com.finsight.app.model.PlaidAccessToken;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaidAccessTokenRepository extends JpaRepository<PlaidAccessToken, String> {
  PlaidAccessToken findByAccessToken(String accessToken);

  List<PlaidAccessToken> findByUserId(String userId);

  boolean existsByUserIdAndInstitutionName(String userId, String institutionName);

  Optional<PlaidAccessToken> findByUserIdAndInstitutionName(String userId, String institutionName);
}
