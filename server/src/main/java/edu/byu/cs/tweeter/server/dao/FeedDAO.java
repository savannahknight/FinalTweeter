package edu.byu.cs.tweeter.server.dao;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;

public interface FeedDAO {
    FeedResponse getFeed(FeedRequest request);
    boolean addStatusToFeed(List<String> followerAliases, Status status);
}
