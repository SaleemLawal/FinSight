package com.finsight.app.service;

import com.finsight.app.model.Account;
import com.finsight.app.repository.AccountRepository;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
  private final AccountRepository accountRepository;
  private final UserService userService;

  @Autowired
  AccountService(AccountRepository accountRepository, UserService userService) {
    this.accountRepository = accountRepository;
    this.userService = userService;
  }

  public com.finsight.app.dto.Account createAccount(Account account) {
    // get currently logged user
    com.finsight.app.model.User loggedUser = userService.getLoggedInUser(102L);
    account.setUser(loggedUser);

    // TODO make an account (Plaid API integration later)

    // save it
    Account createdAccount = accountRepository.save(account);
    return transformToDto(createdAccount);
  }

  public List<com.finsight.app.dto.Account> getAccounts() {
    com.finsight.app.dto.User loggedUser = userService.getUserDto(152L);

    return accountRepository.findAll().stream()
        .filter(account -> account.getUser().getEmail().equals(loggedUser.getEmail()))
        .map(this::transformToDto)
        .collect(Collectors.toList());
  }

  public com.finsight.app.dto.Account transformToDto(Account account) {
    return new com.finsight.app.dto.Account(
        account.getId(),
        account.getName(),
        account.getType(),
        account.getInstitution(),
        account.getUser().getId(),
        account.getCreatedAt());
  }
}
