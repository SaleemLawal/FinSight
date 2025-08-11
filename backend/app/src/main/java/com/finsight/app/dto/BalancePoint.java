package com.finsight.app.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

public class BalancePoint {
  @Getter
  @Setter
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate date;

  private BigDecimal balance;

  public BalancePoint() {}

  public BalancePoint(LocalDate date, BigDecimal balance) {
    this.date = date;
    this.balance = balance;
  }

  public BigDecimal getBalance() {
    return balance;
  }

  public void setBalance(BigDecimal balance) {
    this.balance = balance;
  }
}
