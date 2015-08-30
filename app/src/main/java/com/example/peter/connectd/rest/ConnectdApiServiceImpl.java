package com.example.peter.connectd.rest;

import android.content.Context;
import android.content.Intent;

import com.example.peter.connectd.models.User;
import com.example.peter.connectd.ui.AuthenticatedHomeActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
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

        final RequestParams params = new RequestParams();
        params.put("search_query", login);
        mAsyncHttpClient.addHeader("Accept", "application/json");
        mAsyncHttpClient.addHeader("Content-Type", "application/x-www-form-urlencoded");
        mAsyncHttpClient.get(String.format("%s/%s", ConnectdApiClient.CONNECTD_ENDPOINT,
                        ConnectdApiClient.RELATIVE_SEARCH_ENDPOINT),
                params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        if (statusCode == 200) {
                            try {
                                User.Builder userBuilder = new User.Builder(response.getInt(User.ID_KEY))
                                        .setUsername(response.getString(User.USERNAME_KEY))
                                        .setEmail(response.getString(User.EMAIL_KEY))
                                        .setFirstName(response.getString(User.FIRST_NAME_KEY))
                                        .setLastName(response.getString(User.LAST_NAME_KEY));
                                listener.onUserLoaded(userBuilder.build());
                            } catch (JSONException e) {
                                // NOP
                            }
                        }
                    }
                });
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
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        super.onSuccess(statusCode, headers, response);
                        if (statusCode == 200) {
                            try {
                                List<User> userList = new ArrayList<User>();
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject userObject = response.getJSONObject(i);
                                    User.Builder userBuilder = new User.Builder(userObject.getInt(User.ID_KEY))
                                            .setEmail(userObject.getString(User.EMAIL_KEY))
                                            .setFirstName(userObject.getString(User.FIRST_NAME_KEY))
                                            .setLastName(userObject.getString(User.LAST_NAME_KEY));
                                    if (userObject.getString(User.USERNAME_KEY) != null) {
                                        userBuilder.setUsername(userObject.getString(User.USERNAME_KEY));
                                    }
                                    userList.add(userBuilder.build());
                                }
                                listener.onUsersLoaded(userList);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    @Override
    public void signIn(final Context context, final String login, String password,
                       final OnAuthenticateListener authenticateListener) {
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
                if ((statusCode == 302) || (responseString.contains("Signed in successfully"))) {
                    // redirect in rails app logic -> authenticated
                    authenticateListener.onAuthenticate(login);
                } else  {
                    authenticateListener.onAuthenticateFailed();
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
