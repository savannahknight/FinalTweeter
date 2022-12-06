package edu.byu.cs.tweeter.client.model.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.observer.PagedObserver;
import edu.byu.cs.tweeter.client.model.service.observer.ResponseObserver;
import edu.byu.cs.tweeter.client.presenter.AuthenticationPresenter;
import edu.byu.cs.tweeter.client.presenter.MainActivityPresenter;
import edu.byu.cs.tweeter.client.presenter.PagedPresenter;
import edu.byu.cs.tweeter.client.presenter.template.MainView;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;


public class PostStatusIntegrationTest {
    private User currentUser;
    private AuthToken currentAuthToken;
    private Status lastStatus;

    private StatusService statusServiceSpy;
    private UserService userServiceSpy;
    private GetItemsObserver observer;
    private AuthenticationObserver loginObserverSpy;

    private MainActivityPresenter mainPresenterSpy;
    private PostStatusView postStatusViewSpy;

    private CountDownLatch countDownLatch;

    /**
     * Create a StatusService spy that uses a mock ServerFacade to return known responses to
     * requests.
     */
    @BeforeEach
    public void setup() {
        currentUser = new User("doja", "cat", "@doja", "https://cs340-savannah.s3.amazonaws.com/%40doja");
        currentAuthToken = null;

        lastStatus = new Status("post", currentUser, LocalDateTime.now().toString(), new ArrayList<>(), new ArrayList<>());

        statusServiceSpy = Mockito.spy(new StatusService());
        userServiceSpy = Mockito.spy(new UserService());
        postStatusViewSpy = Mockito.spy(new PostStatusView());
        mainPresenterSpy = Mockito.spy(new MainActivityPresenter(postStatusViewSpy));

        // Setup an observer for the StatusService
        observer = new GetItemsObserver();
        loginObserverSpy = Mockito.spy(new AuthenticationObserver());

        // Prepare the countdown latch
        resetCountDownLatch();
    }

    private void resetCountDownLatch() {
        countDownLatch = new CountDownLatch(1);
    }

    private void awaitCountDownLatch() throws InterruptedException {
        countDownLatch.await();
        resetCountDownLatch();
    }

    /**
     * A StatusServiceObserver implementation that can be used to get the values
     * eventually returned by an asynchronous call on the StatusService. Counts down
     * on the countDownLatch so tests can wait for the background thread to call a method on the
     * observer.
     */
    private class GetItemsObserver implements PagedObserver<Status> {

        private boolean success;
        private String message;
        private List<Status> statuses;
        private boolean hasMorePages;
        private Exception exception;

        @Override
        public void handleSuccess(List<Status> items, boolean hasMorePages) {
            this.success = true;
            this.message = null;
            this.statuses = items;
            this.hasMorePages = hasMorePages;
            this.exception = null;

            countDownLatch.countDown();
        }
        @Override
        public void handleFailure(String message) {
            this.success = false;
            this.message = message;
            this.hasMorePages = false;
            this.exception = null;

            countDownLatch.countDown();
        }

        @Override
        public void handleException(Exception exception) {
            this.success = false;
            this.message = null;
            this.hasMorePages = false;
            this.exception = exception;

            countDownLatch.countDown();
        }


        public boolean isSuccess() {
            return success;
        }

        public List<Status> getStatuses() {
            return statuses;
        }

        public String getMessage() {
            return message;
        }

        public boolean getHasMorePages() {
            return hasMorePages;
        }

        public Exception getException() {
            return exception;
        }

    }
    public class AuthenticationObserver implements ResponseObserver<User> {
        private boolean success;
        private String message;
        private Exception exception;

        @Override
        public void handleSuccess(User user) {
            this.success = true;
            this.message = null;
            this.exception = null;
            countDownLatch.countDown();

        }

        @Override
        public void handleFailure(String message) {
            this.success = false;
            this.message = message;
            this.exception = null;
            countDownLatch.countDown();
        }

        @Override
        public void handleException(Exception exception) {
            this.success = false;
            this.message = null;
            this.exception = exception;

            countDownLatch.countDown();
        }

        public boolean isSuccess() {
            return success;
        }
    }

    public class PostStatusView implements MainView {
        @Override
        public void displayErrorMessage(String message) {
            countDownLatch.countDown();
        }

        @Override
        public void handleFollowSuccess() {

        }

        @Override
        public void handleUnFollowSuccess() {

        }

        @Override
        public void handleLogoutSuccess() {

        }

        @Override
        public void handleGetFollowersCountSuccess(int count) {

        }

        @Override
        public void handleGetFollowingCountSuccess(int count) {

        }

        @Override
        public void handleIsFollowerSuccess(boolean isFollower) {

        }

        @Override
        public void handlePostStatusSuccess() {
            countDownLatch.countDown();
            System.out.println("Successfully Posted!");
        }

        @Override
        public void resetFollowButton() {

        }

        @Override
        public void displayInfoMessage(String message) {

        }

        @Override
        public void clearInfoMessage() {

        }
    }

    /**
     * Verify that for successful requests, the StatusService loadMoreStoryItems()
     * asynchronous method eventually returns the same result as the ServerFacade.
     */
    @Test
    public void testPostStatus_validRequest_correctResponse() throws InterruptedException {
        userServiceSpy.login("@doja", "cat", loginObserverSpy);
        awaitCountDownLatch();
        Mockito.verify(loginObserverSpy).handleSuccess(Mockito.any());

        currentAuthToken = Cache.getInstance().getCurrUserAuthToken();

        mainPresenterSpy.postStatus(lastStatus);
        awaitCountDownLatch();

        Mockito.verify(postStatusViewSpy).displayInfoMessage("Successfully Posted!");

        statusServiceSpy.loadMoreStoryItems(currentAuthToken, currentUser, 10, null, observer);
        awaitCountDownLatch();



        Assertions.assertTrue(observer.isSuccess());
        Assertions.assertNull(observer.getMessage());
        Assertions.assertTrue(lastStatus.equalsNoTime(observer.getStatuses().get(observer.getStatuses().size() - 1)));
        //Assertions.assertTrue(observer.getStoryItems().get(0).getUser().equals(expectedStoryItems.get(0).getUser()));
        Assertions.assertFalse(observer.getHasMorePages());
        Assertions.assertNull(observer.getException());
    }
}
