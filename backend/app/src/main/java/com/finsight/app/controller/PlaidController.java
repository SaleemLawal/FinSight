package com.finsight.app.controller;

import com.finsight.app.service.PlaidAccessTokenService;
import com.finsight.app.service.PlaidService;
import com.finsight.app.service.UserService;
import com.plaid.client.model.*;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import retrofit2.Response;

@RestController
@RequestMapping("/api/plaid")
public class PlaidController {
  private final PlaidService plaidService;
  private final PlaidAccessTokenService plaidAccessTokenService;
  private final UserService userService;

  @Autowired
  private PlaidController(
      PlaidService plaidService,
      PlaidAccessTokenService plaidAccessTokenService,
      UserService userService) {
    this.plaidService = plaidService;
    this.plaidAccessTokenService = plaidAccessTokenService;
    this.userService = userService;
  }

  @PostMapping("/create-token")
  public Map<String, String> createLinkToken(HttpServletRequest request) throws Exception {
    String userId = (String) request.getSession().getAttribute("userId");

    Response<LinkTokenCreateResponse> response = plaidService.createLinkToken(userId);

    assert response.body() != null;
    return Map.of("link_token", response.body().getLinkToken());
  }

  @PostMapping("/exchange-token")
  public ResponseEntity<?> exchangePublicToken(
      @RequestBody Map<String, String> body, HttpServletRequest request) throws IOException {
    String userId = (String) request.getSession().getAttribute("userId");
    String publicToken = body.get("public_token");
    Map<String, String> response = plaidService.exchangePublicTokenForAccessToken(publicToken);

    plaidAccessTokenService.createPlaidItem(
        userId, response.get("accessToken"), response.get("itemId"));

    return ResponseEntity.ok().build();
  }
}
