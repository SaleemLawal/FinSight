package com.finsight.app.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateTransaction {
  private LocalDateTime date;
  private String merchant;
  private String description;
  private Double amount;
  private Boolean isReviewed;
  private Boolean isRecurring;
  private String accountId;
  private String categoryId;
}
