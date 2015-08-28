package com.example.peter.connectd.models;

import com.example.peter.connectd.rest.SocialApiClients;

import java.util.HashSet;
import java.util.Set;

/**
 * Model object for a user - contains all Authentication data
 */
public class User {
    public static final String USERNAME_KEY = "username";
    public static final String EMAIL_KEY = "email";
    public static final String FIRST_NAME_KEY = "first_name";
    public static final String LAST_NAME_KEY = "last_name";
    public static final String PASSWORD_KEY = "password";
    public static final String PASSWORD_CONFIRMATION_KEY = "password_confirmation";
    public static final String LOGIN_KEY = "login";
    public static final String REMEMBER_ME_KEY = "remember_me";
    public static final String USER_KEY = "user";

    private int mId;
    private String mName;
    private String mTwitterUsername;
    private String mFacebookUsername;
    private String mInstagramId;
    private String mGPlusId;
    private String mLinkedInId;
    private Set<Authorization> mAuthorizations;
    private static User sCurrentUser;

    private User(int id,
                 String name,
                 String twitterUsername,
                 String facebookUsername,
                 String instagramId,
                 String gPlusId,
                 String linkedInId,
                 Set<Authorization> authorizations) {
        mId = id;
        mName = name;
        mTwitterUsername = twitterUsername;
        mFacebookUsername = facebookUsername;
        mInstagramId = instagramId;
        mGPlusId = gPlusId;
        mLinkedInId = linkedInId;
        mAuthorizations = authorizations;
    }

    public static class Builder {
        private int mId;
        private String mName;
        private String mTwitterUsername;
        private String mFacebookUsername;
        private String mInstagramId;
        private String mGPlusId;
        private String mLinkedInId;
        private Set<Authorization> mAuthorizations;

        public Builder(int id) {
            mAuthorizations = new HashSet<Authorization>();
            mId = id;
        }

        public Builder setName(String name) {
            mName = name;
            return this;
        }

        public Builder setFacebookUsername(String facebookUsername) {
            mFacebookUsername = facebookUsername;
            mAuthorizations.add(new Authorization(SocialApiClients.SocialMediaName.FACEBOOK, facebookUsername));
            return this;
        }

        public Builder setTwitterUsername(String twitterUsername) {
            mTwitterUsername = twitterUsername;
            mAuthorizations.add(new Authorization(SocialApiClients.SocialMediaName.TWITTER, twitterUsername));
            return this;
        }

        public Builder setInstagramId(String instagramId) {
            mInstagramId = instagramId;
            mAuthorizations.add(new Authorization(SocialApiClients.SocialMediaName.INSTAGRAM, instagramId));
            return this;
        }

        public Builder setGPlusId(String gPlusId) {
            mGPlusId = gPlusId;
            mAuthorizations.add(new Authorization(SocialApiClients.SocialMediaName.GPLUS, gPlusId));
            return this;
        }

        public Builder setLinkedInId(String linkedInId) {
            mLinkedInId = linkedInId;
            mAuthorizations.add(new Authorization(SocialApiClients.SocialMediaName.LINKEDIN, linkedInId));
            return this;
        }

        public User build() {
            return new User(mId, mName, mTwitterUsername, mFacebookUsername, mInstagramId,
                    mGPlusId, mLinkedInId, mAuthorizations);
        }
    }

    public static Builder createBuilder(int id) {
        return new Builder(id);
    }

    public static void setCurrentUser(User user) {
        sCurrentUser = user;
    }

    /**
     * Returns the user that is logged in. Will return null if not logged in.
     * @return the current user
     */
    public static User getCurrentUser() {
        return sCurrentUser;
    }

    /**
     * Returns whether the user is logged in.
     * @return the logged in state of the user.
     */
    public static boolean isLoggedIn() {
        return sCurrentUser == null;
    }

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public String getTwitterUsername() {
        return mTwitterUsername;
    }

    public String getFacebookUsername() {
        return mFacebookUsername;
    }

    public String getInstagramId() {
        return mInstagramId;
    }

    public String getgPlusId() {
        return mGPlusId;
    }

    public String getLinkedInId() {
        return mLinkedInId;
    }


    public void setLinkedInId(String linkedInId) {
        mLinkedInId = linkedInId;
    }

    public void setGPlusId(String gPlusId) {
        mGPlusId = gPlusId;
    }

    public void setInstagramId(String instagramId) {
        mInstagramId = instagramId;
    }

    public void setFacebookUsername(String facebookUsername) {
        mFacebookUsername = facebookUsername;
    }

    public void setTwitterUsername(String twitterUsername) {
        mTwitterUsername = twitterUsername;
    }

    public void setName(String name) {
        mName = name;
    }

    public Set<Authorization> getAuthorizations() {
        return mAuthorizations;
    }

    public static class Authorization {
        SocialApiClients.SocialMediaName mSocialMediaName;
        String mIdentifier;

        public Authorization(SocialApiClients.SocialMediaName socialMediaName, String identifier) {
            mSocialMediaName = socialMediaName;
            mIdentifier = identifier;
        }

        public SocialApiClients.SocialMediaName getSocialMediaName() {
            return mSocialMediaName;
        }

        public String getIdentifier() {
            return mIdentifier;
        }
    }
}
