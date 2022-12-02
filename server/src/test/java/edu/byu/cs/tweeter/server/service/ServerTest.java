package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.QueueFollowersResponse;
import edu.byu.cs.tweeter.server.dao.AuthTokenDAO;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.dao.FeedDAO;
import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.dao.ImageDAO;
import edu.byu.cs.tweeter.server.dao.QueueService;
import edu.byu.cs.tweeter.server.dao.StatusDAO;
import edu.byu.cs.tweeter.server.dao.UserDAO;
import edu.byu.cs.tweeter.server.dao.dynamodb.DynamoDAOFactory;

public class ServerTest {
    public static void main(String[] args) {
        DAOFactory daoFactory = new DynamoDAOFactory();
        StatusService statusService = new StatusService(daoFactory);
        QueueFollowersResponse queueFollowersResponse = statusService.postStatusGetFollowers(new PostStatusRequest(new AuthToken("superToken"), new Status("message", new User("miley", "cyrus", "@miley", ""), "", null, null)));
        System.out.println(queueFollowersResponse);
    }
}
