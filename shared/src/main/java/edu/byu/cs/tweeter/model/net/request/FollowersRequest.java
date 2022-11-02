package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public class FollowersRequest extends PagedRequest<String> {

    public FollowersRequest() {
        super();
    }

    /**
     * Creates an instance.
     *
     * @param authToken
     * @param userAlias the alias of the user whose items are to be returned.
     * @param limit     the maximum number of items to return.
     * @param lastItem  the alias of the last items that was returned in the previous request (null if
     *                  there was no previous request or if no followees were returned in the
     */
    public FollowersRequest(AuthToken authToken, String userAlias, int limit, String lastItem) {
        super(authToken, userAlias, limit, lastItem);
    }
}
