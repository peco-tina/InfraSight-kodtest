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

    public List<Relationship> getRelationships(String memberId, String groupId, String skip){
        String url = buildUrl(memberId, groupId, skip);
        Request request = buildRequest(url);

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return parseJsonToList(response);
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
                newRelationships.addAll(getRelationships(relationship.getGroupId(), null, null));
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

    private Request buildRequest(String url) {
        return new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + AuthenticationService.bearerToken)
                .build();
    }

    private List<Relationship> parseJsonToList(Response response) throws IOException{
        List<Relationship> allRelationships = new ArrayList<>();
        String responseBody = response.body().string();
        Gson gson = new Gson();
        Relationship[] relationshipsArray = gson.fromJson(responseBody, Relationship[].class); // parse all results to array from JSON
        allRelationships.addAll(Arrays.asList(relationshipsArray));
        return allRelationships;
    }

    public Collection<String> getAllMembersID(String groupId, List<String> ids, List<String> subGroupsIds) {
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
            allRelationships.addAll(getRelationships(null, groupId, skip));
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
}