package com.finsight.app.dto;

import com.finsight.app.util.AccountType;
import lombok.Data;

@Data
public class UpdateAccountRequest {
  private String name;
  private AccountType type;
  private String institution;
}
