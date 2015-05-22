package com.example.peter.connectd;

import android.app.Activity;
import android.os.Bundle;

import com.google.api.services.plusDomains.PlusDomains;

import org.jinstagram.Instagram;
import org.jinstagram.model.Relationship;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import twitter4j.Twitter;

/**
 * Created by peter on 22/05/15.
 */
public class AuthenticatedHomeActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authenticated_home);
    }

    private void sendInvitation() {
        Twitter twitterClient = SocialApiClients.getTwitter();
        twitterClient.createFriendship(USER_ID);
        Instagram instagramClient = SocialApiClients.getInstagram();
        instagramClient.setUserRelationship(USER_ID, Relationship.FOLLOW);

        PlusDomains plusDomainsClient = SocialApiClients.getGPlus();=
        List<String> addUserIds = new ArrayList<String>();
        addUserIds.add(userId);
        PlusDomains.Circles.AddPeople addPeople = plusDomainsClient.circles().addPeople(circleId);
        addPeople.setUserId(addUserIds);
        try {
            addPeople.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
