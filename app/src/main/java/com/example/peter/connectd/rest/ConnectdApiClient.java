package com.example.peter.connectd.rest;

/**
 * Created by peter on 27/08/15.
 */
public class ConnectdApiClient {
    public final static String CONNECTD_ENDPOINT = "http://connectd.herokuapp.com";
    public final static String SHAREDPREF_LOGIN_KEY = "com.example.peter.connectd.login";
    public final static String RELATIVE_SEARCH_ENDPOINT = "users/search";

    public static ConnectdApiService getApiService() {
        return new ConnectdApiServiceImpl();
    }
}
