package com.finsight.app.repository;

import com.finsight.app.model.PlaidAccessToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaidAccessTokenRepository extends JpaRepository<PlaidAccessToken, String> {
  PlaidAccessToken findByAccessToken(String accessToken);
}
