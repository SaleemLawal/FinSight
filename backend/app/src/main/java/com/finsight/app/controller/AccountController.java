package com.finsight.app.controller;

import com.finsight.app.dto.BalanceSeriesResponse;
import com.finsight.app.dto.UpdateAccountRequest;
import com.finsight.app.model.Account;
import com.finsight.app.service.AccountService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/user/accounts")
public class AccountController {
  private final AccountService accountService;

  @Autowired
  public AccountController(AccountService accountService) {
    this.accountService = accountService;
  }

  @GetMapping()
  public ResponseEntity<List<com.finsight.app.dto.Account>> getAccounts(HttpServletRequest request)
      throws Exception {
    String userId = (String) request.getSession().getAttribute("userId");
    if (userId == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    return ResponseEntity.status(HttpStatus.OK).body(accountService.getAccounts(userId));
  }

  @PostMapping()
  public ResponseEntity<com.finsight.app.dto.Account> createAccount(
      @Valid @RequestBody Account account, HttpServletRequest request) throws Exception {
    String userId = (String) request.getSession().getAttribute("userId");
    if (userId == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    com.finsight.app.dto.Account accountCreated = accountService.createAccount(account, userId);
    return ResponseEntity.status(HttpStatus.CREATED).body(accountCreated);
  }

  @PutMapping("/{accountId}")
  public ResponseEntity<com.finsight.app.dto.Account> updateAccount(
      @PathVariable String accountId,
      @Valid @RequestBody UpdateAccountRequest updateRequest,
      HttpServletRequest request)
      throws Exception {
    String userId = (String) request.getSession().getAttribute("userId");
    if (userId == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    com.finsight.app.dto.Account updatedAccount =
        accountService.updateAccount(accountId, updateRequest, userId);
    return ResponseEntity.ok(updatedAccount);
  }

  @DeleteMapping("/{accountId}")
  public ResponseEntity<Map<String, Object>> deleteAccount(
      @PathVariable String accountId, HttpServletRequest request) throws Exception {
    String userId = (String) request.getSession().getAttribute("userId");
    if (userId == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    accountService.deleteAccount(accountId, userId);

    Map<String, Object> response = new HashMap<>();
    response.put("timestamp", LocalDateTime.now());
    response.put("status", HttpStatus.OK.value());
    response.put("message", "Account with id " + accountId + " deleted successfully");

    return ResponseEntity.ok(response);
  }

  @GetMapping("/{accountId}/balance-history")
  public ResponseEntity<BalanceSeriesResponse> getBalanceSeries(
          @PathVariable String accountId,
          @RequestParam(defaultValue = "1M") String range,
          @RequestParam(defaultValue = "day") String granularity) {

      // Validate range parameter
      if (!Arrays.asList("1W", "1M", "3M", "YTD", "1Y", "ALL").contains(range.toUpperCase())) {
          throw new IllegalArgumentException("Invalid range. Must be one of: 1W, 1M, 3M, YTD, 1Y, ALL");
      }

      // Validate granularity parameter
      if (!Arrays.asList("day", "week", "month").contains(granularity.toLowerCase())) {
          throw new IllegalArgumentException("Invalid granularity. Must be one of: day, week, month");
      }

      BalanceSeriesResponse response = accountService.getBalanceSeries(accountId, range, granularity);
      return ResponseEntity.ok(response);
  }
}
