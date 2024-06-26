package com.infrasight.kodtest.service;

import com.google.gson.Gson;
import com.infrasight.kodtest.model.Account;
import com.infrasight.kodtest.model.Relationship;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class RelationshipService {
    private OkHttpClient client;

    public RelationshipService(OkHttpClient okHttpClient) {
        this.client = okHttpClient;
    }

    public Relationship [] getAllRelationshipsByMemberId(String memberId){
        String url = "http://localhost:8080/api/relationships?filter=memberId%3D" + memberId;

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + AuthenticationService.bearerToken)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                Gson gson = new Gson();
                Relationship [] relationships = gson.fromJson(responseBody, Relationship[].class);
                return relationships;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
