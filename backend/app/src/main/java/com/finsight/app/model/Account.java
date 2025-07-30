package com.finsight.app.model;

import com.finsight.app.util.AccountType;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
    public Account(String id, String name, AccountType type, String institution_name, String institutionId, String lastFour, Double balance, User user) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.institution_name = institution_name;
        this.institutionId = institutionId;
        this.lastFour = lastFour;
        this.balance = balance;
        this.user = user;
        this.transactions = new ArrayList<>();
    }

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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @OneToMany(
      cascade = CascadeType.ALL,
      mappedBy = "account",
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  private List<Transaction> transactions;

  @CreatedDate
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;
}
