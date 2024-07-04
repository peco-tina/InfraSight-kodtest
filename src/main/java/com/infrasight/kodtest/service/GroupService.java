package com.infrasight.kodtest.service;

import com.google.gson.Gson;
import com.infrasight.kodtest.model.Account;
import com.infrasight.kodtest.model.Group;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GroupService {
    private OkHttpClient client;
    private static final int MAX_RETRIES = 5; // number of allowed attempts of calling the API. Can be increased
    private static final double BACKOFF_FACTOR = 0.5;

    public GroupService(OkHttpClient okHttpClient) {
        this.client = okHttpClient;
    }

    public Group getGroupById(String groupId) throws InterruptedException {
        String url = buildUrl(groupId);

        Request request = buildRequest(url);

        int retries = 0;
        while (retries < MAX_RETRIES) { // it is allowed to try max 5 times with the request
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Gson gson = new Gson();
                    Group[] groups = gson.fromJson(responseBody, Group[].class);
                    if (groups.length > 0) {
                        Group group = groups[0]; // due to unique search, we choose index 0
                        return group;
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

    public List<Group> getAllGroupsByIds(List<String> groupIds) throws InterruptedException{
        List<Group> groups = new ArrayList<>();

        for (String groupId : groupIds) {
            groups.add(getGroupById(groupId));
        }

        return groups;
    }

    private String buildUrl(String groupId) {
        String url = "http://localhost:8080/api/groups?filter=";

        if (groupId != null) {
            url += "id%3D" + groupId;
        }
        return url;
    }

    private Request buildRequest(String url) {
        return new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + AuthenticationService.bearerToken)
                .build();
    }
}
