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
//  @GeneratedValue(strategy = GenerationType.UUID)
  private String itemId;

  private String accessToken;
  private String institutionName;

  @Column(name = "user_id")
  private String userId;

}
