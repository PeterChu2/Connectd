package com.example.peter.connectd;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by peter on 22/05/15.
 */
public class User {
    private String mName;
    private String mTwitterUsername;
    private String mFacebookUsername;
    private String mInstagramId;
    private String mGPlusId;
    private String mLinkedInId;
    private Set<Authorization> mAuthorizations;

    private User(String name, String twitterUsername, String facebookUsername, String instagramId,
                 String gPlusId, String linkedInId, Set<Authorization> authorizations) {
        mName = name;
        mTwitterUsername = twitterUsername;
        mFacebookUsername = facebookUsername;
        mInstagramId = instagramId;
        mGPlusId = gPlusId;
        mLinkedInId = linkedInId;
        mAuthorizations = authorizations;
    }

    public static class Builder {
        private String mName;
        private String mTwitterUsername;
        private String mFacebookUsername;
        private String mInstagramId;
        private String mGPlusId;
        private String mLinkedInId;
        private Set<Authorization> mAuthorizations;

        public Builder() {
            mAuthorizations = new HashSet<Authorization>();
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
            return this;
        }

        public Builder setInstagramId(String instagramId) {
            mInstagramId = instagramId;
            return this;
        }

        public Builder setGPlusId(String gPlusId) {
            mGPlusId = gPlusId;
            return this;
        }

        public Builder setLinkedInId(String linkedInId) {
            mLinkedInId = linkedInId;
            return this;
        }

        public User build() {
            return new User(mName, mTwitterUsername, mFacebookUsername, mInstagramId,
                    mGPlusId, mLinkedInId, mAuthorizations);
        }
    }

    public static Builder createBuilder() {
        return new Builder();
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
