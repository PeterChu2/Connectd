package com.example.peter.connectd.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.example.peter.connectd.R;

public class MainActivity extends Activity implements OnClickListener {

    BootstrapButton btnSignIn;
    BootstrapButton btnSignUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSignIn = (BootstrapButton) findViewById(R.id.btnSignIn);
        btnSignUp = (BootstrapButton) findViewById(R.id.btnSignUp);

        btnSignIn.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        Intent i = null;
        switch(v.getId()){
            case R.id.btnSignIn:
                i = new Intent(this,SignInActivity.class);
                break;
            case R.id.btnSignUp:
                i = new Intent(this,SignUpActivity.class);
                break;
        }
        startActivity(i);
    }
}