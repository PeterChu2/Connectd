package com.example.peter.connectd.ui.activity;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
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

public class SignInActivity extends AccountAuthenticatorActivity implements OnAuthenticateListener {
    private BootstrapEditText mEtLogin;
    private BootstrapEditText mEtPassword;
    private ProgressDialog mProgressDialog;
    private AccountManager mAccountManager;

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
        mAccountManager = AccountManager.get(this);
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
    public void onAuthenticate(String login, String authToken) {
        mProgressDialog.dismiss();
        SharedPreferences.Editor sharedPreferencesEditor = PreferenceManager
                .getDefaultSharedPreferences(this).edit();
        sharedPreferencesEditor.putString(ConnectdApiClient.
                SHAREDPREF_LOGIN_KEY, login.toLowerCase()).apply();
        sharedPreferencesEditor.putString(ConnectdApiClient.
                SHAREDPREF_CURRENT_USER_KEY, login.toLowerCase()).apply();
        sharedPreferencesEditor.putString(ConnectdApiClient.
                AUTH_KEY, authToken).apply();
        // save account details in account manager
        final Account account = new Account(mEtLogin.getText().toString(),
                ConnectdApiClient.CONNECTD_ACCOUNT_TYPE);
        if(mAccountManager.addAccountExplicitly(account, mEtPassword.getText().toString(), null)) {
            Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ACCOUNT_NAME, mEtLogin.getText().toString());
            result.putString(AccountManager.KEY_ACCOUNT_TYPE,ConnectdApiClient.CONNECTD_ACCOUNT_TYPE);
            setAccountAuthenticatorResult(result);
        }
        startActivity(new Intent(this, AuthenticatedHomeActivity.class));
    }

    @Override
    public void onAuthenticateFailed() {
        mProgressDialog.dismiss();
        Toast.makeText(this, "Invalid username or password. Check your credentials.",
                Toast.LENGTH_SHORT).show();
    }
}
