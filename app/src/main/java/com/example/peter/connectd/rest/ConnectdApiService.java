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
     * Asynchronously finds the unique user from a username or email address.
     * The {@link User} is returned via. {@link OnAsyncHttpRequestCompleteListener#onUsersLoaded(User)}
     *
     * @param context  The application context.
     * @param login    The unique username of the user to return.
     * @param listener An {@link OnAsyncHttpRequestCompleteListener} that is notified when an async http request is completed.
     * @return A {@link User} with information fetched from the backend server.
     */
    void findUserByLogin(final Context context, String login,
                         final OnAsyncHttpRequestCompleteListener listener);

    /**
     * Asynchronously queries the database for all {@link User}s matching the provided criteria.
     * The {@link List<User>} os returned via {@link OnAsyncHttpRequestCompleteListener#onUsersLoaded(List<User>)};
     *
     * @param searchParams A map of search parameters.
     * @return A {@link List} of {@link User}s that match the query.
     */
    void findUser(final Context context, Map<String, String> searchParams,
                  final OnAsyncHttpRequestCompleteListener listener);

    /**
     * Authenticates within the app.
     *
     * @param context  The {@link android.app.Activity} context.
     * @param login    The username or the email of the account.
     * @param password The password of the account.
     * @param onAuthenticateListener A listener to be notified when the user has successfully logged in.
     */
    void signIn(final Context context, String login, String password,
                final OnAuthenticateListener onAuthenticateListener);

    /**
     * Signs up for a new account within the app.
     *
     * @param context              The {@link android.app.Activity} context.
     * @param username             The unique username to sign in with.
     * @param email                A valid email address.
     * @param firstName            First name.
     * @param lastName             Last name.
     * @param password             Password.
     * @param passwordConfirmation Password confirmation.
     */
    void signUp(final Context context, String username, String email, String firstName,
                String lastName, String password, String passwordConfirmation);
}
