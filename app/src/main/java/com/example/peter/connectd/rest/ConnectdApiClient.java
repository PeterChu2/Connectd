package com.example.peter.connectd.rest;

/**
 * Created by peter on 27/08/15.
 */
public class ConnectdApiClient {
    public static String CONNECTD_ACCOUNT_TYPE="com.peter.connectd";
    public final static String CONNECTD_ENDPOINT = "http://192.168.0.11:3000";//"http://connectd.herokuapp.com";
    public final static String SHAREDPREF_LOGIN_KEY = "com.example.peter.connectd.login";
    public final static String AUTH_KEY = "com.example.peter.connectd.auth";
    public final static String SHAREDPREF_CURRENT_USER_KEY = "com.example.peter.connectd.current.user";
    public final static String RELATIVE_SEARCH_ENDPOINT = "user/search";
    public final static String RELATIVE_AUTOCOMPLETE_ENDPOINT = "users/autocomplete";
    public final static String RELATIVE_USERS_ENDPOINT= "users";

    public static ConnectdApiService getApiService() {
        return new ConnectdApiServiceImpl();
    }
}
