package edu.byu.cs.tweeter.model.net.response;

public class FollowToggleResponse extends Response {


    public FollowToggleResponse(boolean success) {
        super(success);
    }

    public FollowToggleResponse(boolean success, String message) {
        super(success, message);
    }
}
