package com.finsight.app.model;

import com.finsight.app.util.AccountType;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

@Entity
@Table(name = "accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {
  @Id @GeneratedValue() private Long id;

  private String name;

  @Enumerated(EnumType.STRING)
  private AccountType type;

  private String institution;
  private String last4;
  private Double balance;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @CreatedDate
  @Column(name = "created_at")
  private LocalDateTime createdAt;
}
