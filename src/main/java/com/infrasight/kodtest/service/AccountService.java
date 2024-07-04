package com.infrasight.kodtest.service;

import com.google.gson.Gson;
import com.infrasight.kodtest.model.Account;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class AccountService {
    private OkHttpClient client;

    public AccountService(OkHttpClient client) {
        this.client = client;
    }

    private static final int MAX_RETRIES = 5; // number of allowed attempts of calling the API. Can be increased
    private static final double BACKOFF_FACTOR = 0.5;
    public Account getAccountById(String employeeId, String accountId) throws InterruptedException {
        String url = buildUrl(employeeId, accountId);

        Request request = buildRequest(url);

        int retries = 0;
        while (retries < MAX_RETRIES) { // it is allowed to try max 5 times with the request
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
                    double sleepTime = BACKOFF_FACTOR * Math.pow(2, retries); // will wait some time and then try with call again
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

    public Collection<? extends Account> getAllAccountsByIds(List<String> accountsId) throws InterruptedException {
        List<Account> accounts = new ArrayList<>();
        for(String accountId : accountsId){
            accounts.add(getAccountById(null,accountId));
        }
        return accounts;
    }

    public Map<String, Long> calculateSalaries(List<Account> accounts){
        Map<String, Long> salaries = new HashMap<>();
        salaries.put("SEK", 0L);
        salaries.put("DKK", 0L);
        salaries.put("EUR", 0L);

        for(Account account : accounts) {
            if (account.isActive()) { // we do not calculate salaries for inactive accounts
                if (account.getSalaryCurrency().equals("SEK")) {
                    Long value = salaries.get("SEK");
                    value += account.getSalary();
                    salaries.put("SEK", value);
                } else if (account.getSalaryCurrency().equals("DKK")) {
                    Long value = salaries.get("DKK");
                    value += account.getSalary();
                    salaries.put("DKK", value);
                } else {
                    Long value = salaries.get("EUR");
                    value += account.getSalary();
                    salaries.put("EUR", value);
                }
            }
        }
        return salaries;
    }

    public Collection<?> filterAccountsByEmploymentStartDate(List<Account> saljareAccounts, int firstStartDate, int lastStardDate) {
        List<Account> tempList = new ArrayList<>();
        for(Account account : saljareAccounts){
            if(account.getEmployedSince() >= firstStartDate && account.getEmployedSince() <= lastStardDate){
                tempList.add(account);
            }
        }
        return tempList;
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

    private Request buildRequest(String url) {
        return  new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + AuthenticationService.bearerToken)
                .build();
    }
}
