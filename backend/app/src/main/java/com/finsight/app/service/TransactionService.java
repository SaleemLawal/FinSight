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
import com.finsight.app.repository.TransactionRepository;
import java.util.List;
import java.util.Objects;
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

  @Autowired
  TransactionService(
      TransactionRepository transactionRepository,
      UserService userService,
      CategoryRepository categoryRepository,
      AccountRepository accountRepository) {
    this.transactionRepository = transactionRepository;
    this.userService = userService;
    this.categoryRepository = categoryRepository;
    this.accountRepository = accountRepository;
  }

  public com.finsight.app.dto.Transaction createTransaction(
      CreateTransaction payload, String userId) throws Exception {
    com.finsight.app.model.Transaction transaction = new com.finsight.app.model.Transaction();
    com.finsight.app.model.User loggedUser = userService.getCurrentUser(userId);
    // find a corresponding account / category
    Optional<Account> account = accountRepository.findById(payload.getAccountId());
    if (account.isPresent()) {
      Optional<Category> category = categoryRepository.findById(payload.getCategoryId());
      if (category.isPresent()) {
        transaction.setUser(loggedUser);
        transaction.setAccount(account.get());
        transaction.setCategory(category.get());
        transaction.setDate(payload.getDate());
        transaction.setMerchant(payload.getMerchant());
        transaction.setDescription(payload.getDescription());
        transaction.setAmount(payload.getAmount());
        transaction.setIsReviewed(payload.getIsReviewed());
        transaction.setIsRecurring(payload.getIsRecurring());

        // save it
        com.finsight.app.model.Transaction createdTransaction =
            transactionRepository.save(transaction);
        return transformToDto(createdTransaction);
      } else {
        throw new CategoryNotFoundException("Category not found");
      }
    } else {
      throw new AccountNotFoundException("Account not found");
    }
  }

  public List<com.finsight.app.dto.Transaction> getTransactions(String userId) throws Exception {
    com.finsight.app.dto.User loggedUser = userService.getCurrentUserDto(userId);
    if (!Objects.equals(loggedUser.getId(), userId)) {
      throw new UnauthorizedAccessException("Unauthorized user");
    }

    return transactionRepository.findByUserId(loggedUser.getId()).stream()
        .map(this::transformToDto)
        .collect(Collectors.toList());
  }

  public com.finsight.app.dto.Transaction updateTransaction(
      String transactionId, UpdateTransactionRequest updateRequest, String userId)
      throws Exception {
    com.finsight.app.model.User loggedUser = userService.getCurrentUser(userId);
    com.finsight.app.model.Transaction transactionToUpdate =
        transactionRepository
            .findById(transactionId)
            .orElseThrow(
                () ->
                    new TransactionNotFoundException(
                        "Transaction not found with id: " + transactionId));

    if (!transactionToUpdate.getUser().getId().equals(loggedUser.getId())) {
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
      // check if the category is valid
      if (categoryRepository.existsById(updateRequest.getCategoryId())) {
        transactionToUpdate.setIsReviewed(updateRequest.getIsReviewed());
      }
    }

    com.finsight.app.model.Transaction updatedTransaction =
        transactionRepository.save(transactionToUpdate);
    return transformToDto(updatedTransaction);
  }

  public void deleteTransaction(String transactionId, String userId) throws Exception {
    com.finsight.app.model.User loggedUser = userService.getCurrentUser(userId);
    com.finsight.app.model.Transaction transactionToUpdate =
        transactionRepository
            .findById(transactionId)
            .orElseThrow(
                () ->
                    new TransactionNotFoundException(
                        "Transaction not found with id: " + transactionId));
    if (!transactionToUpdate.getUser().getId().equals(loggedUser.getId())) {
      throw new UnauthorizedAccessException("You are not authorized to delete this transaction");
    }
    transactionRepository.deleteById(transactionId);
  }

  public com.finsight.app.dto.Transaction transformToDto(
      com.finsight.app.model.Transaction transaction) {
    return new com.finsight.app.dto.Transaction(
        transaction.getId(),
        transaction.getDate(),
        transaction.getMerchant(),
        transaction.getDescription(),
        transaction.getAmount(),
        transaction.getIsReviewed(),
        transaction.getIsRecurring(),
        transaction.getAccount().getId(),
        transaction.getCategory().getId(),
        transaction.getUser().getId(),
        transaction.getCreatedAt(),
        transaction.getUpdatedAt());
  }
}
