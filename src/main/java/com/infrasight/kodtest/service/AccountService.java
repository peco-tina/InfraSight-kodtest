package com.infrasight.kodtest.service;

import com.google.gson.Gson;
import com.infrasight.kodtest.model.Account;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class AccountService {
    private OkHttpClient client;

    public AccountService(OkHttpClient client) {
        this.client = client;
    }

    public Account findAccountById(String employeeId) {
        String url = "http://localhost:8080/api/accounts?filter=employeeId=" + employeeId;

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
}
