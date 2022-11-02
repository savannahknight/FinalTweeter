package edu.byu.cs.tweeter.model.net.response;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;

public class GetFollowersCountResponse extends Response {

    int count;

    public GetFollowersCountResponse(String message) {
        super(false, message);
    }

    public GetFollowersCountResponse(int count) {
        super(true);
        this.count = count;
    }

    public int getCount() {
        return count;
    }
}
