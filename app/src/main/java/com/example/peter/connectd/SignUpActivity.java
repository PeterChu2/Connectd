package com.example.peter.connectd;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

public class SignUpActivity extends Activity {

    private EditText mEtFirstName;
    private EditText mEtLastName;
    private EditText mEtEmail;
    private EditText mEtUsername;
    private EditText mEtPassword;
    private EditText mEtPasswordConfirmation;
    private Button mSignInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_screen);
        mEtEmail = (EditText) findViewById(R.id.sign_in_et_email);
        mEtFirstName = (EditText) findViewById(R.id.sign_in_et_first_name);
        mEtLastName = (EditText) findViewById(R.id.sign_up_et_last_name);
        mEtPassword = (EditText) findViewById(R.id.sign_up_et_password);
        mEtPasswordConfirmation = (EditText) findViewById(R.id.sign_up_et_password_confirmation);
        mSignInButton = (Button) findViewById(R.id.btnSignIn);
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
                RequestParams params = new RequestParams();
                params.put("first_name", mEtFirstName);
                params.put("last_name", mEtLastName);
                params.put("email", mEtEmail);
                params.put("password", mEtPassword);
                params.put("password_confirmation", mEtPasswordConfirmation);
                asyncHttpClient.post(Constants.connectdEndpoint + "sign_up", params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        super.onSuccess(statusCode, headers, response);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        super.onSuccess(statusCode, headers, responseString);
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
                        super.onFailure(statusCode, headers, responseString, throwable);
                    }
                });
            }
        });
    }
}
