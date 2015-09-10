package com.example.peter.connectd.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.TextView;

import com.example.peter.connectd.R;
import com.example.peter.connectd.models.SearchResult;
import com.example.peter.connectd.models.User;
import com.example.peter.connectd.rest.ConnectdApiClient;
import com.example.peter.connectd.rest.ConnectdApiService;
import com.example.peter.connectd.rest.OnAsyncHttpRequestCompleteListener;
import com.example.peter.connectd.rest.SocialApiClients;
import com.google.api.services.plusDomains.PlusDomains;
import com.google.api.services.plusDomains.model.Circle;
import com.google.api.services.plusDomains.model.CircleFeed;
import com.google.code.linkedinapi.client.LinkedInApiClient;
import com.google.code.linkedinapi.client.constant.ApplicationConstants;
import com.google.code.linkedinapi.schema.HttpHeader;
import com.google.code.linkedinapi.schema.Person;

import org.jinstagram.Instagram;
import org.jinstagram.exceptions.InstagramException;
import org.jinstagram.model.Relationship;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Class to accept NFC message from sender
 */
public class InvitationAcceptedActivity extends Activity implements OnAsyncHttpRequestCompleteListener {

    private TextView mTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc_display);
        mTextView = (TextView) findViewById(R.id.notice_text_view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Parcelable[] rawMessages = intent.getParcelableArrayExtra(
                    NfcAdapter.EXTRA_NDEF_MESSAGES);

            NdefMessage message = (NdefMessage) rawMessages[0]; // only one message transferred
            String friendLogin = new String(message.getRecords()[0].getPayload());
            mTextView.setText(String
                    .format("Received User data from friend with user login: %s", friendLogin));

            ConnectdApiService connectdApiService = ConnectdApiClient.getApiService();
            connectdApiService.findUserByLogin(this, friendLogin, this);
        }
    }

    private void sendInvitation(User user) {
        for (User.Authorization authorization : user.getAuthorizations()) {
            switch (authorization.getSocialMediaName()) {
                case FACEBOOK:
                    break;
                case TWITTER:
                    Twitter twitterClient = SocialApiClients.getTwitter(this);
                    try {
                        twitterClient.createFriendship(authorization.getIdentifier());
                    } catch (TwitterException e) {
                        e.printStackTrace();
                    }
                    break;
                case GPLUS:
                    PlusDomains plusDomainsClient = SocialApiClients.getGPlus(this);
                    List<String> addUserIds = new ArrayList<String>();
                    addUserIds.add(authorization.getIdentifier());
                    try {
                        PlusDomains.Circles.List listCircles = plusDomainsClient.circles().list("me");
                        listCircles.setMaxResults(1L);
                        CircleFeed circleFeed = listCircles.execute();
                        List<Circle> circles = circleFeed.getItems();
                        for (Circle circle : circles) {
                            PlusDomains.Circles.AddPeople addPeople = plusDomainsClient.circles()
                                    .addPeople(circle.getId());
                            addPeople.setUserId(addUserIds);
                            addPeople.execute();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case INSTAGRAM:
                    Instagram instagramClient = SocialApiClients.getInstagram();
                    int userId = Integer.valueOf(authorization.getIdentifier());
                    try {
                        instagramClient.setUserRelationship(userId, Relationship.FOLLOW);
                    } catch (InstagramException e) {
                        e.printStackTrace();
                    }
                    break;
                case LINKEDIN:
                    LinkedInApiClient linkedInApiClient = SocialApiClients.getLinkedIn(this);
                    Person person = linkedInApiClient.getProfileForCurrentUser();
                    String authHeader = "";
                    for (HttpHeader header : person.getApiStandardProfileRequest().getHeaders().getHttpHeaderList()) {
                        if (ApplicationConstants.AUTH_HEADER_NAME.equals(header.getName())) {
                            authHeader = header.getName();
                        }
                    }
                    linkedInApiClient.sendInviteById(authorization.getIdentifier(),
                            "Invitation to connect", "Please add me to your network. Invitation sent from Connectd",
                            authHeader);
                    break;
            }
        }

    }

    @Override
    public void onUserLoaded(User user) {
        if (user != null) {
            sendInvitation(user);
        }
    }

    @Override
    public void onResultsLoaded(List<SearchResult> results) {
        // NOP
    }

    @Override
    public void onUserLoadFailed(String error) {
        // NOP
    }
}