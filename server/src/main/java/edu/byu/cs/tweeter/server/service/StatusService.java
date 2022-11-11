package edu.byu.cs.tweeter.server.service;

import java.util.List;

import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.dao.StatusDAO;

public class StatusService {
    DAOFactory daoFactory;

    public StatusService(DAOFactory daoFactory) {
        this.daoFactory = daoFactory;
    }


    public StoryResponse getStory(StoryRequest request) {
        if(request.getUserAlias() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a user alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[BadRequest] Request needs to have a positive limit");
        }
        if (!daoFactory.getAuthTokenDAO().authenticateCurrentUser(request.getAuthToken())) {
            throw new RuntimeException("[BadRequest] The current user session is no longer valid. PLease logout and login again.");
        }

        return getStatusDAO().getStory(request);
    }

    public FeedResponse getFeed(FeedRequest request) {
        if(request.getUserAlias() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a user alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[BadRequest] Request needs to have a positive limit");
        }
        if (!daoFactory.getAuthTokenDAO().authenticateCurrentUser(request.getAuthToken())) {
            throw new RuntimeException("[BadRequest] The current user session is no longer valid. PLease logout and login again.");
        }

        return daoFactory.getFeedDAO().getFeed(request);
    }

    public PostStatusResponse postStatus(PostStatusRequest request) {
        if(request.getStatus() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a status");
        }
        if (!daoFactory.getAuthTokenDAO().authenticateCurrentUser(request.getAuthToken())) {
            throw new RuntimeException("[BadRequest] The current user session is no longer valid. PLease logout and login again.");
        }
        PostStatusResponse response = getStatusDAO().postStatus(request);

        List<String> followerAliases = daoFactory.getFollowDAO().getAllFollowers(request.getStatus().getUser());
        daoFactory.getFeedDAO().addStatusToFeed(followerAliases, request.getStatus());

        return response;
    }

    private StatusDAO getStatusDAO() {
        return daoFactory.getStatusDAO();
    }
}
