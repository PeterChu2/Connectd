package com.example.peter.connectd;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

public class SignInActivity extends Activity {

    private EditText mEtEmail;
    private EditText mEtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_screen);
        Button signInButton = (Button) findViewById(R.id.btnSignIn);
        mEtEmail = (EditText) findViewById(R.id.sign_in_et_email);
        mEtPassword = (EditText) findViewById(R.id.etPass);
        final Context context = this;
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(context);
            }
        });
    }

    private void signIn(final Context context) {
        String email = mEtEmail.getText().toString();
        String password = mEtPassword.getText().toString();
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

        RequestParams params = new RequestParams();
        RequestParams userParams = new RequestParams();
        params.put("user", userParams);
        userParams.put("login", email);
        userParams.put("password", password);
        userParams.put("remember_me", 0);
        JsonHttpResponseHandler jsonHttpResponseHandler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if(statusCode == 200) {
                    startActivity(new Intent(context, AuthenticatedHomeActivity.class));
                }
                else {
                    Toast.makeText(context, "Invalid username or password. Check your credentials.", Toast.LENGTH_SHORT).show();
                }
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                if(statusCode == 200) {
                    startActivity(new Intent(context, AuthenticatedHomeActivity.class));
                }
                else {
                    Toast.makeText(context, "Invalid username or password. Check your credentials.", Toast.LENGTH_SHORT).show();
                }
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(context, "wwww" + statusCode, Toast.LENGTH_SHORT).show();
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Toast.makeText(context, "223f" + statusCode, Toast.LENGTH_SHORT).show();
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(context, "11ll" + statusCode, Toast.LENGTH_SHORT).show();
                Log.d("PETER", responseString);
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Toast.makeText(context, "oss" + statusCode, Toast.LENGTH_SHORT).show();
                super.onSuccess(statusCode, headers, responseString);
            }
        };

        asyncHttpClient.post(Constants.connectdEndpoint + "/users/sign_in", params, jsonHttpResponseHandler);
        // TO DO: get user info from the server, and set the current user in User.class
//        User currentUser = User.createBuilder(userId)
//        User.setCurrentUser(currentUser);
    }
    
}
