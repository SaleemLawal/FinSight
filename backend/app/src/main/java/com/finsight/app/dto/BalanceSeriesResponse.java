package com.finsight.app.dto;

import java.util.List;

public class BalanceSeriesResponse {
  private String accountId;
  private String range;
  private String granularity;
  private String currency;
  private List<BalancePoint> points;

  public BalanceSeriesResponse() {}

  public BalanceSeriesResponse(
      String accountId,
      String range,
      String granularity,
      String currency,
      List<BalancePoint> points) {
    this.accountId = accountId;
    this.range = range;
    this.granularity = granularity;
    this.currency = currency;
    this.points = points;
  }

  public String getAccountId() {
    return accountId;
  }

  public void setAccountId(String accountId) {
    this.accountId = accountId;
  }

  public String getRange() {
    return range;
  }

  public void setRange(String range) {
    this.range = range;
  }

  public String getGranularity() {
    return granularity;
  }

  public void setGranularity(String granularity) {
    this.granularity = granularity;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public List<BalancePoint> getPoints() {
    return points;
  }

  public void setPoints(List<BalancePoint> points) {
    this.points = points;
  }
}
