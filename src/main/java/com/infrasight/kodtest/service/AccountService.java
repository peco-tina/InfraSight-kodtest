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

public class AccountService {
    private OkHttpClient client;

    public AccountService(OkHttpClient client) {
        this.client = client;
    }

    public Account findAccountById(String employeeId, String accountId) throws InterruptedException {
        String url = buildUrl(employeeId, accountId);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + AuthenticationService.bearerToken)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                Gson gson = new Gson();
                Account[] accounts = gson.fromJson(responseBody, Account[].class);
                if (accounts.length > 0) {
                    Account account = accounts[0]; //due to unique search, we chose index 0
                    return account;
                }
            } else {
                System.out.println("Request failed with status code: " + response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
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
