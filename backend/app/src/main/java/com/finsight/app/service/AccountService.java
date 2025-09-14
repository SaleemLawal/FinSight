package com.finsight.app.service;

import com.finsight.app.dto.BalancePoint;
import com.finsight.app.dto.BalanceSeriesResponse;
import com.finsight.app.dto.UpdateAccountRequest;
import com.finsight.app.exception.AccountNotFoundException;
import com.finsight.app.exception.UnauthorizedAccessException;
import com.finsight.app.model.Account;
import com.finsight.app.model.Transaction;
import com.finsight.app.repository.AccountRepository;
import com.finsight.app.repository.TransactionRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
  private final AccountRepository accountRepository;
  private final UserService userService;
  private final TransactionRepository transactionRepository;

  @Autowired
  AccountService(AccountRepository accountRepository, UserService userService, TransactionRepository transactionRepository) {
    this.accountRepository = accountRepository;
    this.userService = userService;
      this.transactionRepository = transactionRepository;
  }

  public com.finsight.app.dto.Account createAccount(Account account, String userId)
      throws Exception {
    // Validate user exists
    userService.getCurrentUser(userId);
    account.setUserId(userId);
    Account createdAccount = accountRepository.save(account);
    return transformToDto(createdAccount);
  }

  public List<com.finsight.app.dto.Account> getAccounts(String userId) throws Exception {
    // Validate user exists
    userService.getCurrentUser(userId);

    return accountRepository.findByUserId(userId).stream()
        .map(this::transformToDto)
        .collect(Collectors.toList());
  }

  public com.finsight.app.dto.Account updateAccount(
      String accountId, UpdateAccountRequest updateRequest, String userId) throws Exception {
    // Validate user exists
    userService.getCurrentUser(userId);

    Account accountToUpdate =
        accountRepository
            .findById(accountId)
            .orElseThrow(
                () -> new AccountNotFoundException("Account not found with id: " + accountId));

    if (!accountToUpdate.getUserId().equals(userId)) {
      throw new UnauthorizedAccessException("You are not authorized to update this account");
    }

    if (updateRequest.getName() != null) {
      accountToUpdate.setName(updateRequest.getName());
    }
    if (updateRequest.getType() != null) {
      accountToUpdate.setType(updateRequest.getType());
    }
    if (updateRequest.getInstitution() != null) {
      accountToUpdate.setInstitution_name(updateRequest.getInstitution());
    }
    if (updateRequest.getBalance() != null) {
      accountToUpdate.setBalance(updateRequest.getBalance());
    }
    if (updateRequest.getLast4() != null) {
      accountToUpdate.setLastFour(updateRequest.getLast4());
    }
    Account updatedAccount = accountRepository.save(accountToUpdate);
    return transformToDto(updatedAccount);
  }

  public void deleteAccount(String accountId, String userId) throws Exception {
    userService.getCurrentUser(userId);

    Account accountToUpdate =
        accountRepository
            .findById(accountId)
            .orElseThrow(
                () -> new AccountNotFoundException("Account not found with id: " + accountId));
    if (!accountToUpdate.getUserId().equals(userId)) {
      throw new UnauthorizedAccessException("You are not authorized to delete this account");
    }
    accountRepository.deleteById(accountId);
  }

  public com.finsight.app.dto.Account transformToDto(Account account) {
    return new com.finsight.app.dto.Account(
        account.getAccountId(),
        account.getName(),
        account.getType(),
        account.getInstitution_name(),
        account.getInstitutionId(),
        account.getLastFour(),
        account.getBalance(),
        account.getUserId(),
        account.getCreatedAt());
  }

  public BalanceSeriesResponse getBalanceSeries(
      String accountId, String range, String granularity) {
    Account account =
        accountRepository
            .findById(accountId)
            .orElseThrow(() -> new AccountNotFoundException("Account not found"));

      LocalDateTime endDate = LocalDateTime.now();
      LocalDateTime startDate = calculateStartDate(accountId, range, endDate);

    // Calculate opening balance at start date
    BigDecimal currentBalance = BigDecimal.valueOf(account.getBalance());
      BigDecimal transactionsAfterStart =
          BigDecimal.valueOf(transactionRepository.sumTransactionAmountsByAccountAfterDate(
              accountId, startDate, endDate));
      BigDecimal openingBalance = currentBalance.subtract(transactionsAfterStart);

    // Get all transactions in the date range
    List<Transaction> transactions =
        transactionRepository.findSettledTransactionsByAccountAndDateRange(
            accountId, startDate, endDate);

    // Group transactions by date
    Map<LocalDate, BigDecimal> dailyNetAmounts =
        transactions.stream()
            .collect(
                Collectors.groupingBy(
                    transaction -> transaction.getDate().toLocalDate(),
                    Collectors.reducing(
                        BigDecimal.ZERO,
                        transaction -> BigDecimal.valueOf(transaction.getAmount()),
                        BigDecimal::add)));

    // Generate all dates in range (using LocalDate for daily iteration)
    LocalDate startLocalDate = startDate.toLocalDate();
    LocalDate endLocalDate = endDate.toLocalDate();
    List<LocalDate> allDates = startLocalDate.datesUntil(endLocalDate.plusDays(1))
        .toList();

    // Calculate cumulative balances
    List<BalancePoint> points = new ArrayList<>();
    BigDecimal runningBalance = openingBalance;

    for (LocalDate date : allDates) {
      BigDecimal dailyNet = dailyNetAmounts.getOrDefault(date, BigDecimal.ZERO);
      runningBalance = runningBalance.add(dailyNet);
      points.add(new BalancePoint(date, runningBalance));
    }

    return new BalanceSeriesResponse(
        accountId,
        range,
        granularity,
        "USD",
        points);
  }

  public String getAccountName(String accountId) {
//      Optional<Account> account = accountRepository.findById(accountId);
//      if (account.isPresent()){
//          return account.get().getName();
//      }
      return accountRepository.findById(accountId).get().getName();
  }

  private LocalDateTime calculateStartDate(String accountId, String range, LocalDateTime endDate) {
      return switch (range.toUpperCase()) {
          case "1W" -> endDate.minusDays(7);
          case "1M" -> endDate.minusDays(30);
          case "3M" -> endDate.minusDays(90);
          case "YTD" -> LocalDateTime.of(endDate.getYear(), 1, 1, 0, 0);
          case "1Y" -> endDate.minusDays(365);
          case "ALL" -> {
              LocalDateTime earliestDate =
                  transactionRepository.findEarliestTransactionDateByAccount(accountId);
              yield earliestDate != null ? earliestDate : endDate.minusDays(30);
          }
          default -> throw new IllegalArgumentException("Invalid range: " + range);
      };
  }
}
