package edu.byu.cs.tweeter.model.net.response;

public class QueueFollowersResponse extends Response{
    public QueueFollowersResponse(boolean success) {
        super(success);
    }

    public QueueFollowersResponse(boolean success, String message) {
        super(success, message);
    }
}
