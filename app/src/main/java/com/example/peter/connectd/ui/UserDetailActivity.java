package com.example.peter.connectd.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.EditText;

import com.example.peter.connectd.R;
import com.example.peter.connectd.models.User;
import com.example.peter.connectd.rest.ConnectdApiClient;
import com.example.peter.connectd.rest.ConnectdApiService;
import com.example.peter.connectd.rest.OnAsyncHttpRequestCompleteListener;

import java.util.List;

/**
 * Created by peter on 27/08/15.
 */
public class UserDetailActivity extends Activity implements OnAsyncHttpRequestCompleteListener {
    ConnectdApiService mConnectedApiService;
    User mCurrentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_detail_activity);
        mConnectedApiService = ConnectdApiClient.getApiService();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String login = sharedPreferences.getString(ConnectdApiClient.SHAREDPREF_LOGIN_KEY, null);
        if(login != null) {
            mConnectedApiService.findUserByLogin(this, login, this);
        }
    }

    @Override
    public void onUserLoaded(User user) {
        mCurrentUser = user;
        updateUI();
    }

    @Override
    public void onUsersLoaded(List<User> users) {
        // NOP
    }

    private void updateUI() {
        Log.d("PETER", "updating ui");
        EditText etUserName = (EditText) findViewById(R.id.user_detail_username);
        EditText etFirstName = (EditText) findViewById(R.id.user_detail_first_name);
        EditText etLastName = (EditText) findViewById(R.id.user_detail_last_name);
        EditText etEmail = (EditText) findViewById(R.id.user_detail_email);
        etUserName.setText(mCurrentUser.getUsername());
        etFirstName.setText(mCurrentUser.getFirstName());
        etLastName.setText(mCurrentUser.getLastName());
        etEmail.setText(mCurrentUser.getEmail());
    }
}
