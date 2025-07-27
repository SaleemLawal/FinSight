package com.finsight.app.controller;

import com.finsight.app.dto.CreateTransaction;
import com.finsight.app.dto.UpdateTransactionRequest;
import com.finsight.app.service.TransactionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/transactions")
public class TransactionController {
  private final TransactionService transactionService;

  @Autowired
  public TransactionController(TransactionService transactionService) {
    this.transactionService = transactionService;
  }

  @GetMapping()
  public ResponseEntity<List<com.finsight.app.dto.Transaction>> getAccounts(
      HttpServletRequest request) throws Exception {
      String userId = (String) request.getSession().getAttribute("userId");
      if (userId == null) {
          return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
      }
    return ResponseEntity.status(HttpStatus.OK).body(transactionService.getTransactions(userId));
  }

  @PostMapping()
  public ResponseEntity<com.finsight.app.dto.Transaction> createTransaction(
      @Valid @RequestBody CreateTransaction transaction, HttpServletRequest request)
      throws Exception {
      String userId = (String) request.getSession().getAttribute("userId");
      if (userId == null) {
          return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
      }
    com.finsight.app.dto.Transaction transactionCreated =
        transactionService.createTransaction(transaction, userId);
    return ResponseEntity.status(HttpStatus.CREATED).body(transactionCreated);
  }

  @PutMapping("/{transactionId}")
  public ResponseEntity<com.finsight.app.dto.Transaction> updateTransaction(
      @PathVariable String transactionId,
      @Valid @RequestBody UpdateTransactionRequest updateRequest,
      HttpServletRequest request)
      throws Exception {
      String userId = (String) request.getSession().getAttribute("userId");
      if (userId == null) {
          return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
      }
    com.finsight.app.dto.Transaction updatedTransaction =
        transactionService.updateTransaction(transactionId, updateRequest, userId);
    return ResponseEntity.ok(updatedTransaction);
  }

  @DeleteMapping("/{transactionId}")
  public ResponseEntity<Map<String, Object>> deleteTransaction(
      @PathVariable String transactionId, HttpServletRequest request) throws Exception {
      String userId = (String) request.getSession().getAttribute("userId");
      if (userId == null) {
          return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
      }
    transactionService.deleteTransaction(transactionId, userId);

    Map<String, Object> response = new HashMap<>();
    response.put("timestamp", LocalDateTime.now());
    response.put("status", HttpStatus.OK.value());
    response.put("message", "Transaction with id " + transactionId + " deleted successfully");

    return ResponseEntity.ok(response);
  }
}
