package com.example.peter.connectd.rest;

/**
 * Created by peter on 27/08/15.
 */
public class ConnectdApiClient {
    public static String CONNECTD_ACCOUNT_TYPE="com.peter.connectd";
    public final static String CONNECTD_ENDPOINT = "http://connectd.herokuapp.com";
    public final static String SHAREDPREF_LOGIN_KEY = "com.example.peter.connectd.login";
    public final static String SHAREDPREF_CURRENT_USER_KEY = "com.example.peter.connectd.current.user";
    public final static String RELATIVE_SEARCH_ENDPOINT = "user/search";
    public final static String RELATIVE_AUTOCOMPLETE_ENDPOINT = "users/autocomplete";
    public final static String CONNECTD_AUTH_TOKEN_TYPE = "com.example.peter.connectd";

    public static ConnectdApiService getApiService() {
        return new ConnectdApiServiceImpl();
    }
}
