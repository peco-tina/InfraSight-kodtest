package com.infrasight.kodtest.service;

import com.google.gson.Gson;
import com.infrasight.kodtest.model.Account;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AccountService {
    private OkHttpClient client;

    public AccountService(OkHttpClient client) {
        this.client = client;
    }

    private static final int MAX_RETRIES = 5;
    private static final double BACKOFF_FACTOR = 0.5;
    public Account findAccountById(String employeeId, String accountId) throws InterruptedException {
        String url = buildUrl(employeeId, accountId);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + AuthenticationService.bearerToken)
                .build();

        int retries = 0;
        while (retries < MAX_RETRIES) {
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Gson gson = new Gson();
                    Account[] accounts = gson.fromJson(responseBody, Account[].class);
                    if (accounts.length > 0) {
                        Account account = accounts[0]; // due to unique search, we choose index 0
                        return account;
                    }
                } else if (response.code() == 429) {
                    retries++;
                    double sleepTime = BACKOFF_FACTOR * Math.pow(2, retries);
                    System.out.println("Rate limit exceeded. Retrying in " + sleepTime + " seconds...");
                    TimeUnit.SECONDS.sleep((long) sleepTime);
                } else {
                    System.out.println("Request failed with status code: " + response.code());
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
        return null;
    }

    public Collection<? extends Account> getAllAccountsById(List<String> accountsId) throws InterruptedException {
        List<Account> accounts = new ArrayList<>();
        for(String accountId : accountsId){
            accounts.add(findAccountById(null,accountId));
        }
        return accounts;
    }

    private String buildUrl(String employeeId, String accountId) {
        String url = "http://localhost:8080/api/accounts?filter=";

        if(employeeId != null){
            url += "employeeId=" + employeeId;
        }

        if(accountId != null){
            url += "id=" + accountId;
        }
        return url;
    }
}
