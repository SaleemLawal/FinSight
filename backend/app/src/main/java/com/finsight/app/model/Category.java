package com.finsight.app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @NotNull private String name;
  @NotNull private String color;
  private String icon;

  private boolean isDefault;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;
}
