package com.finsight.app.dto;

import com.finsight.app.util.AccountType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Account {
  private Long id;
  private String name;
  private AccountType type;
  private String institution;
  private Long userId;
  private LocalDateTime createdAt;
}
