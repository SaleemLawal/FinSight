package com.finsight.app.dto;

import com.finsight.app.util.AccountType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Account {
  private String id;
  private String name;
  private AccountType type;
  private String institution_name;
    private String institution_id;
    private String last_four;
  private Double balance;
  private String userId;
  private LocalDateTime createdAt;
}
