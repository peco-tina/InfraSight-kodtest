package com.infrasight.kodtest.service;

import com.google.gson.Gson;
import com.infrasight.kodtest.model.Relationship;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class RelationshipService {
    private OkHttpClient client;
    public RelationshipService(OkHttpClient okHttpClient) {
        this.client = okHttpClient;
    }

    private static final int MAX_RETRIES = 5; // number of allowed attempts of calling the API. Can be increased
    private static final double BACKOFF_FACTOR = 0.5;
    public List<Relationship> getRelationships(String memberId, String groupId, String skip) throws InterruptedException {
        String url = buildUrl(memberId, groupId, skip);
        Request request = buildRequest(url);

        int retries = 0;
        while (retries < MAX_RETRIES) { // it is allowed to try max 5 times with the request
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    return parseJsonToList(response);
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

    public List<Relationship> getAllRelationshipsByMemberIds(List<Relationship> relationships, List<String> processedGroupIds) throws InterruptedException {
        List<Relationship> newRelationships = new ArrayList<>();
        for (Relationship relationship : relationships) {
            if(!processedGroupIds.contains(relationship.getGroupId())){ //if the groupId is not already used in the search "processed"
                List<Relationship> tempRelationships = getRelationships(relationship.getGroupId(), null, null);
                if(tempRelationships != null){
                    newRelationships.addAll(tempRelationships);
                    processedGroupIds.add(relationship.getGroupId());
                }
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

    public Collection<String> getAllMembersID(String groupId, List<String> ids, List<String> subGroupsIds) throws InterruptedException {
        List<Relationship> allRelationships = new ArrayList<>();
        List<String> subGroups = new ArrayList<>();
        List<String> memberIds = new ArrayList<>();

        if(ids != null){
            memberIds.addAll(ids);
        }
        if(subGroupsIds != null){
            subGroups.addAll(subGroupsIds);
        }

        boolean isListSizeChanged = true;
        int skipCounter = 0;
        int listSize = 0;
        int previousListSize = 0;

        do{
            String skip = new Integer(skipCounter).toString();
            List<Relationship> relationships = getRelationships(null, groupId, skip);
            if(relationships != null){
                allRelationships.addAll(relationships);
            }
            previousListSize = listSize;
            skipCounter += 50;
            listSize = allRelationships.size();

            if(previousListSize == listSize){
                isListSizeChanged = false;
            }

        } while (isListSizeChanged);

        for(Relationship relationship : allRelationships){
            if(!relationship.getMemberId().substring(0,3).equals("acc") && !relationship.getMemberId().equals("vera_scope")){
                subGroups.add(relationship.getMemberId());
            } else {
                memberIds.add(relationship.getMemberId());
            }
        }

        for (String subGroupId : subGroups) {
            List<String> temporarySubGroups = new ArrayList<>();
            temporarySubGroups.addAll(subGroups);
            temporarySubGroups.remove(0);
            memberIds.addAll(getAllMembersID(subGroupId, memberIds, temporarySubGroups));
        }

        memberIds = removeDuplicate(memberIds);

        return memberIds;
    }

    private List<String> removeDuplicate(List<String> memberIds) {
        Set<String> set = new HashSet<>(memberIds);
        return new ArrayList<>(set);
    }

    private List<Relationship> parseJsonToList(Response response) throws IOException{
        List<Relationship> allRelationships = new ArrayList<>();
        String responseBody = response.body().string();
        Gson gson = new Gson();
        Relationship[] relationshipsArray = gson.fromJson(responseBody, Relationship[].class); // parse all results to array from JSON
        allRelationships.addAll(Arrays.asList(relationshipsArray));
        return allRelationships;
    }

    private Request buildRequest(String url) {
        return new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + AuthenticationService.bearerToken)
                .build();
    }

    private String buildUrl(String memberId, String groupId, String skip) {
        String url = "http://localhost:8080/api/relationships?filter=";
        if(memberId!=null){
            url += "memberId%3D" + memberId;
        }

        if(groupId!=null){
            url += "groupId%3D" + groupId;
        }

        if(skip!=null){
            url += "&skip=" + skip;
        }
        return url;
    }
}