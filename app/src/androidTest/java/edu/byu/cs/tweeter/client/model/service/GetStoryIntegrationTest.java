package edu.byu.cs.tweeter.client.model.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import edu.byu.cs.tweeter.client.model.service.observer.PagedObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.util.FakeData;


public class GetStoryIntegrationTest {
    private User currentUser;
    private AuthToken currentAuthToken;

    private StatusService statusServiceSpy;
    private StatusServiceObserver observer;

    private CountDownLatch countDownLatch;

    /**
     * Create a StatusService spy that uses a mock ServerFacade to return known responses to
     * requests.
     */
    @BeforeEach
    public void setup() {
        currentUser = new User("FirstName", "LastName", null);
        currentAuthToken = new AuthToken();

        statusServiceSpy = Mockito.spy(new StatusService());

        // Setup an observer for the StatusService
        observer = new StatusServiceObserver();

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
    private class StatusServiceObserver implements PagedObserver<Status> {

        private boolean success;
        private String message;
        private List<Status> storyItems;
        private boolean hasMorePages;
        private Exception exception;

        @Override
        public void handleSuccess(List<Status> feedItems, boolean hasMorePages) {
            this.success = true;
            this.message = null;
            this.storyItems = feedItems;
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

        public List<Status> getStoryItems() {
            return storyItems;
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

    /**
     * Verify that for successful requests, the StatusService loadMoreStoryItems()
     * asynchronous method eventually returns the same result as the ServerFacade.
     */
    @Test
    public void testLoadMoreStoryItems_validRequest_correctResponse() throws InterruptedException {
        statusServiceSpy.loadMoreStoryItems(currentAuthToken, currentUser, 3, null, observer);
        awaitCountDownLatch();

        List<Status> expectedStoryItems = FakeData.getInstance().getFakeStatuses().subList(0, 3);
        Assertions.assertTrue(observer.isSuccess());
        Assertions.assertNull(observer.getMessage());
        Assertions.assertTrue(observer.getStoryItems().get(0).getUser().equals(expectedStoryItems.get(0).getUser()));
        Assertions.assertTrue(observer.getHasMorePages());
        Assertions.assertNull(observer.getException());
    }
}
