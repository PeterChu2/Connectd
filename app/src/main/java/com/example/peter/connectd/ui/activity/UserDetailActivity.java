package com.example.peter.connectd.ui.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
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
import com.example.peter.connectd.models.SearchResult;
import com.example.peter.connectd.models.User;
import com.example.peter.connectd.rest.ConnectdApiClient;
import com.example.peter.connectd.rest.ConnectdApiService;
import com.example.peter.connectd.rest.OnAsyncHttpRequestCompleteListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BinaryHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

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
    private FontAwesomeText mFaEdit;

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
                // TODO - reset password - setup SendGrid
            }
        });
        mFaEdit = (FontAwesomeText) findViewById(R.id.edit_basic_info);
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
                        JSONObject updateParams = new JSONObject();
                        JSONObject userParams = new JSONObject();
                        try {
                            userParams.put(User.USERNAME_KEY, mEtUsername.getText().toString());
                            userParams.put(User.FIRST_NAME_KEY, mEtFirstName.getText().toString());
                            userParams.put(User.LAST_NAME_KEY, mEtLastName.getText().toString());
                            userParams.put(User.EMAIL_KEY, mEtEmail.getText().toString());
                            updateParams.put(User.USER_KEY, userParams);
                        } catch (JSONException e) {
                            // NOP
                        }
                        mConnectedApiService.updateUser(UserDetailActivity.this, mCurrentUser.getId(), updateParams, UserDetailActivity.this);
                    } else {
                        if (!User.isUsernameValid(mEtUsername.getText().toString()) &&
                                !User.isEmailValid(mEtEmail.getText().toString())) {
                            Toast.makeText(UserDetailActivity.this,
                                    "Please enter a valid username and email!", Toast.LENGTH_SHORT).show();
                        } else if (!User.isUsernameValid(mEtUsername.getText().toString())) {
                            Toast.makeText(UserDetailActivity.this,
                                    "Username must be less than 20 characters!", Toast.LENGTH_SHORT).show();
                        } else if (!User.isEmailValid(mEtEmail.getText().toString())) {
                            Toast.makeText(UserDetailActivity.this,
                                    "Email is invalid!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onUserLoaded(User user) {
        mCurrentUser = user;
        if(mFaEdit.isActivated()) {
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
        }
        updateUI();
    }

    @Override
    public void onResultsLoaded(List<SearchResult> searchResults) {
        // NOP
    }

    @Override
    public void onUserLoadFailed(String error) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }

    private void updateUI() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String currentUserLogin = sharedPrefs.getString(ConnectdApiClient.SHAREDPREF_CURRENT_USER_KEY, "");
        if (currentUserLogin.equals(mCurrentUser.getEmail().toLowerCase()) ||
                currentUserLogin.equals(mCurrentUser.getUsername().toLowerCase())) {
            // viewing own detail page - show edit button, and reset password button
            FontAwesomeText faEdit = (FontAwesomeText) findViewById(R.id.edit_basic_info);
            faEdit.setVisibility(View.VISIBLE);
            BootstrapButton resetPwdButton = (BootstrapButton) findViewById(R.id.detail_reset_pwd);
            resetPwdButton.setVisibility(View.VISIBLE);
        } else {
            // show button to get Connectd
            BootstrapButton getConnectdButton = (BootstrapButton) findViewById(R.id.get_connectd_button);
            getConnectdButton.setVisibility(View.VISIBLE);
        }

        if(mCurrentUser.getProfilePicture() != null) {
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(mCurrentUser.getProfilePicture(), null, new BinaryHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] binaryData) {
                    BootstrapThumbnail profilePicture = (BootstrapThumbnail) findViewById(R.id.user_detail_photo);
                    Bitmap picture = BitmapFactory.decodeByteArray(binaryData, 0, binaryData.length);
                    profilePicture.setBackground(new BitmapDrawable(getResources(), picture));
                    profilePicture.invalidate();
                    profilePicture.requestLayout();
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] binaryData, Throwable error) {
                    // NOP
                }
            });
        }

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
