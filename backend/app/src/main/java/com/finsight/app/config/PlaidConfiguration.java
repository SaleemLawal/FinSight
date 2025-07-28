package com.finsight.app.config;

import com.plaid.client.request.PlaidApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.plaid.client.ApiClient;

import java.util.HashMap;

@Configuration
public class PlaidConfiguration {

    @Value("${plaid.client-id}")
    private String clientId;

    @Value("${plaid.secret}")
    private String secret;

    @Value("${plaid.env}")
    private String plaidEnv;

    @Bean
    public PlaidApi plaidApi() {
        HashMap<String, String> apiKeys = new HashMap<>();
        apiKeys.put("clientId", clientId);
        apiKeys.put("secret", secret);

        ApiClient apiClient = new ApiClient(apiKeys);

        // Set the environment based on configuration
        switch (plaidEnv.toLowerCase()) {
            case "sandbox":
                apiClient.setPlaidAdapter(ApiClient.Sandbox);
                break;
            case "production":
                apiClient.setPlaidAdapter(ApiClient.Production);
                break;
            default:
                 throw new IllegalArgumentException("Invalid Plaid environment: " + plaidEnv);
        }

        return apiClient.createService(PlaidApi.class);
    }
}
