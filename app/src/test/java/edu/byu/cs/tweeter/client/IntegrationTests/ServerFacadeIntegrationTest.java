package edu.byu.cs.tweeter.client.IntegrationTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.AuthenticationResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowingCountResponse;
import edu.byu.cs.tweeter.util.FakeData;

public class ServerFacadeIntegrationTest {
    ServerFacade serverFacade;
    User fakeUser;
    FakeData fakeData;

    @BeforeEach
    public void setup() {
        serverFacade = new ServerFacade();
        fakeUser = new User("Allen", "Anderson", "@allen", "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png");
        fakeData = new FakeData();
    }

    @Test
    public void registerTest() {
        try {
            RegisterRequest request = new RegisterRequest("@sav", "password", "Savannah",
                    "Knight", "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png");
            AuthenticationResponse response = serverFacade.register(request, "/register");

            Assertions.assertEquals(fakeUser, response.getUser());
        } catch(Exception ex) {
            Assertions.fail();
        }
    }

    @Test
    public void getFollowersTest() {
        try {
            FollowersRequest request = new FollowersRequest(new AuthToken(), "@test", 10, "@allen");
            FollowersResponse response = serverFacade.getFollowers(request, "/getfollowers");

            List<User> expected = fakeData.getFakeUsers().subList(1, 11);
            Assertions.assertEquals(expected, response.getFollowers());
        } catch(Exception ex) {
            Assertions.fail();
        }
    }

    @Test
    public void getFollowingCountTest() {
        try {
            GetFollowingCountRequest request = new GetFollowingCountRequest(new AuthToken(), fakeUser);
            GetFollowingCountResponse response = serverFacade.getFollowingCount(request, "/getfollowingcount");

            Assertions.assertEquals(20, response.getCount());
        } catch(Exception ex) {
            Assertions.fail();
        }
    }


}
