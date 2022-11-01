package edu.byu.cs.tweeter.client.presenter.template;

public interface MainView extends BaseView{
    void handleFollowSuccess();
    void handleUnFollowSuccess();
    void handleLogoutSuccess();
    void handleGetFollowersCountSuccess(int count);
    void handleGetFollowingCountSuccess(int count);
    void handleIsFollowerSuccess(boolean isFollower);
    void handlePostStatusSuccess();
    void resetFollowButton();
    void displayInfoMessage(String message);
    void clearInfoMessage();
}
