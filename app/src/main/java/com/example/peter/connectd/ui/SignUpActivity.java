package com.example.peter.connectd.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.peter.connectd.R;
import com.example.peter.connectd.rest.ConnectdApiClient;
import com.example.peter.connectd.rest.ConnectdApiService;

public class SignUpActivity extends Activity {

    private EditText mEtFirstName;
    private EditText mEtLastName;
    private EditText mEtEmail;
    private EditText mEtUsername;
    private EditText mEtPassword;
    private EditText mEtPasswordConfirmation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_screen);
        mEtEmail = (EditText) findViewById(R.id.sign_up_et_email);
        mEtUsername = (EditText) findViewById(R.id.sign_up_et_username);
        mEtFirstName = (EditText) findViewById(R.id.sign_up_et_first_name);
        mEtLastName = (EditText) findViewById(R.id.sign_up_et_last_name);
        mEtPassword = (EditText) findViewById(R.id.sign_up_et_password);
        mEtPasswordConfirmation = (EditText) findViewById(R.id.sign_up_et_password_confirmation);
        Button mSignInButton = (Button) findViewById(R.id.btnSignUpSubmit);
        final Context context = this;
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });
    }

    private void signUp() {
        ConnectdApiService mConnectdApiService = ConnectdApiClient.getApiService();
        mConnectdApiService.signUp(this, mEtUsername.getText().toString(),
                mEtEmail.getText().toString(), mEtFirstName.getText().toString(),
                mEtLastName.getText().toString(), mEtPassword.getText().toString(),
                mEtPasswordConfirmation.getText().toString());
    }
}
