package com.finsight.app.controller;

import com.finsight.app.dto.UpdateAccountRequest;
import com.finsight.app.model.Account;
import com.finsight.app.service.AccountService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/{userId}/accounts")
public class AccountController {
  private final AccountService accountService;

  @Autowired
  public AccountController(AccountService accountService) {
    this.accountService = accountService;
  }

  @GetMapping()
  public ResponseEntity<List<com.finsight.app.dto.Account>> getAccounts(@PathVariable String userId) throws Exception {
    return ResponseEntity.status(HttpStatus.OK).body(accountService.getAccounts(userId));
  }

  @PostMapping()
  public ResponseEntity<com.finsight.app.dto.Account> createAccount(@RequestBody Account account, @PathVariable String userId) throws Exception {
    com.finsight.app.dto.Account accountCreated = accountService.createAccount(account, userId);
    return ResponseEntity.status(HttpStatus.CREATED).body(accountCreated);
  }

  @PutMapping("/{accountId}")
  public ResponseEntity<com.finsight.app.dto.Account> updateAccount(
      @PathVariable Long accountId, @RequestBody UpdateAccountRequest updateRequest, @PathVariable String userId) throws Exception {

    com.finsight.app.dto.Account updatedAccount = accountService.updateAccount(accountId, updateRequest, userId);
    return ResponseEntity.ok(updatedAccount);
  }

  @DeleteMapping("/{accountId}")
  public ResponseEntity<String> deleteAccount(@PathVariable Long accountId, @PathVariable String userId) throws Exception {

    accountService.deleteAccount(accountId, userId);
    return ResponseEntity.ok("Success, account with id " + accountId + " deleted");
  }
}
