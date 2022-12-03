package edu.byu.cs.tweeter.server.service;

import org.joda.time.DateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.server.dao.AuthTokenDAO;
import edu.byu.cs.tweeter.server.dao.StatusDAO;
import edu.byu.cs.tweeter.server.dao.dynamodb.DynamoDAOFactory;

public class StatusServiceTest {
    private StoryRequest request;
    private StoryRequest nextPageRequest;
    private StoryResponse expectedResponse;
    private StoryResponse expectedResponse2;
    private StatusDAO mockStatusDAO;
    private AuthTokenDAO mockAuthTokenDAO;
    private StatusService statusServiceSpy;
    private AuthToken authToken;

    @BeforeEach
    public void setup() {
        authToken = new AuthToken();

        User currentUser = new User("FirstName", "LastName", "@test", null);
        Status resultStatus1 = new Status("Test post", currentUser, DateTime.now().toString(), new ArrayList<>(Collections.singletonList("https:www.byu.edu")), new ArrayList<>(Collections.singletonList("@test2")));
        Status resultStatus2 = new Status("Test post2", currentUser, DateTime.now().toString(), new ArrayList<>(Collections.singletonList("https:www.byu.edu")), new ArrayList<>(Collections.singletonList("@test2")));
        Status resultStatus3 = new Status("Test post3", currentUser, DateTime.now().toString(), new ArrayList<>(Collections.singletonList("https:www.byu.edu")), new ArrayList<>(Collections.singletonList("@test2")));

        request = new StoryRequest(authToken, currentUser.getAlias(), 2, null);
        nextPageRequest = new StoryRequest(authToken, currentUser.getAlias(), 2, resultStatus2);

        expectedResponse = new StoryResponse(Arrays.asList(resultStatus1, resultStatus2), true);
        expectedResponse2 = new StoryResponse(Arrays.asList(resultStatus3), false);

        mockStatusDAO = Mockito.mock(StatusDAO.class);
        mockAuthTokenDAO = Mockito.mock(AuthTokenDAO.class);

        Mockito.when(mockStatusDAO.getStory(request)).thenReturn(expectedResponse);
        Mockito.when(mockStatusDAO.getStory(nextPageRequest)).thenReturn(expectedResponse2);

        statusServiceSpy = Mockito.spy(new StatusService(new DynamoDAOFactory()));
        Mockito.when(statusServiceSpy.getStatusDAO()).thenReturn(mockStatusDAO);
        Mockito.when(statusServiceSpy.getAuthTokenDAO()).thenReturn(mockAuthTokenDAO);
        Mockito.when(mockAuthTokenDAO.authenticateCurrentUser(Mockito.any())).thenReturn(true);
    }

    @Test
    public void testGetStory_validRequest_correctResponse() {
        StoryResponse response = statusServiceSpy.getStory(request);
        Assertions.assertEquals(expectedResponse, response);

        response = statusServiceSpy.getStory(nextPageRequest);
        Assertions.assertEquals(expectedResponse2, response);

    }
}
