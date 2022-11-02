package edu.byu.cs.tweeter.client.model.service;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.BackgroundTaskUtils;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.FollowTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.UnfollowTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.NotificationHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.PagedHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.ResponseHandler;
import edu.byu.cs.tweeter.client.model.service.observer.NotificationObserver;
import edu.byu.cs.tweeter.client.model.service.observer.PagedObserver;
import edu.byu.cs.tweeter.client.model.service.observer.ResponseObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowService extends PagedService{

    public void getFollowing(AuthToken currUserAuthToken, User user, int pageSize, User lastFollowee, PagedObserver<User> getFollowingObserver) {
        GetFollowingTask getFollowingTask = new GetFollowingTask(currUserAuthToken,
                user, pageSize, lastFollowee, new PagedHandler<>(getFollowingObserver));
        BackgroundTaskUtils.runTask(getFollowingTask);
    }

    public void getFollowers(AuthToken currUserAuthToken, User user, int pageSize, User lastFollower, PagedObserver<User> getFollowersObserver) {
        GetFollowersTask getFollowersTask = new GetFollowersTask(currUserAuthToken,
                user, pageSize, lastFollower, new PagedHandler<>(getFollowersObserver));
        BackgroundTaskUtils.runTask(getFollowersTask);

    }

    public void follow(AuthToken currUserAuthToken, User selectedUser, NotificationObserver followObserver) {
        FollowTask followTask = new FollowTask(currUserAuthToken,
                selectedUser, new NotificationHandler(followObserver));
        BackgroundTaskUtils.runTask(followTask);
    }

    public void unfollow(AuthToken currUserAuthToken, User selectedUser, NotificationObserver unfollowObserver) {
        UnfollowTask unfollowTask = new UnfollowTask(currUserAuthToken,
                selectedUser, new NotificationHandler(unfollowObserver));
        BackgroundTaskUtils.runTask(unfollowTask);
    }

    public void isFollower(AuthToken currUserAuthToken, User selectedUser, User currUser, ResponseObserver<Boolean> isFollowerObserver) {
        IsFollowerTask isFollowerTask = new IsFollowerTask(currUserAuthToken,
                currUser, selectedUser, new ResponseHandler<>(isFollowerObserver));
        BackgroundTaskUtils.runTask(isFollowerTask);
    }

    public void getFollowersCount(AuthToken currUserAuthToken, User selectedUser, ResponseObserver<Integer> getFollowersCountObserver) {
        GetFollowersCountTask followersCountTask = new GetFollowersCountTask(currUserAuthToken,
                selectedUser, new ResponseHandler<>(getFollowersCountObserver));
        BackgroundTaskUtils.runTask(followersCountTask);
    }

    public void getFollowingCount(AuthToken currUserAuthToken, User selectedUser, ResponseObserver<Integer> getFollowingCountObserver) {
        GetFollowingCountTask followingCountTask = new GetFollowingCountTask(currUserAuthToken,
                selectedUser, new ResponseHandler<>(getFollowingCountObserver));
        BackgroundTaskUtils.runTask(followingCountTask);
    }
}
