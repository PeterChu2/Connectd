package com.example.peter.connectd.rest;

import com.example.peter.connectd.models.SearchResult;
import com.example.peter.connectd.models.User;

import java.util.List;

/**
 * Class that notifies the listener of the completion of an {@link com.loopj.android.http.AsyncHttpRequest}.
 */
public interface OnAsyncHttpRequestCompleteListener extends ErrorCallBacks {
    /**
     * Invoked when a {@link User} has been fetched from the database.
     * @param user a {@link User} fetched from the database.
     */
    void onUserLoaded(User user);

    /**
     * Invoked when a list of {@link User}s has been fetched from the database.
     * @param results a list of {@link java.util.List<SearchResult>}s fetched from the database.
     */
    void onResultsLoaded(List<SearchResult> results);

    /**
     * Invoked when the user failed to load due to some error
     */
    void onUserLoadFailed(String error);
}
