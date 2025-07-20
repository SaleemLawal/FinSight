package com.finsight.app.service;

import com.finsight.app.dto.UpdateAccountRequest;
import com.finsight.app.dto.User;
import com.finsight.app.exception.UnauthorizedAccessException;
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

  public com.finsight.app.dto.Account createAccount(Account account, String userId) throws Exception {
    com.finsight.app.model.User loggedUser = userService.getCurrentUser(userId);
    account.setUser(loggedUser);

    // TODO make an account (Plaid API integration later)

    // save it
    Account createdAccount = accountRepository.save(account);
    return transformToDto(createdAccount);
  }

  public List<com.finsight.app.dto.Account> getAccounts(String userId) throws Exception {
    com.finsight.app.dto.User loggedUser = userService.getCurrentUserDto(userId);

    return accountRepository.findAll().stream()
        .filter(account -> account.getUser().getEmail().equals(loggedUser.getEmail()))
        .map(this::transformToDto)
        .collect(Collectors.toList());
  }

  public com.finsight.app.dto.Account updateAccount(
      Long accountId, UpdateAccountRequest updateRequest, String userId) throws Exception {
    com.finsight.app.model.User loggedUser = userService.getCurrentUser(userId);
    Account accountToUpdate =
        accountRepository
            .findById(accountId)
            .orElseThrow(() -> new RuntimeException("Account not found with id: " + accountId));

    if (!accountToUpdate.getUser().getId().equals(loggedUser.getId())) {
      throw new UnauthorizedAccessException("You are not authorized to update this account");
    }

    if (updateRequest.getName() != null) {
      accountToUpdate.setName(updateRequest.getName());
    }
    if (updateRequest.getType() != null) {
      accountToUpdate.setType(updateRequest.getType());
    }
    if (updateRequest.getInstitution() != null) {
      accountToUpdate.setInstitution(updateRequest.getInstitution());
    }
    if (updateRequest.getBalance() != null){
        accountToUpdate.setBalance(updateRequest.getBalance());
    }
      if (updateRequest.getLast4() != null){
          accountToUpdate.setLast4(updateRequest.getLast4());
      }
    Account updatedAccount = accountRepository.save(accountToUpdate);
    return transformToDto(updatedAccount);
  }

  public void deleteAccount(Long accountId, String userId) throws Exception {
    com.finsight.app.model.User loggedUser = userService.getCurrentUser(userId);
    Account accountToUpdate =
        accountRepository
            .findById(accountId)
            .orElseThrow(() -> new RuntimeException("Account not found with id: " + accountId));
    if (!accountToUpdate.getUser().getId().equals(loggedUser.getId())) {
      throw new UnauthorizedAccessException("You are not authorized to update this account");
    }
    accountRepository.deleteById(accountId);
  }

  public com.finsight.app.dto.Account transformToDto(Account account) {
    return new com.finsight.app.dto.Account(
        account.getId(),
        account.getName(),
        account.getType(),
        account.getInstitution(),
        account.getLast4(),
        account.getBalance(),
        account.getUser().getId(),
        account.getCreatedAt());
  }
}
