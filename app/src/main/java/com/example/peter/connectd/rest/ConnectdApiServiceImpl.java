package com.example.peter.connectd.rest;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.peter.connectd.models.User;
import com.example.peter.connectd.ui.AuthenticatedHomeActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by peter on 27/08/15.
 */
public class ConnectdApiServiceImpl implements ConnectdApiService {
    AsyncHttpClient mAsyncHttpClient;

    protected ConnectdApiServiceImpl() {
        mAsyncHttpClient = new AsyncHttpClient();
        mAsyncHttpClient.addHeader("Content-Type", "application/json");
    }

    @Override
    public void findUserByLogin(final Context context, String login,
                                final OnAsyncHttpRequestCompleteListener listener) {
        JSONObject params = new JSONObject();
        try {
            params.put("search_query", login);
        } catch (JSONException e) {
            // NOP
        }

        try {
            StringEntity entity = new StringEntity(params.toString());
            mAsyncHttpClient.addHeader("Accept", "application/json");
            mAsyncHttpClient.get(context, ConnectdApiClient.CONNECTD_ENDPOINT + "/" + ConnectdApiClient.RELATIVE_SEARCH_ENDPOINT,
                    entity, "application/x-www-form-urlencoded", new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);
                            if (statusCode == 200) {
                                try {
                                    User.Builder userBuilder = new User.Builder(response.getInt(User.ID_KEY))
                                            .setEmail(response.getString(User.EMAIL_KEY))
                                            .setFirstName(response.getString(User.FIRST_NAME_KEY))
                                            .setLastName(response.getString(User.LAST_NAME_KEY));
                                    if (response.getString(User.USERNAME_KEY) != null) {
                                        userBuilder.setUsername(response.getString(User.USERNAME_KEY));
                                    }
                                    listener.onUserLoaded(userBuilder.build());
                                } catch (JSONException e) {
                                    // NOP
                                }
                            }
                        }
                    });
        } catch (UnsupportedEncodingException e) {
            // NOP
        }
    }

    @Override
    public void findUser(final Context context, Map<String, String> searchParams,
                         final OnAsyncHttpRequestCompleteListener listener) {
        StringEntity entity = null;
        try {
            entity = new StringEntity(new JSONObject(searchParams).toString());
        } catch (UnsupportedEncodingException e) {
            // NOP
        }
        mAsyncHttpClient.addHeader("Accept", "application/json");
        mAsyncHttpClient.get(context, ConnectdApiClient.CONNECTD_ENDPOINT + "/" + ConnectdApiClient.RELATIVE_SEARCH_ENDPOINT,
                entity, "application/x-www-form-urlencoded", new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        if (statusCode == 200) {
                            try {
                                User.Builder userBuilder = new User.Builder(response.getInt(User.ID_KEY))
                                        .setEmail(response.getString(User.EMAIL_KEY))
                                        .setFirstName(response.getString(User.FIRST_NAME_KEY))
                                        .setLastName(response.getString(User.LAST_NAME_KEY));
                                if (response.getString(User.USERNAME_KEY) != null) {
                                    userBuilder.setUsername(response.getString(User.USERNAME_KEY));
                                }
                                listener.onUserLoaded(userBuilder.build());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    @Override
    public void signIn(final Context context, String login, String password) {
        JSONObject params = new JSONObject();
        JSONObject userParams = new JSONObject();
        try {
            userParams.put(User.LOGIN_KEY, login);
            userParams.put(User.PASSWORD_KEY, password);
            userParams.put(User.REMEMBER_ME_KEY, 0);
            params.put(User.USER_KEY, userParams);
        } catch (JSONException e) {
            // NOP
        }
        TextHttpResponseHandler httpResponseHandler = new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                if (statusCode == 200 || statusCode == 302) {
                    context.startActivity(new Intent(context, AuthenticatedHomeActivity.class));
                } else if (statusCode == 401) {
                    Toast.makeText(context, "Invalid username or password. Check your credentials.",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                // NOP
            }
        };

        try {
            StringEntity entity = new StringEntity(params.toString());
            mAsyncHttpClient.addHeader("Accept", "text/html");
            mAsyncHttpClient.post(context, ConnectdApiClient.CONNECTD_ENDPOINT + "/users/sign_in",
                    entity, "application/json", httpResponseHandler);
        } catch (UnsupportedEncodingException e) {
            // NOP
        }
    }

    @Override
    public void signUp(final Context context, String username, String email,
                       String firstName, String lastName, String password,
                       String passwordConfirmation) {
        JSONObject params = new JSONObject();
        JSONObject userParams = new JSONObject();

        try {
            userParams.put(User.USERNAME_KEY, username);
            userParams.put(User.EMAIL_KEY, email);
            userParams.put(User.FIRST_NAME_KEY, firstName);
            userParams.put(User.LAST_NAME_KEY, lastName);
            userParams.put(User.PASSWORD_KEY, password);
            userParams.put(User.PASSWORD_CONFIRMATION_KEY, passwordConfirmation);
            params.put(User.USER_KEY, userParams);
        } catch (JSONException e) {
            // NOP
        }

        TextHttpResponseHandler httpResponseHandler = new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                if (statusCode == 200 || statusCode == 302) {
                    context.startActivity(new Intent(context, AuthenticatedHomeActivity.class));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                // NOP
            }
        };

        try {
            mAsyncHttpClient.addHeader("Accept", "text/html");
            StringEntity entity = new StringEntity(params.toString());
            mAsyncHttpClient.post(context, ConnectdApiClient.CONNECTD_ENDPOINT + "/users",
                    entity, "application/json", httpResponseHandler);
        } catch (UnsupportedEncodingException e) {
            // NOP
        }
    }
}
