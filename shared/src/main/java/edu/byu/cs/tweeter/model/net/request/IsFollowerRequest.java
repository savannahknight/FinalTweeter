package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class IsFollowerRequest {
    AuthToken authToken;
    /**
     * The alleged follower.
     */
    User follower;

    /**
     * The alleged followee.
     */
    User followee;

    public IsFollowerRequest() {}

    public IsFollowerRequest(AuthToken authToken, User follower, User followee) {
        this.authToken = authToken;
        this.follower = follower;
        this.followee = followee;


    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    public User getFollower() {
        return follower;
    }

    public User getFollowee() {
        return followee;
    }

    public void setFollower(User follower) {
        this.follower = follower;
    }

    public void setFollowee(User followee) {
        this.followee = followee;
    }


    @Override
    public String toString() {
        return "{\nfollowee: " + followee.toString() + ",\n" + "follower: " + follower.toString()
                + ",\n" + "AuthToken: " + authToken.toString();
    }
}
