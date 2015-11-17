package com.example.peter.connectd.ui.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.beardedhen.androidbootstrap.BootstrapThumbnail;
import com.example.peter.connectd.R;
import com.example.peter.connectd.models.SearchResult;
import com.example.peter.connectd.models.User;
import com.example.peter.connectd.rest.ConnectdApiClient;
import com.example.peter.connectd.rest.ConnectdApiService;
import com.example.peter.connectd.rest.OnAsyncHttpRequestCompleteListener;
import com.example.peter.connectd.rest.SocialApiClients;
import com.google.api.services.plusDomains.PlusDomains;
import com.google.api.services.plusDomains.model.Circle;
import com.google.api.services.plusDomains.model.CircleFeed;
import com.google.code.linkedinapi.client.LinkedInApiClient;
import com.google.code.linkedinapi.client.constant.ApplicationConstants;
import com.google.code.linkedinapi.schema.HttpHeader;
import com.google.code.linkedinapi.schema.Person;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BinaryHttpResponseHandler;

import org.apache.http.Header;
import org.jinstagram.Instagram;
import org.jinstagram.exceptions.InstagramException;
import org.jinstagram.model.Relationship;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import twitter4j.Twitter;
import twitter4j.TwitterException;

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
    private AwesomeTextView mFaEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_detail_activity);
        mConnectedApiService = ConnectdApiClient.getApiService();

        BootstrapButton resetPasswordButton = (BootstrapButton) findViewById(R.id.detail_reset_pwd);
        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO - reset password - setup SendGrid
            }
        });
        mFaEdit = (AwesomeTextView) findViewById(R.id.edit_basic_info);
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
    protected void onResume() {
        super.onResume();
        Log.d("PETER", "TEST");
        Intent intent = getIntent();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Parcelable[] rawMessages = intent.getParcelableArrayExtra(
                    NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage message = (NdefMessage) rawMessages[0]; // only one message transferred
            String friendLogin = new String(message.getRecords()[0].getPayload());
            mConnectedApiService.findUserByLogin(this, friendLogin, this);
        } else {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            String login = sharedPreferences.getString(ConnectdApiClient.SHAREDPREF_LOGIN_KEY, null);
            if (login != null) {
                mConnectedApiService.findUserByLogin(this, login, this);
            }
        }
    }

    private void sendInvitation(User user) {
        for (User.Authorization authorization : user.getAuthorizations()) {
            switch (authorization.getSocialMediaName()) {
                case FACEBOOK:
                    break;
                case TWITTER:
                    Twitter twitterClient = SocialApiClients.getTwitter(this);
                    try {
                        twitterClient.createFriendship(authorization.getIdentifier());
                    } catch (TwitterException e) {
                        e.printStackTrace();
                    }
                    break;
                case GPLUS:
                    PlusDomains plusDomainsClient = SocialApiClients.getGPlus(this);
                    List<String> addUserIds = new ArrayList<String>();
                    addUserIds.add(authorization.getIdentifier());
                    try {
                        PlusDomains.Circles.List listCircles = plusDomainsClient.circles().list("me");
                        listCircles.setMaxResults(1L);
                        CircleFeed circleFeed = listCircles.execute();
                        List<Circle> circles = circleFeed.getItems();
                        for (Circle circle : circles) {
                            PlusDomains.Circles.AddPeople addPeople = plusDomainsClient.circles()
                                    .addPeople(circle.getId());
                            addPeople.setUserId(addUserIds);
                            addPeople.execute();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case INSTAGRAM:
                    Instagram instagramClient = SocialApiClients.getInstagram();
                    int userId = Integer.valueOf(authorization.getIdentifier());
                    try {
                        instagramClient.setUserRelationship(userId, Relationship.FOLLOW);
                    } catch (InstagramException e) {
                        e.printStackTrace();
                    }
                    break;
                case LINKEDIN:
                    LinkedInApiClient linkedInApiClient = SocialApiClients.getLinkedIn(this);
                    Person person = linkedInApiClient.getProfileForCurrentUser();
                    String authHeader = "";
                    for (HttpHeader header : person.getApiStandardProfileRequest().getHeaders().getHttpHeaderList()) {
                        if (ApplicationConstants.AUTH_HEADER_NAME.equals(header.getName())) {
                            authHeader = header.getName();
                        }
                    }
                    linkedInApiClient.sendInviteById(authorization.getIdentifier(),
                            "Invitation to connect", "Please add me to your network. Invitation sent from Connectd",
                            authHeader);
                    break;
            }
        }
    }

    @Override
    public void onUserLoaded(User user) {
        mCurrentUser = user;
        if (mFaEdit.isActivated()) {
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
            AwesomeTextView faEdit = (AwesomeTextView) findViewById(R.id.edit_basic_info);
            faEdit.setVisibility(View.VISIBLE);
            BootstrapButton resetPwdButton = (BootstrapButton) findViewById(R.id.detail_reset_pwd);
            resetPwdButton.setVisibility(View.VISIBLE);


            // show button to get Connectd
            BootstrapButton getConnectdButton = (BootstrapButton) findViewById(R.id.get_connectd_button);
            getConnectdButton.setVisibility(View.VISIBLE);
            getConnectdButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AccountManager accountManager = AccountManager.get(UserDetailActivity.this);
                    Account[] accounts = accountManager.getAccountsByType(null);
                    if(accounts != null) {
                        for (Account account : accounts) {
                            User.Authorization auth = null;
                            switch (account.type) {
                                case SocialApiClients.TWITTER_ACCOUNT_TYPE:
                                    auth = new User.Authorization(SocialApiClients.Name.TWITTER, account.name, null);
                                    mCurrentUser.addAuthorization(UserDetailActivity.this, mConnectedApiService, auth);
                                    break;
                                case SocialApiClients.EMAIL_ACCOUNT_TYPE:
                                    break;
                                case SocialApiClients.FACEBOOK_ACCOUNT_TYPE:
                                    auth = new User.Authorization(SocialApiClients.Name.FACEBOOK, account.name, null);
                                    mCurrentUser.addAuthorization(UserDetailActivity.this, mConnectedApiService, auth);
                                    break;
                                case SocialApiClients.GITHUB_ACCOUNT:
                                    break;
                                case SocialApiClients.GOOGLE_ACCOUNT:
                                    auth = new User.Authorization(SocialApiClients.Name.GPLUS, account.name, null);
                                    mCurrentUser.addAuthorization(UserDetailActivity.this, mConnectedApiService, auth);
                                    break;
                                case SocialApiClients.LINKEDIN_ACCOUNT_TYPE:
                                    auth = new User.Authorization(SocialApiClients.Name.LINKEDIN, account.name, null);
                                    mCurrentUser.addAuthorization(UserDetailActivity.this, mConnectedApiService, auth);
                                    break;
                            }
                        }
                    }
                }
            });

        } else {
            // show button to get Connectd
            BootstrapButton getConnectdButton = (BootstrapButton) findViewById(R.id.get_connectd_button);
            getConnectdButton.setVisibility(View.VISIBLE);
            getConnectdButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        sendConnectionRequests();
                    } catch (AuthenticatorException e) {
                        e.printStackTrace();
                    } catch (OperationCanceledException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        if (mCurrentUser.getProfilePicture() != null) {
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

    private void sendConnectionRequests() throws AuthenticatorException, OperationCanceledException, IOException {
        final AccountManager accountManager = AccountManager.get(this);
        Account socialMediaAccount1 = accountManager.getAccountsByType(SocialApiClients.TWITTER_ACCOUNT_TYPE)[0];
//        String auth = accountManager.getAuthToken(socialMediaAccount1, SocialApiClients.TWITTER_ACCOUNT_TYPE, null, UserDetailActivity.this, new AccountManagerCallback<Bundle>() {
//            @Override
//            public void run(AccountManagerFuture<Bundle> arg0) {
//                try {
//                    Bundle b = arg0.getResult();
//                    Log.d("PETER", "twitter THIS AUHTOKEN: "
//                            + b.getString(AccountManager.KEY_AUTHTOKEN));
//                    Intent launch = (Intent) b
//                            .get(AccountManager.KEY_INTENT);
//                    if (launch != null) {
//                        startActivityForResult(launch, 0);
//                        return;
//                    }
//                } catch (Exception e) {
//                    Log.d("PETER", "EXCEPTION@AUTHTOKEN");
//                }
//            }
//        }, new Handler()).getResult().getString(AccountManager.KEY_AUTHTOKEN);
        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                final AccountManager accountManager = AccountManager.get(UserDetailActivity.this);
                Account socialMediaAccount1 = accountManager.getAccountsByType(SocialApiClients.TWITTER_ACCOUNT_TYPE)[0];
                Log.d("PETER", "got twitter");
                String authToken1 = null;
                try {
                    Log.d("PETER", "tryGET");
//                    authToken1 = accountManager.blockingGetAuthToken(socialMediaAccount1, SocialApiClients.TWITTER_AUTH_TOKEN_TYPE, true);
                    String tok = accountManager.getAuthToken(socialMediaAccount1, SocialApiClients.TWITTER_ACCOUNT_TYPE, null, UserDetailActivity.this, new AccountManagerCallback<Bundle>() {
                        @Override
                        public void run(AccountManagerFuture<Bundle> arg0) {
                            try {
                                Bundle b = arg0.getResult();
                                Log.d("PETER", "twitter THIS AUHTOKEN: "
                                        + b.getString(AccountManager.KEY_AUTHTOKEN));
                                Intent launch = (Intent) b
                                        .get(AccountManager.KEY_INTENT);
                                if (launch != null) {
                                    startActivityForResult(launch, 0);
                                    return;
                                }
                            } catch (Exception e) {
                                Log.d("PETER", "EXCEPTION@AUTHTOKEN");
                            }
                        }
                    }, null).getResult().getString(AccountManager.KEY_AUTHTOKEN);
                    Log.d("PETER", "got "+tok);
                    return tok;
                } catch (OperationCanceledException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (AuthenticatorException e) {
                    e.printStackTrace();
                }
                return authToken1;
            }

            @Override
            protected void onPostExecute(String token) {
                Log.d("PETER", "Access token retrieved:" + token);
            }

        };
        task.execute();
//        String authToken1 = accountManager.blockingGetAuthToken(socialMediaAccount1, SocialApiClients.TWITTER_AUTH_TOKEN_TYPE, false);
//        Log.d("PETER", "auth token is " + authToken1);
//        for(User.Authorization authorization : mCurrentUser.getAuthorizations()) {
//            Account socialMediaAccount;
//            switch(authorization.getSocialMediaName()) {
//                case TWITTER:
//                    socialMediaAccount = accountManager.getAccountsByType(SocialApiClients.TWITTER_ACCOUNT_TYPE)[0];
//                    String authToken = accountManager.blockingGetAuthToken(socialMediaAccount, SocialApiClients.TWITTER_AUTH_TOKEN_TYPE, false);
//                    Log.d("PETER", "auth token is " + authToken);
//                    break;
//                case LINKEDIN:
//                    break;
//            }
//        }
    }
}
