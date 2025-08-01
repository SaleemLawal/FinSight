package com.finsight.app.service;

import com.finsight.app.dto.CreateTransaction;
import com.finsight.app.dto.UpdateTransactionRequest;
import com.finsight.app.exception.AccountNotFoundException;
import com.finsight.app.exception.CategoryNotFoundException;
import com.finsight.app.exception.TransactionNotFoundException;
import com.finsight.app.exception.UnauthorizedAccessException;
import com.finsight.app.model.Account;
import com.finsight.app.model.Category;
import com.finsight.app.repository.AccountRepository;
import com.finsight.app.repository.CategoryRepository;
import com.finsight.app.repository.PlaidAccessTokenRepository;
import com.finsight.app.repository.TransactionRepository;
import com.finsight.app.util.TransactionSyncResult;
import com.plaid.client.model.Transaction;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {
  private final TransactionRepository transactionRepository;
  private final UserService userService;
  private final CategoryRepository categoryRepository;
  private final AccountRepository accountRepository;
  private final PlaidAccessTokenRepository plaidAccessTokenRepository;

  @Autowired
  TransactionService(
      TransactionRepository transactionRepository,
      UserService userService,
      CategoryRepository categoryRepository,
      AccountRepository accountRepository, PlaidAccessTokenRepository plaidAccessTokenRepository) {
    this.transactionRepository = transactionRepository;
    this.userService = userService;
    this.categoryRepository = categoryRepository;
    this.accountRepository = accountRepository;
      this.plaidAccessTokenRepository = plaidAccessTokenRepository;
  }

  public com.finsight.app.dto.Transaction createTransaction(
      CreateTransaction payload, String userId) throws Exception {
    com.finsight.app.model.Transaction transaction = new com.finsight.app.model.Transaction();
    userService.getCurrentUser(userId);

    // Validate account exists and belongs to user
    Optional<Account> account = accountRepository.findById(payload.getAccountId());
    if (account.isEmpty()) {
      throw new AccountNotFoundException("Account not found");
    }
    if (!account.get().getUserId().equals(userId)) {
      throw new UnauthorizedAccessException("Account does not belong to user");
    }

    Optional<Category> category = categoryRepository.findById(payload.getCategoryId());
    if (category.isEmpty()) {
      throw new CategoryNotFoundException("Category not found");
    }
    if (!category.get().getUserId().equals(userId)) {
      throw new UnauthorizedAccessException("Category does not belong to user");
    }

    transaction.setUserId(userId);
    transaction.setAccountId(payload.getAccountId());
    transaction.setCategoryId(payload.getCategoryId());
    transaction.setDate(payload.getDate());
    transaction.setMerchant(payload.getMerchant());
    transaction.setDescription(payload.getDescription());
    transaction.setAmount(payload.getAmount());
    transaction.setIsReviewed(payload.getIsReviewed());
    transaction.setIsRecurring(payload.getIsRecurring());

    com.finsight.app.model.Transaction createdTransaction = transactionRepository.save(transaction);
    return transformToDto(createdTransaction);
  }

  public List<com.finsight.app.dto.Transaction> getTransactions(String userId) throws Exception {
    userService.getCurrentUser(userId);

    return transactionRepository.findByUserId(userId).stream()
        .map(this::transformToDto)
        .collect(Collectors.toList());
  }

  public com.finsight.app.dto.Transaction updateTransaction(
      String transactionId, UpdateTransactionRequest updateRequest, String userId)
      throws Exception {
    userService.getCurrentUser(userId);

    com.finsight.app.model.Transaction transactionToUpdate =
        transactionRepository
            .findById(transactionId)
            .orElseThrow(
                () ->
                    new TransactionNotFoundException(
                        "Transaction not found with id: " + transactionId));

    if (!transactionToUpdate.getUserId().equals(userId)) {
      throw new UnauthorizedAccessException("You are not authorized to update this transaction");
    }

    if (updateRequest.getDate() != null) {
      transactionToUpdate.setDate(updateRequest.getDate());
    }
    if (updateRequest.getMerchant() != null) {
      transactionToUpdate.setMerchant(updateRequest.getMerchant());
    }
    if (updateRequest.getDescription() != null) {
      transactionToUpdate.setDescription(updateRequest.getDescription());
    }
    if (updateRequest.getAmount() != null) {
      transactionToUpdate.setAmount(updateRequest.getAmount());
    }
    if (updateRequest.getIsRecurring() != null) {
      transactionToUpdate.setIsRecurring(updateRequest.getIsRecurring());
    }
    if (updateRequest.getIsReviewed() != null) {
      transactionToUpdate.setIsReviewed(updateRequest.getIsReviewed());
    }

    if (updateRequest.getCategoryId() != null) {
      // Validate category exists and belongs to user
      Optional<Category> category = categoryRepository.findById(updateRequest.getCategoryId());
      if (category.isEmpty()) {
        throw new CategoryNotFoundException("Category not found");
      }
      if (!category.get().getUserId().equals(userId)) {
        throw new UnauthorizedAccessException("Category does not belong to user");
      }
      transactionToUpdate.setCategoryId(updateRequest.getCategoryId());
    }

    com.finsight.app.model.Transaction updatedTransaction =
        transactionRepository.save(transactionToUpdate);
    return transformToDto(updatedTransaction);
  }

  public void deleteTransaction(String transactionId, String userId) throws Exception {
    userService.getCurrentUser(userId);

    com.finsight.app.model.Transaction transactionToUpdate =
        transactionRepository
            .findById(transactionId)
            .orElseThrow(
                () ->
                    new TransactionNotFoundException(
                        "Transaction not found with id: " + transactionId));
    if (!transactionToUpdate.getUserId().equals(userId)) {
      throw new UnauthorizedAccessException("You are not authorized to delete this transaction");
    }
    transactionRepository.deleteById(transactionId);
  }

  public void SyncTransactionsToDB(TransactionSyncResult transactionSyncResult, String userId) {
//    PlaidAccessToken plaidAccessToken = transactionSyncResult.getPlaidAccessToken();

    for (Transaction txn : transactionSyncResult.getAdded()) {
      if (!transactionRepository.existsById(txn.getTransactionId())) {
        transactionRepository.save(toEntity(txn, userId));
      }
    }

    for (Transaction txn : transactionSyncResult.getModified()) {
      transactionRepository.save(toEntity(txn, userId));
    }

    for (com.plaid.client.model.RemovedTransaction txn : transactionSyncResult.getRemoved()) {
      transactionRepository.deleteById(txn.getTransactionId());
    }
  }

  private com.finsight.app.dto.Transaction transformToDto(
      com.finsight.app.model.Transaction transaction) {
    return new com.finsight.app.dto.Transaction(
        transaction.getTransactionId(),
        transaction.getDate(),
        transaction.getMerchant(),
        transaction.getDescription(),
        transaction.getAmount(),
        transaction.getIsReviewed(),
        transaction.getIsRecurring(),
        transaction.getAccountId(),
        transaction.getCategoryId(),
        transaction.getUserId(),
        transaction.getCreatedAt(),
        transaction.getUpdatedAt());
  }

  private com.finsight.app.model.Transaction toEntity(Transaction txn, String userId) {
    return new com.finsight.app.model.Transaction(
        txn.getTransactionId(),
        txn.getDate().atStartOfDay(),
        txn.getMerchantName(),
        txn.getAmount(),
        txn.getPending(),
        txn.getAccountId(),
        txn.getCategoryId(),
        userId);
  }
}
