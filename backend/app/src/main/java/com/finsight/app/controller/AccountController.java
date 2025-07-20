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
@RequestMapping("api/account")
public class AccountController {
  private final AccountService accountService;

  @Autowired
  public AccountController(AccountService accountService) {
    this.accountService = accountService;
  }

  @GetMapping("/")
  public ResponseEntity<List<com.finsight.app.dto.Account>> getAccounts() {
    return ResponseEntity.status(HttpStatus.OK).body(accountService.getAccounts());
  }

  @PostMapping("/")
  public ResponseEntity<com.finsight.app.dto.Account> createAccount(@RequestBody Account account) {
    com.finsight.app.dto.Account accountCreated = accountService.createAccount(account);
    return ResponseEntity.status(HttpStatus.CREATED).body(accountCreated);
  }

  @PutMapping("/{id}")
  public ResponseEntity<com.finsight.app.dto.Account> updateAccount(
      @PathVariable Long id, @RequestBody UpdateAccountRequest updateRequest) {

    com.finsight.app.dto.Account updatedAccount = accountService.updateAccount(id, updateRequest);
    return ResponseEntity.ok(updatedAccount);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<String> deleteAccount(@PathVariable Long id) {

    accountService.deleteAccount(id);
    return ResponseEntity.ok("Success, account with id " + id + " deleted");
  }
}
