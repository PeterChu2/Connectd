package com.example.peter.connectd.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.beardedhen.androidbootstrap.BootstrapThumbnail;
import com.beardedhen.androidbootstrap.FontAwesomeText;
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
    private ConnectdApiService mConnectedApiService;
    private User mCurrentUser;
    private BootstrapEditText mEtUsername;
    private BootstrapEditText mEtFirstName;
    private BootstrapEditText mEtLastName;
    private BootstrapEditText mEtEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_detail_activity);
        mConnectedApiService = ConnectdApiClient.getApiService();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String login = sharedPreferences.getString(ConnectdApiClient.SHAREDPREF_LOGIN_KEY, null);
        if (login != null) {
            mConnectedApiService.findUserByLogin(this, login, this);
        }
        BootstrapButton resetPasswordButton = (BootstrapButton) findViewById(R.id.detail_reset_pwd);
        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO
            }
        });
        final FontAwesomeText mFaEdit = (FontAwesomeText) findViewById(R.id.edit_basic_info);
        mFaEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mFaEdit.isActivated()) {
                    mFaEdit.setActivated(true);
                    mEtUsername.setEnabled(true);
                    mEtUsername.setFocusable(true);
                    mEtEmail.setEnabled(true);
                    mEtEmail.setFocusable(true);
                    mEtFirstName.setEnabled(true);
                    mEtFirstName.setFocusable(true);
                    mEtLastName.setEnabled(true);
                    mEtLastName.setFocusable(true);
                } else {
                    if (User.isUsernameValid(mEtUsername.getText().toString())
                            && User.isEmailValid(mEtEmail.getText().toString())) {
                        mFaEdit.setActivated(false);
                        mEtUsername.setEnabled(false);
                        mEtUsername.setFocusable(false);
                        mEtEmail.setEnabled(false);
                        mEtEmail.setFocusable(false);
                        mEtFirstName.setEnabled(false);
                        mEtFirstName.setFocusable(false);
                        mEtLastName.setEnabled(false);
                        mEtLastName.setFocusable(false);
                        Toast.makeText(UserDetailActivity.this, "SAVED!", Toast.LENGTH_SHORT).show();
                    } else {
                        if (!User.isUsernameValid(mEtUsername.getText().toString()) &&
                                !User.isEmailValid(mEtEmail.getText().toString())) {
                            Toast.makeText(UserDetailActivity.this, String
                                    .format("Username and email is already in use!"), Toast.LENGTH_SHORT).show();
                        } else if (!User.isUsernameValid(mEtUsername.getText().toString())) {
                            Toast.makeText(UserDetailActivity.this, String
                                    .format("Username is already in use!"), Toast.LENGTH_SHORT).show();
                        } else if (!User.isEmailValid(mEtEmail.getText().toString())) {
                            Toast.makeText(UserDetailActivity.this, String
                                    .format("Email is already in use!"), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
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
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String login = sharedPrefs.getString(ConnectdApiClient.SHAREDPREF_LOGIN_KEY, "");
        if (login.equals(mCurrentUser.getEmail().toLowerCase()) ||
                login.equals(mCurrentUser.getUsername().toLowerCase())) {
            // viewing own detail page - show edit button, and reset password button
            FontAwesomeText faEdit = (FontAwesomeText) findViewById(R.id.edit_basic_info);
            faEdit.setVisibility(View.VISIBLE);
            BootstrapButton resetPwdButton = (BootstrapButton) findViewById(R.id.detail_reset_pwd);
            resetPwdButton.setVisibility(View.VISIBLE);
        }

        // TODO - if user has a profile picture, set the thumbnail
        BootstrapThumbnail profilePicture = (BootstrapThumbnail) findViewById(R.id.user_detail_photo);


        TextView mTvFullName = (TextView) findViewById(R.id.detail_name);
        mTvFullName.setText(String.format("%s %s",
                mCurrentUser.getFirstName(), mCurrentUser.getLastName()));
        mEtUsername = (BootstrapEditText) findViewById(R.id.user_detail_username);
        mEtFirstName = (BootstrapEditText) findViewById(R.id.user_detail_first_name);
        mEtLastName = (BootstrapEditText) findViewById(R.id.user_detail_last_name);
        mEtEmail = (BootstrapEditText) findViewById(R.id.user_detail_email);
        mEtUsername.setText(mCurrentUser.getUsername());
        mEtFirstName.setText(mCurrentUser.getFirstName());
        mEtLastName.setText(mCurrentUser.getLastName());
        mEtEmail.setText(mCurrentUser.getEmail());
    }
}
