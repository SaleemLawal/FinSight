package com.finsight.app.model;

import com.finsight.app.util.AccountType;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "accounts")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {
  @Id
  //  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  private String name;

  @Enumerated(EnumType.STRING)
  private AccountType type;

  private String institution_name;
  private String institutionId;
  private String lastFour;
  private Double balance;

  @Column(name = "user_id")
  private String userId;

  @CreatedDate
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  public Account(
      String id,
      String name,
      AccountType type,
      String institution_name,
      String institutionId,
      String lastFour,
      Double balance,
      String userId) {
    this.id = id;
    this.name = name;
    this.type = type;
    this.institution_name = institution_name;
    this.institutionId = institutionId;
    this.lastFour = lastFour;
    this.balance = balance;
    this.userId = userId;
  }
}
