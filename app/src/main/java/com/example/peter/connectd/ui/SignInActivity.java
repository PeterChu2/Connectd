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

public class SignInActivity extends Activity {

    private EditText mEtLogin;
    private EditText mEtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_screen);
        Button signInButton = (Button) findViewById(R.id.btnSignInSubmit);
        mEtLogin = (EditText) findViewById(R.id.sign_in_et_email);
        mEtPassword = (EditText) findViewById(R.id.sign_in_et_pass);
        final Context context = this;
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    private void signIn() {
        String login = mEtLogin.getText().toString();
        String password = mEtPassword.getText().toString();
        ConnectdApiService mConnectdApiService = ConnectdApiClient.getApiService();
        mConnectdApiService.signIn(this, login, password);
    }
}
