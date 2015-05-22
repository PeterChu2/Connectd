package com.example.peter.connectd;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.facebook.FacebookSdk;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.plusDomains.PlusDomains;
import com.google.code.linkedinapi.client.LinkedInApiClient;
import com.google.code.linkedinapi.client.LinkedInApiClientFactory;

import org.jinstagram.Instagram;
import org.jinstagram.auth.InstagramAuthService;
import org.jinstagram.auth.model.Token;
import org.jinstagram.auth.model.Verifier;
import org.jinstagram.auth.oauth.InstagramService;

import java.security.PrivateKey;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

//import com.google.api.services.plusDomains.PlusDomains.Circles.AddPeople;
//import com.google.api.services.plusDomains.model.Circle;

/**
 * Created by peter on 22/05/15.
 */
public class SocialApiClients {
    FACEBOOK_APP_SECRET=4ee26bcb519450184d31917305b63357

    GPLUS_APP_ID=303584122338-26s78h7upkk6jmb3g9alsvfr5635hfh2.apps.googleusercontent.com
            GPLUS_APP_SECRET=K4nyOYBhBuEiLJFUx1ITMer9

    public static Twitter getTwitter() {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.setOAuthConsumerKey("lGJcPrnQCrMfLvhYlSRQCnEef");
        builder.setOAuthConsumerSecret("dYKcPlUibwIW2EI0Bn51LotigUKdWpZofbcBIdYWzUshsjHRCg");
        Configuration configuration = builder.build();
        TwitterFactory factory = new TwitterFactory(configuration);
        Twitter twitter = factory.getInstance();
        try {
            RequestToken requestToken = twitter
                    .getOAuthRequestToken(TWITTER_CALLBACK_URL);
            this.startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri
                    .parse(requestToken.getAuthenticationURL())), 500);
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        return twitter;
    }

    public static void initializeFacebook(Context context) {
        FacebookSdk.sdkInitialize(context);
    }

    public static Instagram getInstagram() {
        static final Token EMPTY_TOKEN = null;
        InstagramService service = new InstagramAuthService()
                .apiKey("your_client_id")
                .apiSecret("your_client_secret")
                .callback("your_callback_url")
                .scope("SCOPE")
                .build();
        String authorizationUrl = service.getAuthorizationUrl(EMPTY_TOKEN);
        Verifier verifier = new Verifier(authorizationUrl);
        Token accessToken = service.getAccessToken(EMPTY_TOKEN, verifier);
        Instagram instagram = new Instagram(accessToken);
        return instagram;
    }

    public static PlusDomains getGPlus() {
        HttpTransport httpTransport = new NetHttpTransport();
        JsonFactory jsonFactory = new JacksonFactory();

        // Setting the sub field with USER_EMAIL allows you to make API calls using the special keyword
        // 'me' in place of a user id for that user.
        PlusDomains plusDomain = new GoogleCredential.Builder()
                .setTransport(httpTransport)
                .setJsonFactory(jsonFactory)
                .setServiceAccountId("ewong2222@gmail.com")
                .setServiceAccountScopes("")
                .setServiceAccountUser(USER_EMAIL);
                .setServiceAccountPrivateKey(new PrivateKey() {
                    @Override
                    public String getAlgorithm() {
                        return null;
                    }

                    @Override
                    public String getFormat() {
                        return null;
                    }

                    @Override
                    public byte[] getEncoded() {
                        return new byte[0];
                    }
                })
                .setServiceAccountPrivateKeyFromP12File(
                        new java.io.File(SERVICE_ACCOUNT_PKCS12_FILE_PATH))
                .build();
        return plusDomain;
    }

    public static LinkedInApiClient getLinkedIn() {
        final LinkedInApiClientFactory factory = LinkedInApiClientFactory.newInstance("77ew7bvhr0i9jf", "I5cH7FWjwqYGzno9");
        final LinkedInApiClient client = factory.createLinkedInApiClient(accessTokenValue, tokenSecretValue);
    }
}
