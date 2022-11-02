
package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public class PagedRequest<T> {

    private AuthToken authToken;
    private String userAlias;
    private int limit;
    private T lastItem;

    /**
     * Allows construction of the object from Json. Private so it won't be called in normal code.
     */
    public PagedRequest() {}

    /**
     * Creates an instance.
     *
     * @param userAlias the alias of the user whose items are to be returned.
     * @param limit the maximum number of items to return.
     * @param lastItem the alias of the last items that was returned in the previous request (null if
     *                     there was no previous request or if no followees were returned in the
     *                     previous request).
     */
    public PagedRequest(AuthToken authToken, String userAlias, int limit, T lastItem) {
        this.authToken = authToken;
        this.userAlias = userAlias;
        this.limit = limit;
        this.lastItem = lastItem;
    }


    public AuthToken getAuthToken() {
        return authToken;
    }


    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }


    public String getUserAlias() {
        return userAlias;
    }


    public void setUserAlias(String userAlias) {
        this.userAlias = userAlias;
    }


    public int getLimit() {
        return limit;
    }


    public void setLimit(int limit) {
        this.limit = limit;
    }


    public T getLastItem() {
        return lastItem;
    }


    public void setLastItem(T lastItem) {
        this.lastItem = lastItem;
    }
}
