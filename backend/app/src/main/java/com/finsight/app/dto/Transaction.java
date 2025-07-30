package com.finsight.app.dto;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
  private String id;
  private LocalDateTime date;
  private String merchant;
  private String description;
  private Double amount;
  private Boolean isReviewed;
  private Boolean isRecurring;
  private String accountId;
  private String categoryId;
  private String userId;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
