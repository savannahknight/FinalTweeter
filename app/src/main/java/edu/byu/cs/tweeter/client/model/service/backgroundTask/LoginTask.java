package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;
import android.util.Log;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.response.AuthenticationResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that logs in a user (i.e., starts a session).
 */
public class LoginTask extends AuthenticateTask {
    private static final String URL_PATH = "/login";
    private static final String LOG_TAG = "LoginTask";


    public LoginTask(String username, String password, Handler messageHandler) {
        super(messageHandler, username, password);
    }

    @Override
    protected Pair<User, AuthToken> runAuthenticationTask() {
        User loggedInUser = null;
        AuthToken authToken = null;
        try {
            LoginRequest request = new LoginRequest(username, password);
            AuthenticationResponse response = getServerFacade().login(request, URL_PATH);

            if(response.isSuccess()) {
                loggedInUser = response.getUser();
                authToken = response.getAuthToken();
                return new Pair<>(loggedInUser, authToken);
            }
            else {
                sendFailedMessage(response.getMessage());
            }
        } catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage(), ex);
            sendExceptionMessage(ex);
        }
        return new Pair<>(loggedInUser, authToken);
    }
}
