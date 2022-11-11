package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.AuthenticationResponse;
import edu.byu.cs.tweeter.server.dao.dynamodb.DynamoDAOFactory;
import edu.byu.cs.tweeter.server.service.UserService;

public class RegisterHandler implements RequestHandler<RegisterRequest, AuthenticationResponse> {
    @Override
    public AuthenticationResponse handleRequest(RegisterRequest registerRequest, Context context) {
        UserService userService = new UserService(new DynamoDAOFactory());
        return userService.register(registerRequest);
    }
}
