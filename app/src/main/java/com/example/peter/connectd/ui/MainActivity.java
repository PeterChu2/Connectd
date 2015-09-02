package com.example.peter.connectd.ui;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.example.peter.connectd.R;
import com.example.peter.connectd.rest.ConnectdApiClient;
import com.example.peter.connectd.rest.ConnectdApiService;
import com.example.peter.connectd.rest.OnAuthenticateListener;

public class MainActivity extends Activity implements OnClickListener, OnAuthenticateListener {

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BootstrapButton btnSignIn = (BootstrapButton) findViewById(R.id.btnSignIn);
        BootstrapButton btnSignUp = (BootstrapButton) findViewById(R.id.btnSignUp);

        btnSignIn.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);
        mProgressDialog = new ProgressDialog(this);
    }

    @Override
    public void onClick(View v) {
        Intent i;
        switch(v.getId()){
            case R.id.btnSignIn:
                AccountManager am = AccountManager.get(this);
                Account accounts[] = am.getAccountsByType(ConnectdApiClient.CONNECTD_ACCOUNT_TYPE);
                if(accounts.length >= 1) {
                    final String password = am.getPassword(accounts[0]);
                    if(password != null) {
                        mProgressDialog.setMessage("Signing in");
                        mProgressDialog.show();
                        ConnectdApiService mConnectdApiService = ConnectdApiClient.getApiService();
                        mConnectdApiService.signIn(this, accounts[0].name, password, this);
                    }
                }
                else {
                    i = new Intent(this, SignInActivity.class);
                    startActivity(i);
                }
                break;
            case R.id.btnSignUp:
                i = new Intent(this,SignUpActivity.class);
                startActivity(i);
                break;
        }
    }

    @Override
    public void onAuthenticate(String login) {
        mProgressDialog.dismiss();
        SharedPreferences.Editor sharedPreferencesEditor = PreferenceManager
                .getDefaultSharedPreferences(this).edit();
        sharedPreferencesEditor.putString(ConnectdApiClient.
                SHAREDPREF_LOGIN_KEY, login.toLowerCase()).apply();
        sharedPreferencesEditor.putString(ConnectdApiClient.
                SHAREDPREF_CURRENT_USER_KEY, login.toLowerCase()).apply();
        startActivity(new Intent(this, AuthenticatedHomeActivity.class));
    }

    @Override
    public void onAuthenticateFailed() {
        // bad credentials - go to sign in page
        mProgressDialog.dismiss();
        Intent i = new Intent(this, SignInActivity.class);
        startActivity(i);
    }
}