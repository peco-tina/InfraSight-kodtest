package com.infrasight.kodtest.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;

import java.io.IOException;

public class AuthenticationService {
    public static String bearerToken;
    private static final String AUTH_URL = "http://localhost:8080/api/auth";

    public void getBearerToken(OkHttpClient client) {
        JsonObject json = new JsonObject();
        json.addProperty("user", "apiUser");
        json.addProperty("password", "apiPassword!");

        String jsonString = json.toString();
        System.out.println(jsonString);

        RequestBody requestBody = RequestBody.create(jsonString, MediaType.get("application/json"));

        Request request = new Request.Builder()
                .url(AUTH_URL)
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                JsonObject responseJson = JsonParser.parseString(responseBody).getAsJsonObject();
                bearerToken = responseJson.get("token").getAsString();
            } else {
                System.out.println("Authentication failed with status code: " + response.code());
            }
        } catch (IOException e) {
            System.out.println("Failed to authenticate!");
            e.printStackTrace();
        }
    }
}
