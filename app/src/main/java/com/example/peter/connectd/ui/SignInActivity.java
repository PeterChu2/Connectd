package com.example.peter.connectd.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.example.peter.connectd.R;
import com.example.peter.connectd.rest.ConnectdApiClient;
import com.example.peter.connectd.rest.ConnectdApiService;
import com.example.peter.connectd.rest.OnAuthenticateListener;

public class SignInActivity extends Activity implements OnAuthenticateListener {
    private BootstrapEditText mEtLogin;
    private BootstrapEditText mEtPassword;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_screen);
        BootstrapButton signInButton = (BootstrapButton) findViewById(R.id.btnSignInSubmit);
        mEtLogin = (BootstrapEditText) findViewById(R.id.sign_in_et_email);
        mEtPassword = (BootstrapEditText) findViewById(R.id.sign_in_et_pass);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        mProgressDialog = new ProgressDialog(this);
    }

    private void signIn() {
        String login = mEtLogin.getText().toString();
        String password = mEtPassword.getText().toString();
        mProgressDialog.setMessage("Signing in");
        mProgressDialog.show();
        ConnectdApiService mConnectdApiService = ConnectdApiClient.getApiService();
        mConnectdApiService.signIn(this, login, password, this);
    }

    @Override
    public void onAuthenticate(String login) {
        mProgressDialog.dismiss();
        SharedPreferences.Editor sharedPreferencesEditor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        sharedPreferencesEditor.putString(ConnectdApiClient.SHAREDPREF_LOGIN_KEY, login.toLowerCase()).commit();
        sharedPreferencesEditor.putString(ConnectdApiClient.SHAREDPREF_CURRENT_USER_KEY, login.toLowerCase()).apply();
        startActivity(new Intent(this, AuthenticatedHomeActivity.class));
    }

    @Override
    public void onAuthenticateFailed() {
        mProgressDialog.dismiss();
        Toast.makeText(this, "Invalid username or password. Check your credentials.",
                Toast.LENGTH_SHORT).show();
    }
}
