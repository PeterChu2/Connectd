package com.example.peter.connectd.rest;

/**
 * Created by peter on 28/08/15.
 */
public interface OnAuthenticateListener {
    /**
     * Invoked when the user successfully signs in.
     */
    void onAuthenticate(String login);

    /**
     * Invoked when the user has unsuccessfully attempted to sign in.
     */
    void onAuthenticateFailed();
}
