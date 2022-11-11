package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;

public interface AuthTokenDAO {
    AuthToken generateAuthToken(User user);
    LogoutResponse logout(LogoutRequest request, boolean timedOut);
    String getCurrUserAlias(AuthToken authToken);
    boolean authenticateCurrentUser(AuthToken authToken);
}
