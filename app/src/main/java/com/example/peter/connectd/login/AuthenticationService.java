package com.example.peter.connectd.login;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AuthenticationService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return new AccountAuthenticator(this).getIBinder();
    }
}
