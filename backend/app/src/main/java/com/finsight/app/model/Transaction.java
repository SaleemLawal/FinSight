package com.finsight.app.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "transactions")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
  @Id
  //  @GeneratedValue(strategy = GenerationType.UUID)
  private String transactionId;

  private LocalDateTime date;
  private String merchant;
  private String description = "";
  private Double amount;

  @Column(name = "is_reviewed")
  private Boolean isReviewed = false;

  @Column(name = "is_recurring")
  private Boolean isRecurring = false;

  @Column(name = "is_pending")
  private Boolean isPending = false;

  @Column(name = "account_id")
  private String accountId;

  @Column(name  = "account_name")
  private String account;

  @Column(name = "category_id")
  private String categoryId;

  @Column(name = "user_id")
  private String userId;

  @CreatedDate
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  public Transaction(
      String transactionId,
      LocalDateTime date,
      String merchant,
      Double amount,
      Boolean isPending,
      String accountId,
      String categoryId,
      String userId,
      String accountName) {
    this.transactionId = transactionId;
    this.date = date;
    this.merchant = merchant;
    this.amount = amount;
    this.isPending = isPending;
    this.accountId = accountId;
    this.categoryId = categoryId;
    this.userId = userId;
    this.account = accountName;
  }
}
