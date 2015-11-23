package com.example.peter.connectd.rest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.peter.connectd.models.SearchResult;
import com.example.peter.connectd.models.User;
import com.example.peter.connectd.ui.activity.AuthenticatedHomeActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

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
                                listener.onUserLoaded(new User.Builder(response).build());
                            } catch (JSONException e) {
                                // NOP
                            }
                        }
                    }
                });
    }

    @Override
    public void findUsers(final Context context, Map<String, String> searchParams,
                          final OnAsyncHttpRequestCompleteListener listener) {
        // TODO - make endpoint that allows for searching with all params and implement this function
    }

    @Override
    public void findUsers(Context context, String query, final OnAsyncHttpRequestCompleteListener listener) {
        StringEntity entity = null;
        JSONObject searchParams = new JSONObject();
        JSONObject queryParams = new JSONObject();
        try {
            queryParams.put(User.QUERY_KEY, query);
            searchParams.put(User.SEARCH_KEY, queryParams);
        } catch (JSONException e) {
            // NOP
        }
        try {
            entity = new StringEntity(searchParams.toString());
        } catch (UnsupportedEncodingException e) {
            // NOP
        }
        mAsyncHttpClient.addHeader("Content-Type", "application/json");
        mAsyncHttpClient.addHeader("Accept", "application/json");
        mAsyncHttpClient.get(context, String.format("%s/%s", ConnectdApiClient.CONNECTD_ENDPOINT,
                ConnectdApiClient.RELATIVE_AUTOCOMPLETE_ENDPOINT),
                entity, "application/json", new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        if (statusCode == 200) {
                            try {
                                List<SearchResult> searchResultsList = new ArrayList<SearchResult>();
                                JSONArray userResults = response.getJSONArray(User.USERS_RESULTS_KEY);
                                for (int i = 0; i < userResults.length(); i++) {
                                    JSONObject userObject = userResults.getJSONObject(i);
                                    searchResultsList.add(new SearchResult(userObject));
                                }
                                listener.onResultsLoaded(searchResultsList);
                            } catch (JSONException e) {
                                // NOP
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
        JsonHttpResponseHandler httpResponseHandler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                if ((statusCode == 302) || (responseString.contains("Signed in successfully"))) {
                    // redirect in rails app logic -> authenticated
                    String authToken = "";//TODO
                    authenticateListener.onAuthenticate(login, authToken);
                } else  {
                    authenticateListener.onAuthenticateFailed();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                // NOP
                Log.d("PETER", "TeS");
            }
        };

        try {
            StringEntity entity = new StringEntity(params.toString());
            mAsyncHttpClient.addHeader("Accept", "application/json");
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

        JsonHttpResponseHandler httpResponseHandler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                if (statusCode == 200 || statusCode == 302) {
                    String login = "";//get login here;
                    String auth = ""; //get auth here

                    SharedPreferences.Editor sharedPreferencesEditor = PreferenceManager
                            .getDefaultSharedPreferences(context).edit();

                    sharedPreferencesEditor.putString(ConnectdApiClient.
                            AUTH_KEY, auth).apply();
                    sharedPreferencesEditor.putString(ConnectdApiClient.
                            SHAREDPREF_LOGIN_KEY, login.toLowerCase()).apply();
                    sharedPreferencesEditor.putString(ConnectdApiClient.
                            SHAREDPREF_CURRENT_USER_KEY, login.toLowerCase()).apply();


                    context.startActivity(new Intent(context, AuthenticatedHomeActivity.class));
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
            }
        };

        try {
            mAsyncHttpClient.addHeader("Accept", "application/json");
            StringEntity entity = new StringEntity(params.toString());
            mAsyncHttpClient.post(context, ConnectdApiClient.CONNECTD_ENDPOINT + "/users",
                    entity, "application/json", httpResponseHandler);
        } catch (UnsupportedEncodingException e) {
            // NOP
        }
    }

    @Override
    public void updateUser(final Context context, int id, JSONObject updateParams,
                       final OnAsyncHttpRequestCompleteListener listener) {
        mAsyncHttpClient.addHeader("Accept", "application/json");
        mAsyncHttpClient.addHeader("Content-Type", "application/json");
        StringEntity entity = null;
        try {
            entity = new StringEntity(updateParams.toString());
        } catch (UnsupportedEncodingException e) {
            // NOP
        }
        mAsyncHttpClient.post(context, String.format("%s/%s/%d", ConnectdApiClient.CONNECTD_ENDPOINT,
                        ConnectdApiClient.RELATIVE_USERS_ENDPOINT, id), entity, "application/json",
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        if (statusCode == 200) {
                            try {
                                listener.onUserLoaded(new User.Builder(response).build());
                            } catch (JSONException e) {
                                // NOP
                            }
                        } else if (statusCode == 500) {
                            listener.onUserLoadFailed("User was not successfully saved. Your edited email or username is already registered.");
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                    }
                });
    }
}
