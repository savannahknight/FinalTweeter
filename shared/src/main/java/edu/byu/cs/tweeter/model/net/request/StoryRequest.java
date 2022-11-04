package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;

public class StoryRequest extends PagedRequest<Status> {

    public StoryRequest() {
        super();
    }

    public StoryRequest(AuthToken authToken, String userAlias, int limit, Status lastItem) {
        super(authToken, userAlias, limit, lastItem);
    }
}
