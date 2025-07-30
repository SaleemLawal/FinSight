package com.finsight.app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "plaid_access_tokens")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaidAccessToken {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  private String itemId;
  private String accessToken;
  private String cursor = null;

  @Column(name = "user_id")
  private String userId;

  public PlaidAccessToken(String itemId, String accessToken, String userId) {
    this.itemId = itemId;
    this.accessToken = accessToken;
    this.userId = userId;
  }
}
