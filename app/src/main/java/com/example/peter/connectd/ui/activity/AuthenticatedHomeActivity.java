package com.example.peter.connectd.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapCircleThumbnail;
import com.example.peter.connectd.R;
import com.example.peter.connectd.models.SearchResult;
import com.example.peter.connectd.models.User;
import com.example.peter.connectd.rest.ConnectdApiClient;
import com.example.peter.connectd.rest.ConnectdApiService;
import com.example.peter.connectd.rest.OnAsyncHttpRequestCompleteListener;
import com.quinny898.library.persistentsearch.SearchBox;

import java.util.ArrayList;
import java.util.List;

import static android.nfc.NdefRecord.createMime;

/**
 * Created by peter on 22/05/15.
 */
public class AuthenticatedHomeActivity extends Activity implements NfcAdapter.CreateNdefMessageCallback,
        OnAsyncHttpRequestCompleteListener {
    private NfcAdapter mAdapter;
    private BootstrapCircleThumbnail mThumbnail;
    private SearchBox mSearchBox;
    private ConnectdApiService mConnectdApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authenticated_home);
        mConnectdApiService = ConnectdApiClient.getApiService();
        mThumbnail = (BootstrapCircleThumbnail) findViewById(R.id.thumbnail);
        mThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPrefs = PreferenceManager
                        .getDefaultSharedPreferences(AuthenticatedHomeActivity.this);
                SharedPreferences.Editor sharedPrefEditor = sharedPrefs.edit();
                sharedPrefEditor.putString(ConnectdApiClient.SHAREDPREF_LOGIN_KEY, sharedPrefs
                        .getString(ConnectdApiClient.SHAREDPREF_CURRENT_USER_KEY, null)).commit();
                Intent i = new Intent(AuthenticatedHomeActivity.this, UserDetailActivity.class);
                startActivity(i);
            }
        });
        TextView textView = (TextView) findViewById(R.id.instructions_text_view);
        mAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mAdapter == null) {
            textView.setText("Sorry this device does not have NFC.");
            return;
        }
        if (!mAdapter.isEnabled()) {
            Toast.makeText(this, "Please enable NFC via Settings.", Toast.LENGTH_LONG).show();
        }
        mAdapter.setNdefPushMessageCallback(this, this);

        mSearchBox = (SearchBox) findViewById(R.id.search_box);
        mSearchBox.setSearchListener(new SearchBox.SearchListener() {
            @Override
            public void onSearchOpened() {
                // TODO - tint the screen
            }

            @Override
            public void onSearchCleared() {
            }

            @Override
            public void onSearchClosed() {
                // TODO - untint the screen
            }

            @Override
            public void onSearchTermChanged() {
                // TODO - show search suggestions
                mConnectdApiService.findUsers(AuthenticatedHomeActivity.this,
                        mSearchBox.getSearchText(), AuthenticatedHomeActivity.this);
            }

            @Override
            public void onSearch(String searchQuery) {


                Intent i;
//                if(mSearchBox.getNumberOfResults() != 0) {
//                     TODO - user must exist, if not - redirect to search results page
//                SharedPreferences.Editor sharedPreferencesEditor = PreferenceManager
//                        .getDefaultSharedPreferences(AuthenticatedHomeActivity.this).edit();
//                sharedPreferencesEditor.putString(ConnectdApiClient.SHAREDPREF_LOGIN_KEY, searchQuery).commit();
//                    i = new Intent(AuthenticatedHomeActivity.this, UserDetailActivity.class);
//                } else {
                    i = new Intent(AuthenticatedHomeActivity.this, ResultsActivity.class);
                    i.putExtra(ResultsActivity.QUERY_STRING, searchQuery);
//                }
                startActivity(i);
            }
        });
        // TODO - enable features of search box
        mSearchBox.enableVoiceRecognition(this);
        mSearchBox.setLogoText("Connectd");
    }

    @Override
    protected void onPause() {
        mAdapter.setNdefPushMessageCallback(null, this);
        super.onPause();
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        String text = String.valueOf(User.getCurrentUser().getId());
        return new NdefMessage(createMime(
                        "text/plain", text.getBytes()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SearchBox.VOICE_RECOGNITION_CODE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            mSearchBox.setSearchString(matches.get(0));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onUserLoaded(User user) {
        // NOP
    }

    @Override
    public void onResultsLoaded(List<SearchResult> results) {
        for (SearchResult result : results) {
            com.quinny898.library.persistentsearch.SearchResult option;
            if (result.getPictureUrl() != null) {
                option = new com.quinny898.library.persistentsearch.SearchResult(result.getUsername(),
                        ContextCompat.getDrawable(this, R.drawable.icon_user_default));
            } else {
                option = new com.quinny898.library.persistentsearch.SearchResult(result.getUsername(),
                        ContextCompat.getDrawable(this, R.drawable.icon_user_default));
            }
            mSearchBox.addSearchable(option);
        }
    }

    @Override
    public void onUserLoadFailed(String error) {
        // NOP
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to log out?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AuthenticatedHomeActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
