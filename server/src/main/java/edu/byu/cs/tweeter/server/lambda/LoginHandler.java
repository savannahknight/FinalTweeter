package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.response.AuthenticationResponse;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.server.dao.dynamodb.DynamoDAOFactory;
import edu.byu.cs.tweeter.server.service.UserService;

/**
 * An AWS lambda function that logs a user in and returns the user object and an auth code for
 * a successful login.
 */
public class LoginHandler implements RequestHandler<LoginRequest, AuthenticationResponse> {
    @Override
    public AuthenticationResponse handleRequest(LoginRequest loginRequest, Context context) {
        UserService userService = new UserService(new DynamoDAOFactory());
        return userService.login(loginRequest);
    }
}
