package edu.byu.cs.tweeter.model.net.response;

public class IsFollowerResponse extends Response {

    boolean isFollower;

    public IsFollowerResponse(String message) {
        super(false, message);
    }

    public IsFollowerResponse(boolean isFollower) {
        super(true);
        this.isFollower = isFollower;
    }

    public boolean getIsFollower() {
        return isFollower;
    }
}
