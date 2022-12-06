package edu.byu.cs.tweeter.client.presenter;

import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.observer.NotificationObserver;
import edu.byu.cs.tweeter.client.model.service.observer.ResponseObserver;
import edu.byu.cs.tweeter.client.model.service.observer.ServiceObserver;
import edu.byu.cs.tweeter.client.presenter.template.BaseView;
import edu.byu.cs.tweeter.client.presenter.template.MainView;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class MainActivityPresenter extends BasePresenter<MainView>{
    private FollowService followService;
    private UserService userService;
    private StatusService statusService;

    public MainActivityPresenter(MainView view) {
        super(view);
        this.followService = new FollowService();
        this.userService = new UserService();
        //this.statusService = getStatusService();
    }

    protected StatusService getStatusService() {
        if(statusService == null) {
            statusService = new StatusService();
        }
       return statusService;
    }
    public void unfollow(User selectedUser) {
        view.displayInfoMessage("Removing " + selectedUser.getName() + "...");
        followService.unfollow(Cache.getInstance().getCurrUserAuthToken(), selectedUser, new UnfollowObserver());
    }

    public void follow(User selectedUser) {
        view.displayInfoMessage("Adding " + selectedUser.getName() + "...");
        followService.follow(Cache.getInstance().getCurrUserAuthToken(), selectedUser, new FollowObserver());
    }

    public void isFollower(User selectedUser) {
        followService.isFollower(Cache.getInstance().getCurrUserAuthToken(), selectedUser, Cache.getInstance().getCurrUser(), new IsFollowerObserver());
    }

    public void logout() {
        view.displayInfoMessage("Logging Out...");
        userService.logout(Cache.getInstance().getCurrUserAuthToken(), new LogoutObserver());
    }

    public void postStatus(Status newStatus) {
        view.displayInfoMessage("Posting Status...");
        getStatusService().postStatus(Cache.getInstance().getCurrUserAuthToken(), newStatus, new PostStatusObserver());

    }

    public void getFollowersCount(User selectedUser) {
        followService.getFollowersCount(Cache.getInstance().getCurrUserAuthToken(), selectedUser, new GetFollowersCountObserver());
    }

    public void getFollowingCount(User selectedUser) {
        followService.getFollowingCount(Cache.getInstance().getCurrUserAuthToken(), selectedUser, new GetFollowingCountObserver());
    }

    public User getCurrUser() {
        return Cache.getInstance().getCurrUser();
    }

    public void clearCache() {
        Cache.getInstance().clearCache();
    }

    public String getFormattedDateTime() throws ParseException {
        SimpleDateFormat userFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat statusFormat = new SimpleDateFormat("MMM d yyyy h:mm aaa");

        return statusFormat.format(userFormat.parse(LocalDate.now().toString() + " " + LocalTime.now().toString().substring(0, 8)));
    }
    public List<String> parseURLs(String post) {
        List<String> containedUrls = new ArrayList<>();
        for (String word : post.split("\\s")) {
            if (word.startsWith("http://") || word.startsWith("https://")) {

                int index = findUrlEndIndex(word);

                word = word.substring(0, index);

                containedUrls.add(word);
            }
        }

        return containedUrls;
    }

    public List<String> parseMentions(String post) {
        List<String> containedMentions = new ArrayList<>();

        for (String word : post.split("\\s")) {
            if (word.startsWith("@")) {
                word = word.replaceAll("[^a-zA-Z0-9]", "");
                word = "@".concat(word);

                containedMentions.add(word);
            }
        }

        return containedMentions;
    }

    public int findUrlEndIndex(String word) {
        if (word.contains(".com")) {
            int index = word.indexOf(".com");
            index += 4;
            return index;
        } else if (word.contains(".org")) {
            int index = word.indexOf(".org");
            index += 4;
            return index;
        } else if (word.contains(".edu")) {
            int index = word.indexOf(".edu");
            index += 4;
            return index;
        } else if (word.contains(".net")) {
            int index = word.indexOf(".net");
            index += 4;
            return index;
        } else if (word.contains(".mil")) {
            int index = word.indexOf(".mil");
            index += 4;
            return index;
        } else {
            return word.length();
        }
    }

    public class BaseMainObserver implements ServiceObserver {
        String description;

        public BaseMainObserver(String description) {
            this.description = description;
        }

        @Override
        public void handleFailure(String message) {
            view.displayErrorMessage(description + ": " + message);
            view.clearInfoMessage();
        }

        @Override
        public void handleException(Exception exception) {
            view.displayErrorMessage(description + " because of exception: " + exception.getMessage());
            view.clearInfoMessage();
        }
    }

    public class UnfollowObserver extends BaseMainObserver implements NotificationObserver {

        public UnfollowObserver() {
            super("Failed to unfollow user ");
        }

        @Override
        public void handleSuccess() {
            view.handleUnFollowSuccess();
            view.resetFollowButton();
        }
    }

    public class FollowObserver extends BaseMainObserver implements NotificationObserver {

        public FollowObserver() {
            super("Failed to follow user ");
        }

        @Override
        public void handleSuccess() {
            view.handleFollowSuccess();
            view.resetFollowButton();
        }
    }

    public class IsFollowerObserver extends BaseMainObserver implements ResponseObserver<Boolean> {

        public IsFollowerObserver() {
            super("Failed to check if user is follower ");
        }

        @Override
        public void handleSuccess(Boolean isFollower) {
            view.handleIsFollowerSuccess(isFollower);
        }
    }

    public class LogoutObserver extends BaseMainObserver implements NotificationObserver {

        public LogoutObserver() {
            super("Failed to logout ");
        }

        @Override
        public void handleSuccess() {
            view.clearInfoMessage();
            view.handleLogoutSuccess();
        }
    }

    public class PostStatusObserver extends BaseMainObserver implements NotificationObserver {

        public PostStatusObserver() {
            super("Failed to post status");
        }

        @Override
        public void handleSuccess() {
            view.clearInfoMessage();
            view.displayInfoMessage("Successfully Posted!");
            view.handlePostStatusSuccess();
        }
    }

    public class GetFollowersCountObserver extends BaseMainObserver implements ResponseObserver<Integer> {

        public GetFollowersCountObserver() {
            super("Failed to get followers ");
        }

        @Override
        public void handleSuccess(Integer response) {
            view.handleGetFollowersCountSuccess(response);
        }
    }

    public class GetFollowingCountObserver extends BaseMainObserver implements ResponseObserver<Integer> {

        public GetFollowingCountObserver() {
            super("Failed to get following ");
        }

        @Override
        public void handleSuccess(Integer response) {
            view.handleGetFollowingCountSuccess(response);
        }
    }
}
