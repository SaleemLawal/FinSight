package com.finsight.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(
    name = "users",
    uniqueConstraints = {@UniqueConstraint(columnNames = "email")})
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @NotBlank(message = "Name is required")
  @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
  private String name;

  @NotBlank(message = "Email is required")
  @Email(message = "Please provide a valid email address")
  private String email;

  @NotBlank(message = "Password is required")
  @Size(min = 6, message = "Password must be at least 6 characters")
  private String password;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<Account> accounts;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PlaidItem> plaidItems;

  @OneToMany(
      mappedBy = "user",
      cascade = CascadeType.ALL,
      fetch = FetchType.LAZY,
      orphanRemoval = true)
  private List<Category> categories;

  @OneToMany(
      mappedBy = "user",
      cascade = CascadeType.ALL,
      fetch = FetchType.LAZY,
      orphanRemoval = true)
  private List<Transaction> transactions;

  @CreatedDate
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;
}
