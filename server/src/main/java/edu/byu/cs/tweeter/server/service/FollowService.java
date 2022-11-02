package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.net.request.FollowToggleRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.response.FollowToggleResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowersCountResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.server.dao.FollowDAO;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class FollowService {

    /**
     * Returns the users that the user specified in the request is following. Uses information in
     * the request object to limit the number of followees returned and to return the next set of
     * followees after any that were returned in a previous request. Uses the {@link FollowDAO} to
     * get the followees.
     *
     * @param request contains the data required to fulfill the request.
     * @return the followees.
     */
    public FollowingResponse getFollowees(FollowingRequest request) {
        if(request.getUserAlias() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a follower alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[BadRequest] Request needs to have a positive limit");
        }
        return getFollowDAO().getFollowees(request);
    }

    public FollowersResponse getFollowers(FollowersRequest request) {
        if(request.getUserAlias() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a follower alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[BadRequest] Request needs to have a positive limit");
        }
        return getFollowDAO().getFollowers(request);
    }

    public FollowToggleResponse follow(FollowToggleRequest request) {
        if(request.getFollowee() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a followee");
        }
        return getFollowDAO().follow();
    }

    public FollowToggleResponse unfollow(FollowToggleRequest request) {
        if(request.getFollowee() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a followee");
        }
        return getFollowDAO().unfollow();
    }

    public GetFollowersCountResponse getFollowersCount(GetFollowersCountRequest request) {
        if(request.getTargetUser() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a user alias");
        }
        return getFollowDAO().getFollowersCount();
    }

    public GetFollowingCountResponse getFollowingCount(GetFollowingCountRequest request) {
        if(request.getTargetUser() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a user alias");
        }
        return getFollowDAO().getFollowingCount();
    }

    public IsFollowerResponse isFollower(IsFollowerRequest request) {
        if(request.getFollowee() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a followee");
        }
        if(request.getFollower() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a follower");
        }
        return getFollowDAO().isFollower();
    }

    /**
     * Returns an instance of {@link FollowDAO}. Allows mocking of the FollowDAO class
     * for testing purposes. All usages of FollowDAO should get their FollowDAO
     * instance from this method to allow for mocking of the instance.
     *
     * @return the instance.
     */
    FollowDAO getFollowDAO() {
        return new FollowDAO();
    }
}
