package com.example.peter.connectd.rest;

import org.json.JSONObject;

/**
 * Created by peter on 23/11/15.
 */
public interface ErrorCallBacks {
    void onError(JSONObject errors);
    void onError(String error);
}
