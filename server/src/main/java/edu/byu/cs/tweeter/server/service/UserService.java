package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.AuthenticationResponse;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.server.dao.UserDAO;
import edu.byu.cs.tweeter.util.FakeData;

public class UserService {

    public AuthenticationResponse login(LoginRequest request) {
        if(request.getUsername() == null){
            throw new RuntimeException("[BadRequest] Missing a username");
        } else if(request.getPassword() == null) {
            throw new RuntimeException("[BadRequest] Missing a password");
        }

        return getUserDao().login(request);
    }
    public AuthenticationResponse register(RegisterRequest request) {
        if(request.getUsername() == null){
            throw new RuntimeException("[BadRequest] Missing a username");
        } else if(request.getPassword() == null) {
            throw new RuntimeException("[BadRequest] Missing a password");
        } else if(request.getFirstName() == null) {
            throw new RuntimeException("[BadRequest] Missing a first name");
        }  else if(request.getLastName() == null) {
            throw new RuntimeException("[BadRequest] Missing a last name");
        }  else if(request.getImage() == null) {
            throw new RuntimeException("[BadRequest] Missing a profile picture");
        }

        return getUserDao().register(request);
    }

    public GetUserResponse getUser(GetUserRequest request) {
        if(request.getUserAlias() == null){
            throw new RuntimeException("[BadRequest] Missing a user alias");
        }
        return getUserDao().getUser(request);
    }

    public LogoutResponse logout(LogoutRequest request) {
        if(request.getAuthToken() == null){
            throw new RuntimeException("[BadRequest] Missing an auth token");
        }
        return getUserDao().logout(request);
    }

    private UserDAO getUserDao() {
        return new UserDAO();
    }

}
