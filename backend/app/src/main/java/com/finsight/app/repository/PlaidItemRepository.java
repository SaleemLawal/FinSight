package com.finsight.app.repository;

import com.finsight.app.model.PlaidAccessToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaidItemRepository extends JpaRepository<PlaidAccessToken, String> {}
