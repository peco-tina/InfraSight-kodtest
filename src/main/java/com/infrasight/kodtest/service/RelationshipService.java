package com.infrasight.kodtest.service;

import com.google.gson.Gson;
import com.infrasight.kodtest.model.Relationship;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.*;

public class RelationshipService {
    private OkHttpClient client;
    public RelationshipService(OkHttpClient okHttpClient) {
        this.client = okHttpClient;
    }

    public List<Relationship> getAllRelationshipsByMemberId(String memberId){
        String url = "http://localhost:8080/api/relationships?filter=memberId%3D" + memberId;
        List<Relationship> allRelationships = new ArrayList<>();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + AuthenticationService.bearerToken)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                Gson gson = new Gson();
                Relationship[] relationshipsArray = gson.fromJson(responseBody, Relationship[].class); // parse all members in array from JSON
                allRelationships.addAll(Arrays.asList(relationshipsArray));
                return allRelationships;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Relationship> getAllRelationshipsByMemberIds(List<Relationship> relationships, List<String> processedGroupIds) {
        List<Relationship> newRelationships = new ArrayList<>();
        for (Relationship relationship : relationships) {
            if(!processedGroupIds.contains(relationship.getGroupId())){ //if the groupId is not already used in the search "processed"
                newRelationships.addAll(getAllRelationshipsByMemberId(relationship.getGroupId()));
                processedGroupIds.add(relationship.getGroupId());
            }
        }

        Iterator<Relationship> iterator = newRelationships.iterator();
        while (iterator.hasNext()) {
            Relationship newRelationship = iterator.next();
            boolean exists = false; // to avoid adding existing group in case: GROUP1 -> GROUP2 -> GROUP3 -> GROUP1
            for (Relationship existingRelationship : relationships) {
                if (existingRelationship.getGroupId().equals(newRelationship.getGroupId())) {
                    exists = true;
                    break;
                }
            }
            if (exists) {
                iterator.remove();
            }
        }

        relationships.addAll(newRelationships);

        if(processedGroupIds.size()==relationships.size()){ //if the sizes are equal then we found all
            return relationships;
        } else {
            getAllRelationshipsByMemberIds(relationships, processedGroupIds); // we call this method until all groupIds are processed, until all group that are member in other groups are found
        }
        return relationships;
    }
}