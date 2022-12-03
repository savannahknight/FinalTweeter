package edu.byu.cs.tweeter.server.service;

import java.util.List;

import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.QueueFollowersRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.model.net.response.QueueFollowersResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.server.dao.AuthTokenDAO;
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

        daoFactory.getQueueService().addStatusToQueue(request);
//        List<String> followerAliases = daoFactory.getFollowDAO().getAllFollowers(request.getStatus().getUser());
//        daoFactory.getFeedDAO().addStatusToFeed(followerAliases, request.getStatus());

        return response;
    }
    public QueueFollowersResponse postStatusGetFollowers(PostStatusRequest request) {
        FollowersResponse followersResponse = null;
        FollowersRequest followersRequest = new FollowersRequest(request.getAuthToken(), request.getStatus().getUser().getAlias(), 500, null);
        QueueFollowersResponse queueFollowersResponse = null;
        int counter = 1;

        do {
            followersResponse = daoFactory.getFollowDAO().getFollowers(followersRequest);
            System.out.println("numFollowers" + followersResponse.getFollowers().size());
            QueueFollowersRequest queueFollowersRequest = new QueueFollowersRequest(request.getAuthToken(), followersResponse.getFollowers(), request.getStatus());
            queueFollowersResponse = daoFactory.getQueueService().addFollowersToQueue(queueFollowersRequest);

            String lastItem = (followersResponse.getFollowers().size() > 0) ?
                    followersResponse.getFollowers().get(followersResponse.getFollowers().size() - 1).getAlias() : null;

            followersRequest.setLastItem(lastItem);
            System.out.println("Sent the " + counter + " batch to be processed in the UpdateFeedSQSFeed.");

            counter++;

        } while (followersResponse.getHasMorePages());

        return queueFollowersResponse;
    }

    public QueueFollowersResponse postStatusUpdateFeeds(QueueFollowersRequest request) {
        daoFactory.getFeedDAO().addFeedBatch(request.getFollowers(), request.getStatus());
        return new QueueFollowersResponse(true);
    }
    public StatusDAO getStatusDAO() {
        return daoFactory.getStatusDAO();
    }
    public AuthTokenDAO getAuthTokenDAO() {return daoFactory.getAuthTokenDAO();}
}
