package edu.byu.cs.tweeter.server.dao.dynamodb;

import edu.byu.cs.tweeter.server.dao.AuthTokenDAO;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.dao.FeedDAO;
import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.dao.ImageDAO;
import edu.byu.cs.tweeter.server.dao.QueueService;
import edu.byu.cs.tweeter.server.dao.StatusDAO;
import edu.byu.cs.tweeter.server.dao.UserDAO;

public class DynamoDAOFactory implements DAOFactory {
    @Override
    public FollowDAO getFollowDAO() {
        return new FollowDAODynamo();
    }

    @Override
    public StatusDAO getStatusDAO() {
        return new StatusDAODynamo();
    }

    @Override
    public UserDAO getUserDAO() {
        return new UserDAODynamo();
    }

    @Override
    public FeedDAO getFeedDAO() {
        return new FeedDAODynamo();
    }

    @Override
    public AuthTokenDAO getAuthTokenDAO() {
        return new AuthTokenDAODynamo();
    }

    @Override
    public ImageDAO getImageDAO() {
        return new ImageDAOUsingS3();
    }
    @Override
    public QueueService getQueueService() {
        return new QueueServiceSQS();
    }
}
