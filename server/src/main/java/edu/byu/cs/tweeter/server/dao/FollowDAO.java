package edu.byu.cs.tweeter.server.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowToggleRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.response.FollowToggleResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowersCountResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.util.FakeData;

/**
 * A DAO for accessing 'following' data from the database.
 */
public interface FollowDAO {
    FollowingResponse getFollowees(FollowingRequest request);
    FollowersResponse getFollowers(FollowersRequest request);
    FollowToggleResponse follow(FollowToggleRequest request, User currUser);
    FollowToggleResponse unfollow(FollowToggleRequest request, User currUser);
    IsFollowerResponse isFollower(IsFollowerRequest request);
    List<String> getAllFollowers(User user);
    void addFollowersBatch(List<User> users, String followTarget);
}
