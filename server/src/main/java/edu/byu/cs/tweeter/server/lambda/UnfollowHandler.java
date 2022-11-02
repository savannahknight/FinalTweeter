package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.FollowToggleRequest;
import edu.byu.cs.tweeter.model.net.response.FollowToggleResponse;
import edu.byu.cs.tweeter.server.service.FollowService;

public class UnfollowHandler implements RequestHandler<FollowToggleRequest, FollowToggleResponse> {

    @Override
    public FollowToggleResponse handleRequest(FollowToggleRequest request, Context context) {
        FollowService service = new FollowService();
        return service.unfollow(request);
    }
}
