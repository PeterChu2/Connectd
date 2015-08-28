package com.example.peter.connectd.rest;

import android.content.Context;

import com.example.peter.connectd.models.User;

import java.util.List;
import java.util.Map;

/**
 * Created by peter on 27/08/15.
 */
public interface ConnectdApiService {
    /**
     * Finds the unique user from a username.
     * @param username The unique username of the user to return.
     * @return A {@link User} with information fetched from the backend server.
     */
    public User findUserByUsername(String username);

    /**
     * Finds the unique user from an email.
     * @param email The email of the user to return.
     * @return A {@link User} with information fetched from the backend server.
     */
    public User findUserByEmail(String email);

    /**
     * Queries the database for all {@link User}s matching the provided criteria.
     * @param searchParams A map of search parameters.
     * @return A {@link List} of {@link User}s that match the query.
     */
    public List<User> findUser(Map<String, String> searchParams);

    /**
     * Authenticates within the app.
     * @param context The {@link android.app.Activity} context.
     * @param login The username or the email of the account.
     * @param password The password of the account.
     */
    public void signIn(final Context context, String login, String password);

    /**
     * Signs up for a new account within the app.
     * @param context The {@link android.app.Activity} context.
     * @param username The unique username to sign in with.
     * @param email A valid email address.
     * @param firstName First name.
     * @param lastName Last name.
     * @param password Password.
     * @param passwordConfirmation Password confirmation.
     */
    public void signUp(final Context context, String username, String email, String firstName,
                       String lastName, String password, String passwordConfirmation);
}
