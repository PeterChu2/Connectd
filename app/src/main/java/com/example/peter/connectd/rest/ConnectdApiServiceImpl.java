package com.example.peter.connectd.rest;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.peter.connectd.models.User;
import com.example.peter.connectd.ui.AuthenticatedHomeActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Created by peter on 27/08/15.
 */
public class ConnectdApiServiceImpl implements ConnectdApiService {
    AsyncHttpClient mAsyncHttpClient;

    protected ConnectdApiServiceImpl() {
        mAsyncHttpClient = new AsyncHttpClient();
    }

    @Override
    public User findUserByUsername(String username) {
        return null;
    }

    @Override
    public User findUserByEmail(String email) {
        return null;
    }

    @Override
    public List<User> findUser(Map<String, String> searchParams) {
        return null;
    }

    @Override
    public void signIn(final Context context, String login, String password) {
        RequestParams params = new RequestParams();
        RequestParams userParams = new RequestParams();
        userParams.put(User.LOGIN_KEY, login);
        userParams.put(User.PASSWORD_KEY, password);
        userParams.put(User.REMEMBER_ME_KEY, 0);
        params.put(User.USER_KEY, userParams);

        JsonHttpResponseHandler jsonHttpResponseHandler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (statusCode == 200 || statusCode == 302) {
                    context.startActivity(new Intent(context, AuthenticatedHomeActivity.class));
                } else if (statusCode == 401) {
                    Toast.makeText(context, "Invalid username or password. Check your credentials.",
                            Toast.LENGTH_SHORT).show();
                }
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                if (statusCode == 200 || statusCode == 302) {
                    context.startActivity(new Intent(context, AuthenticatedHomeActivity.class));
                } else if (statusCode == 401) {
                    Toast.makeText(context, "Invalid username or password. Check your credentials.",
                            Toast.LENGTH_SHORT).show();
                }
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                if (statusCode == 200 || statusCode == 302) {
                    context.startActivity(new Intent(context, AuthenticatedHomeActivity.class));
                } else if (statusCode == 401) {
                    Toast.makeText(context, "Invalid username or password. Check your credentials.",
                            Toast.LENGTH_SHORT).show();
                }
                super.onSuccess(statusCode, headers, responseString);
            }
        };
        mAsyncHttpClient = new AsyncHttpClient();
        Log.d("PETER", params.toString());
        mAsyncHttpClient.addHeader("Accept", "application/json");
        mAsyncHttpClient.addHeader("Content-Type", "application/json");
        mAsyncHttpClient.post(ConnectdApiClient.CONNECTD_ENDPOINT + "/users/sign_in",
                params, jsonHttpResponseHandler);
    }

    @Override
    public void signUp(final Context context, String username, String email, String firstName,
                       String lastName, String password, String passwordConfirmation) {
        RequestParams params = new RequestParams();
        RequestParams userParams = new RequestParams();
        userParams.put(User.USERNAME_KEY, username);
        userParams.put(User.EMAIL_KEY, email);
        userParams.put(User.FIRST_NAME_KEY, firstName);
        userParams.put(User.LAST_NAME_KEY, lastName);
        userParams.put(User.PASSWORD_KEY, password);
        userParams.put(User.PASSWORD_CONFIRMATION_KEY, passwordConfirmation);
        params.put(User.USER_KEY, userParams);

        JsonHttpResponseHandler jsonHttpResponseHandler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                if (statusCode == 200 || statusCode == 302) {
                    context.startActivity(new Intent(context, AuthenticatedHomeActivity.class));
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                if (statusCode == 200 || statusCode == 302) {
                    context.startActivity(new Intent(context, AuthenticatedHomeActivity.class));
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
                if (statusCode == 200 || statusCode == 302) {
                    context.startActivity(new Intent(context, AuthenticatedHomeActivity.class));
                }
            }

        };

        mAsyncHttpClient.addHeader("Accept", "application/json");
        mAsyncHttpClient.addHeader("Content-Type", "application/json");
        mAsyncHttpClient.post(ConnectdApiClient.CONNECTD_ENDPOINT + "/users",
                userParams, jsonHttpResponseHandler);
    }
}
