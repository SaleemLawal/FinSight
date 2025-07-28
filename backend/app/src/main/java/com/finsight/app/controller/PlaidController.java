package com.finsight.app.controller;

import com.finsight.app.service.PlaidService;
import com.plaid.client.model.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import retrofit2.Response;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/plaid")
public class PlaidController {
    private final PlaidService plaidService;

    @Autowired
    private PlaidController(PlaidService plaidService) {
        this.plaidService = plaidService;
    }

    @PostMapping("/create_link_token")
    public Map<String, String> createLinkToken(HttpServletRequest request) throws Exception {
        String userId = (String) request.getSession().getAttribute("userId");

        Response<LinkTokenCreateResponse>  response = plaidService.createLinkToken(userId);

        assert response.body() != null;
        return Map.of("link_token", response.body().getLinkToken());
    }

    @GetMapping("/extract-public-token/{linkToken}")
    public Map<String, String> extractPublicTokenOnly(@PathVariable String linkToken) throws IOException {
        String publicToken = plaidService.extractPublicToken(linkToken);
        return Map.of("public_token", publicToken);
    }

    @GetMapping("/extract-access-token/{publicToken}")
    public void extractAccessToken(@PathVariable String publicToken) throws IOException{
        plaidService.getAccessToken(publicToken);
    }

}
