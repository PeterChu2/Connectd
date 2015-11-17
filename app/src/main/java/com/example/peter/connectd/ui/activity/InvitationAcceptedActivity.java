package com.example.peter.connectd.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.peter.connectd.R;

/**
 * Class to accept NFC message from sender
 */
public class InvitationAcceptedActivity extends Activity {

    private TextView mTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc_display);
        mTextView = (TextView) findViewById(R.id.notice_text_view);
    }

}