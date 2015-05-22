package com.example.peter.connectd;

import android.app.Activity;
import android.os.Bundle;

import com.google.api.services.plusDomains.PlusDomains;
import com.google.api.services.plusDomains.model.Circle;
import com.google.api.services.plusDomains.model.CircleFeed;
import com.google.code.linkedinapi.client.LinkedInApiClient;
import com.google.code.linkedinapi.client.constant.ApplicationConstants;
import com.google.code.linkedinapi.schema.HttpHeader;
import com.google.code.linkedinapi.schema.Person;

import org.jinstagram.Instagram;
import org.jinstagram.model.Relationship;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Created by peter on 22/05/15.
 */
public class AuthenticatedHomeActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authenticated_home);
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
                    PlusDomains plusDomainsClient = SocialApiClients.getGPlus();
                    List<String> addUserIds = new ArrayList<String>();
                    addUserIds.add(authorization.getIdentifier());
                    try {
                        PlusDomains.Circles.List listCircles = plusDomainsClient.circles().list("me");
                        listCircles.setMaxResults(1L);
                        CircleFeed circleFeed = listCircles.execute();
                        List<Circle> circles = circleFeed.getItems();
                        for(circle : circles) {
                            PlusDomains.Circles.AddPeople addPeople = plusDomainsClient.circles().addPeople(listCircles.getUserId());
                            addPeople.setUserId(addUserIds);
                            addPeople.execute();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case INSTAGRAM:
                        Instagram instagramClient = SocialApiClients.getInstagram();
                        instagramClient.setUserRelationship(authorization.getIdentifier(), Relationship.FOLLOW);
                    break;
                case LINKEDIN:
                    LinkedInApiClient linkedInApiClient = SocialApiClients.getLinkedIn();
                    Person person = linkedInApiClient.getProfileForCurrentUser();
                    String authHeader = "";
                    for(HttpHeader header : person.getApiStandardProfileRequest().getHeaders().getHttpHeaderList()) {
                        if(ApplicationConstants.AUTH_HEADER_NAME.equals(header.getName())) {
                            authHeader = header.getName();
                        }
                    }
                    linkedInApiClient.sendInviteById(authorization.getIdentifier(),
                            "Invitation to connect", "Please add me to your network. Invitation sent from Connectd",
                            );
                    break;
            }
        }

    }

}
