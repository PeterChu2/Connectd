package com.example.peter.connectd.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.example.peter.connectd.R;
import com.example.peter.connectd.rest.ConnectdApiClient;
import com.example.peter.connectd.rest.ConnectdApiService;
import com.example.peter.connectd.rest.ErrorCallBacks;

import org.json.JSONObject;

public class SignUpActivity extends Activity implements ErrorCallBacks {

    private BootstrapEditText mEtFirstName;
    private BootstrapEditText mEtLastName;
    private BootstrapEditText mEtEmail;
    private BootstrapEditText mEtUsername;
    private BootstrapEditText mEtPassword;
    private BootstrapEditText mEtPasswordConfirmation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_screen);
        mEtEmail = (BootstrapEditText) findViewById(R.id.sign_up_et_email);
        mEtUsername = (BootstrapEditText) findViewById(R.id.sign_up_et_username);
        mEtFirstName = (BootstrapEditText) findViewById(R.id.sign_up_et_first_name);
        mEtLastName = (BootstrapEditText) findViewById(R.id.sign_up_et_last_name);
        mEtPassword = (BootstrapEditText) findViewById(R.id.sign_up_et_password);
        mEtPasswordConfirmation = (BootstrapEditText) findViewById(R.id.sign_up_et_password_confirmation);
        BootstrapButton mSignUpButton = (BootstrapButton) findViewById(R.id.btnSignUpSubmit);
        final Context context = this;
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
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
                mEtPasswordConfirmation.getText().toString(), this);
    }

    @Override
    public void onError(JSONObject errors) {
        Toast.makeText(SignUpActivity.this, errors.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onError(String error) {
        Toast.makeText(SignUpActivity.this, error, Toast.LENGTH_LONG).show();
    }
}
