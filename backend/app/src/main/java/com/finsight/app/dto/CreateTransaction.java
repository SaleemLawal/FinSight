package com.finsight.app.dto;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateTransaction {
  private Date date;
  private String merchant;
  private String description;
  private Double amount;
  private Boolean isReviewed;
  private Boolean isRecurring;
  private String accountId;
  private String categoryId;
}
