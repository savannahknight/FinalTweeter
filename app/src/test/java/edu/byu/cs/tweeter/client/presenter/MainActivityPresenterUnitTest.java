package edu.byu.cs.tweeter.client.presenter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.observer.NotificationObserver;
import edu.byu.cs.tweeter.client.presenter.template.MainView;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;

public class MainActivityPresenterUnitTest {
    private MainView mockView;
    private Cache mockCache;
    private StatusService mockStatusService;
    private Status mockStatus;
    private AuthToken mockAuthToken;
    private MainActivityPresenter mainPresenterSpy;

    @BeforeEach
    public void setup() {
        // Create mocks
        mockView = Mockito.mock(MainView.class);
        mockCache = Mockito.mock(Cache.class);
        mockStatusService = Mockito.mock(StatusService.class);
        mockStatus = Mockito.mock(Status.class);
        mockAuthToken = Mockito.mock(AuthToken.class);

        Cache.setInstance(mockCache);

        mainPresenterSpy = Mockito.spy(new MainActivityPresenter(mockView));
        Mockito.when(mainPresenterSpy.getStatusService()).thenReturn(mockStatusService);
        Mockito.when(mockCache.getCurrUserAuthToken()).thenReturn(mockAuthToken);
    }

    @Test
    public void testPostStatus_PostStatusSuccessful() {
        Answer<Void> answer = new Answer<>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                NotificationObserver observer = invocation.getArgument(2, NotificationObserver.class);
                observer.handleSuccess();
                return null;
            }
        };

        verifyServiceCall(answer);
        Mockito.verify(mockView).handlePostStatusSuccess();
        Mockito.verify(mockView).clearInfoMessage();
    }

    @Test
    public void testPostStatus_PostStatusFailed() {
        Answer<Void> answer = new Answer<>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                NotificationObserver observer = invocation.getArgument(2, NotificationObserver.class);
                observer.handleFailure("Error Message");
                return null;
            }
        };

        verifyServiceCall(answer);
        verifyErrorResult("Failed to post status: Error Message");
    }

    @Test
    public void testPostStatus_PostStatusFailedWithException() {
        Answer<Void> answer = new Answer<>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                NotificationObserver observer = invocation.getArgument(2, NotificationObserver.class);
                observer.handleException(new Exception("Exception Message"));
                return null;
            }
        };

        verifyServiceCall(answer);
        verifyErrorResult("Failed to post status because of exception: Exception Message");
    }

    private void verifyServiceCall(Answer<Void> answer) {
        Mockito.doAnswer(answer).when(mockStatusService).postStatus(Mockito.eq(mockAuthToken), Mockito.eq(mockStatus), Mockito.any());
        mainPresenterSpy.postStatus(mockStatus);

        Mockito.verify(mockView).displayInfoMessage("Posting Status...");
        Mockito.verify(mockStatusService).postStatus(Mockito.eq(mockAuthToken), Mockito.eq(mockStatus), Mockito.notNull());
    }
    private void verifyErrorResult(String message) {
        Mockito.verify(mockView).displayErrorMessage(message);
        Mockito.verify(mockView).clearInfoMessage();
    }
}
