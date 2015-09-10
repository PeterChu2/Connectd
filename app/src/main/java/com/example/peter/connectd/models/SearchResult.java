package com.example.peter.connectd.models;

import org.json.JSONException;
import org.json.JSONObject;

public class SearchResult {
    private String mFullName;
    private String mUsername;
    private String mPictureUrl;

    public SearchResult(String username, String firstName, String lastName) {
        mFullName = String.format("%s %s", firstName, lastName);
        mUsername = username;
    }

    public SearchResult(JSONObject searchResult) {
        String firstName = "";
        String lastName = "";
        try {
            if (searchResult.has(User.FIRST_NAME_KEY))
                firstName = searchResult.getString(User.FIRST_NAME_KEY);
            if (searchResult.has(User.LAST_NAME_KEY))
                lastName = searchResult.getString(User.LAST_NAME_KEY);
            if (searchResult.has(User.USERNAME_KEY))
                mUsername = searchResult.getString(User.USERNAME_KEY);

            // TODO - Parse the JSON to find picture URL
        } catch (JSONException e) {
            // NOP
        }
        mFullName = String.format("%s %s", firstName, lastName);
    }

    @Override
    public String toString() {
        return mFullName;
    }

    public String getDisplayName() {
        return mFullName;
    }

    public String getUsername() {
        return mUsername;
    }

    public String getPictureUrl() {
        return mPictureUrl;
    }
}
