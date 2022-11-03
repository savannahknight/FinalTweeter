package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public class GetUserRequest {
    AuthToken authToken;
    String userAlias;

    public GetUserRequest() {
    }


    public GetUserRequest(AuthToken authToken, String targetUser) {
        this.authToken = authToken;
        this.userAlias = targetUser;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    public String getUserAlias() {
        return userAlias;
    }

    public void setUserAlias(String userAlias) {
        this.userAlias = userAlias;
    }
}
