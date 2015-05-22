package com.example.peter.connectd;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.TextView;

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
import java.util.Iterator;
import java.util.List;

import twitter4j.Twitter;
import twitter4j.TwitterException;

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

    @Override
    protected void onResume(){
        super.onResume();
        Intent intent = getIntent();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Parcelable[] rawMessages = intent.getParcelableArrayExtra(
                    NfcAdapter.EXTRA_NDEF_MESSAGES);

            NdefMessage message = (NdefMessage) rawMessages[0]; // only one message transferred

            int friendId = Integer.valueOf(new String(message.getRecords()[0].getPayload()));
            User friend = User.findById(friendId);
            if(friend != null) {
                sendInvitation(friend);
            }
            mTextView.setText(String.format("Received User data from friend with user ID: %i", friendId));
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
                        Iterator<Circle> itr = circles.iterator();
                        while(itr.hasNext()) {
                            Circle circle = itr.next();
                            PlusDomains.Circles.AddPeople addPeople = plusDomainsClient.circles().addPeople(circle.getId());
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
                    for(HttpHeader header : person.getApiStandardProfileRequest().getHeaders().getHttpHeaderList()) {
                        if(ApplicationConstants.AUTH_HEADER_NAME.equals(header.getName())) {
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
}