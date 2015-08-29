package com.example.peter.connectd.rest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.EditText;
import android.widget.Toast;

import com.example.peter.connectd.R;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.CredentialRefreshListener;
import com.google.api.client.auth.oauth2.TokenErrorResponse;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.plus.PlusScopes;
import com.google.api.services.plusDomains.PlusDomains;
import com.google.api.services.plusDomains.PlusDomainsScopes;
import com.google.code.linkedinapi.client.LinkedInApiClient;
import com.google.code.linkedinapi.client.LinkedInApiClientFactory;
import com.google.code.linkedinapi.client.oauth.LinkedInAccessToken;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthService;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthServiceFactory;
import com.google.code.linkedinapi.client.oauth.LinkedInRequestToken;

import org.jinstagram.Instagram;
import org.jinstagram.auth.InstagramAuthService;
import org.jinstagram.auth.model.Token;
import org.jinstagram.auth.model.Verifier;
import org.jinstagram.auth.oauth.InstagramService;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;


/**
 * Class to manage all the different social media platforms.
 */
public class SocialApiClients {
    private static final String FACEBOOK_APP_SECRET = "4ee26bcb519450184d31917305b63357";
    private static final String TWITTER_CONSUMER_KEY = "lGJcPrnQCrMfLvhYlSRQCnEef";
    private static final String TWITTER_CONSUMER_SECRET = "dYKcPlUibwIW2EI0Bn51LotigUKdWpZofbcBIdYWzUshsjHRCg";
    private static final String GOOGLE_CLIENT_ID = "303584122338-26s78h7upkk6jmb3g9alsvfr5635hfh2.apps.googleusercontent.com";
    private static final String GOOGLE_CLIENT_SECRET = "K4nyOYBhBuEiLJFUx1ITMer9";
    private final static String LINKEDIN_CONSUMER_KEY = "77ew7bvhr0i9jf";
    private final static String LINKEDIN_CONSUMER_SECRET = "I5cH7FWjwqYGzno9";
    private final static String LINKEDIN_SCOPE_PARAMS = "w_messages";


    private static Twitter sTwitter;
    private static Instagram sInstagram;
    private static PlusDomains sPlusDomains;
    private static LinkedInApiClient sLinkedInApiClient;

    public enum SocialMediaName {
        TWITTER, FACEBOOK, INSTAGRAM, GPLUS, LINKEDIN
    }

    public static Twitter getTwitter(Activity activity) {
        if (sTwitter == null) {
            ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
            builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
            Configuration configuration = builder.build();
            TwitterFactory factory = new TwitterFactory(configuration);
            sTwitter = factory.getInstance();
            try {
                RequestToken requestToken = sTwitter
                        .getOAuthRequestToken("oauth://t4jsample");
                activity.startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri
                        .parse(requestToken.getAuthenticationURL())), 500);
                sTwitter.setOAuthAccessToken(sTwitter.getOAuthAccessToken(requestToken));
            } catch (TwitterException e) {
                e.printStackTrace();
            }
        }
        return sTwitter;
    }

    public static void initializeFacebook(Context context) {
        FacebookSdk.sdkInitialize(context);
        CallbackManager callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });
    }

    public static Instagram getInstagram() {
        if (sInstagram == null) {
            final Token EMPTY_TOKEN = null;
            InstagramService service = new InstagramAuthService()
                    .apiKey("your_client_id")
                    .apiSecret("your_client_secret")
                    .callback("your_callback_url")
                    .scope("SCOPE")
                    .build();
            String authorizationUrl = service.getAuthorizationUrl(EMPTY_TOKEN);
            Verifier verifier = new Verifier(authorizationUrl);
            Token accessToken = service.getAccessToken(EMPTY_TOKEN, verifier);
            sInstagram = new Instagram(accessToken);
        }
        return sInstagram;
    }

    public static PlusDomains getGPlus(final Context context) {
        if (sPlusDomains == null) {
            HttpTransport httpTransport = new NetHttpTransport();
            JsonFactory jsonFactory = new JacksonFactory();

            List<String> scopes = Arrays
                    .asList(PlusScopes.PLUS_ME, PlusDomainsScopes.PLUS_CIRCLES_READ,
                            PlusDomainsScopes.PLUS_CIRCLES_WRITE);
            final String REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob";

            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    new NetHttpTransport(),
                    new JacksonFactory(),
                    GOOGLE_CLIENT_ID, // This comes from your Developers Console project
                    GOOGLE_CLIENT_SECRET, // This, as well
                    scopes)
                    .setApprovalPrompt("force")
                    .setAccessType("offline").build();

            String url = flow.newAuthorizationUrl().setRedirectUri(REDIRECT_URI).build();
            Toast.makeText(context, "Redirecting to authorization URL", Toast.LENGTH_SHORT).show();

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            context.startActivity(browserIntent);

            EditText pinEditText = (EditText) ((Activity) context).findViewById(R.id.pin_edit_text);

            try {
                GoogleTokenResponse tokenResponse = flow.newTokenRequest(pinEditText.getText().toString())
                        .setRedirectUri(REDIRECT_URI).execute();

                // Setting the sub field with USER_EMAIL allows you to make API calls using the special keyword
                // 'me' in place of a user id for that user.
                GoogleCredential credential = new GoogleCredential.Builder()
                        .setTransport(httpTransport)
                        .setJsonFactory(jsonFactory)
                        .setClientSecrets(GOOGLE_CLIENT_ID,
                                GOOGLE_CLIENT_SECRET)
                        .addRefreshListener(new CredentialRefreshListener() {
                            @Override
                            public void onTokenResponse(Credential credential, TokenResponse tokenResponse) {
                                Toast.makeText(context, "Credential was refreshed successfully.",
                                        Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onTokenErrorResponse(Credential credential,
                                                             TokenErrorResponse tokenErrorResponse) {
                                Toast.makeText(context, "Credential was not refreshed successfully.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        })
                        .build();
                credential.setFromTokenResponse(tokenResponse);
                sPlusDomains = new PlusDomains
                        .Builder(new NetHttpTransport(), new JacksonFactory(), credential).build();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sPlusDomains;
    }

    public static LinkedInApiClient getLinkedIn(Context context) {
        if (sLinkedInApiClient == null) {

            EditText pinEditText = (EditText) ((Activity) context).findViewById(R.id.pin_edit_text);


            final String consumerKeyValue = LINKEDIN_CONSUMER_KEY;

            final String consumerSecretValue = LINKEDIN_CONSUMER_SECRET;
            final LinkedInOAuthService oauthService = LinkedInOAuthServiceFactory.getInstance().createLinkedInOAuthService(consumerKeyValue, consumerSecretValue, LINKEDIN_SCOPE_PARAMS);

            Toast.makeText(context, "Fetching request token from LinkedIn...", Toast.LENGTH_SHORT).show();

            LinkedInRequestToken requestToken = oauthService.getOAuthRequestToken();

            String authUrl = requestToken.getAuthorizationUrl();

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(authUrl));
            context.startActivity(browserIntent);

            Toast.makeText(context, "Fetching access token from LinkedIn...", Toast.LENGTH_SHORT).show();

            LinkedInAccessToken accessToken = oauthService.getOAuthAccessToken(requestToken, pinEditText.getText().toString());


            final LinkedInApiClientFactory factory = LinkedInApiClientFactory.newInstance(consumerKeyValue, consumerSecretValue);
            sLinkedInApiClient = factory.createLinkedInApiClient(accessToken);
        }
        return sLinkedInApiClient;
    }
}
