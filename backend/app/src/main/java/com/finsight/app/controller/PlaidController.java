package com.finsight.app.controller;

import com.finsight.app.repository.PlaidAccessTokenRepository;
import com.finsight.app.service.PlaidAccessTokenService;
import com.finsight.app.service.PlaidService;
import com.plaid.client.model.*;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import retrofit2.Response;

@RestController
@RequestMapping("/api/plaid")
public class PlaidController {
  private final PlaidService plaidService;
  private final PlaidAccessTokenService plaidAccessTokenService;
  private final PlaidAccessTokenRepository plaidAccessTokenRepository;

  @Autowired
  private PlaidController(
      PlaidService plaidService,
      PlaidAccessTokenService plaidAccessTokenService,
      PlaidAccessTokenRepository plaidAccessTokenRepository) {
    this.plaidService = plaidService;
    this.plaidAccessTokenService = plaidAccessTokenService;
    this.plaidAccessTokenRepository = plaidAccessTokenRepository;
  }

  @PostMapping("/create-token")
  public Map<String, String> createLinkToken(@RequestBody Map<String, String> body,
      HttpServletRequest req) throws IOException {
    String userId = (String) req.getSession().getAttribute("userId");
    String mode = body.getOrDefault("mode", "create");
    String itemId = body.get("itemId");

    Response<LinkTokenCreateResponse> response = "update".equals(mode)
        ? plaidService.createUpdateLinkToken(userId, itemId)
        : plaidService.createLinkToken(userId);

    LinkTokenCreateResponse res = response.body();
    if (res == null) {
      throw new RuntimeException("Failed to create link token");
    }

    return Map.of("link_token", res.getLinkToken());
  }

  @PostMapping("/exchange-token")
  public ResponseEntity<?> exchangePublicToken(
      @RequestBody Map<String, String> body, HttpServletRequest request) throws IOException {
    String userId = (String) request.getSession().getAttribute("userId");
    String publicToken = body.get("public_token");
    Map<String, String> response = plaidService.exchangePublicTokenForAccessToken(publicToken);

    // Get institution name from Plaid API
    String institutionName = plaidService.getInstitutionName(response.get("accessToken"));

    plaidAccessTokenService.createPlaidItem(
        userId, response.get("accessToken"), response.get("itemId"), institutionName);

    return ResponseEntity.ok(Map.of(
        "itemId", response.get("itemId"),
        "institutionName", institutionName));
  }

  @GetMapping("/items")
  public ResponseEntity<List<Map<String, String>>> getItems(HttpServletRequest request) {
    String userId = (String) request.getSession().getAttribute("userId");
    List<Map<String, String>> items = plaidAccessTokenRepository.findByUserId(userId).stream()
        .map(token -> Map.of(
            "itemId", token.getItemId(),
            "institutionName", token.getInstitutionName()))
        .collect(Collectors.toList());
    return ResponseEntity.ok(items);
  }

}
