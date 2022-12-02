package edu.byu.cs.tweeter.server.dao;

public interface DAOFactory {
    FollowDAO getFollowDAO();
    StatusDAO getStatusDAO();
    UserDAO getUserDAO();
    FeedDAO getFeedDAO();
    AuthTokenDAO getAuthTokenDAO();
    ImageDAO getImageDAO();
    QueueService getQueueService();
}
