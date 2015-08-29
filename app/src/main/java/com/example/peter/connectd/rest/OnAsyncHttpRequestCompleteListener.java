package com.example.peter.connectd.rest;

import com.example.peter.connectd.models.User;

import java.util.List;

/**
 * Class that notifies the listener of the completion of an {@link com.loopj.android.http.AsyncHttpRequest}.
 */
public interface OnAsyncHttpRequestCompleteListener {
    /**
     * Invoked when a {@link User} has been fetched from the database.
     * @param user a {@link User} fetched from the database.
     */
    public void onUserLoaded(User user);

    /**
     * Invoked when a list of {@link User}s has been fetched from the database.
     * @param users a {@link java.util.List<User>} fetched from the database.
     */
    public void onUsersLoaded(List<User> users);
}
