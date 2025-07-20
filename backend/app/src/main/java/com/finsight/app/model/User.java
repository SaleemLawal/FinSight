package com.finsight.app.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import lombok.*;

@Entity
@Table(
    name = "users",
    uniqueConstraints = {@UniqueConstraint(columnNames = "email")})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
  @Id @GeneratedValue(strategy = GenerationType.UUID) private String id;
  private String name;
  private String email;
  private String password;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<Account> accounts;

  @Column(name = "created_at")
  private LocalDateTime createdAt;
}
