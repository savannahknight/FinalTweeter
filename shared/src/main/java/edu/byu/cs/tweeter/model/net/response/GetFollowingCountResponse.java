package edu.byu.cs.tweeter.model.net.response;

public class GetFollowingCountResponse extends Response {

    int count;

    public GetFollowingCountResponse(String message) {
        super(false, message);
    }

    public GetFollowingCountResponse(int count) {
        super(true);
        this.count = count;
    }

    public int getCount() {
        return count;
    }
}
