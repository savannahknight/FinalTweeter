package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;

public class FeedRequest extends PagedRequest<Status> {
    public FeedRequest() {
        super();
    }

    public FeedRequest(AuthToken authToken, String userAlias, int limit, Status lastItem) {
        super(authToken, userAlias, limit, lastItem);
    }
}
