package com.example.peter.connectd.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.peter.connectd.R;
import com.example.peter.connectd.models.User;
import com.example.peter.connectd.rest.ConnectdApiClient;
import com.example.peter.connectd.rest.ConnectdApiService;

import static android.nfc.NdefRecord.createMime;

/**
 * Created by peter on 22/05/15.
 */
public class AuthenticatedHomeActivity extends Activity implements NfcAdapter.CreateNdefMessageCallback {
    NfcAdapter mAdapter;
    User mCurrentUser;
    ConnectdApiService mConnectedApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authenticated_home);
        mConnectedApiService = ConnectdApiClient.getApiService();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String login = sharedPreferences.getString(ConnectdApiClient.SHAREDPREF_LOGIN_KEY, null);
        if(login != null && login.contains("@")) {
            mCurrentUser = mConnectedApiService.findUserByEmail(login);
        } else if(login != null) {
            mCurrentUser = mConnectedApiService.findUserByUsername(login);
        }

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
    }

    @Override
    protected void onPause() {
        mAdapter.setNdefPushMessageCallback(null, this);
        super.onPause();
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        String text = String.valueOf(User.getCurrentUser().getId());
        return new NdefMessage(
                new NdefRecord[]{createMime(
                        "application/vnd.com.example.peter.connectd", text.getBytes())
                        , NdefRecord.createApplicationRecord("com.example.peter.connectd")
                });
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
}
