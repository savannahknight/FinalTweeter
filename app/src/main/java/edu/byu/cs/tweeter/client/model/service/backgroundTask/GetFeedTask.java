package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that retrieves a page of statuses from a user's feed.
 */
public class GetFeedTask extends PagedStatusTask {
    static final String URL_PATH = "/getfeed";
    static final String LOG_TAG = "getFeed";

    public GetFeedTask(AuthToken authToken, User targetUser, int limit, Status lastStatus,
                       Handler messageHandler) {
        super(authToken, targetUser, limit, lastStatus, messageHandler);
    }

    @Override
    protected Pair<List<Status>, Boolean> getItems() {
        List<Status> statuses = null;
        Boolean hasMorePages = false;
        try {
            String targetUserAlias = getTargetUser() == null ? null : getTargetUser().getAlias();
            Status lastStatus = getLastItem() == null ? null : getLastItem();

            FeedRequest request = new FeedRequest(getAuthToken(), targetUserAlias, getLimit(), lastStatus);
            FeedResponse response = getServerFacade().getFeed(request, URL_PATH);

            if(response.isSuccess()) {
                statuses = response.getStatuses();
                hasMorePages = response.getHasMorePages();
                return new Pair<>(statuses, hasMorePages);
            }
            else {
                sendFailedMessage(response.getMessage());
            }
        } catch (IOException | TweeterRemoteException ex) {
            Log.e(LOG_TAG, "Failed to get feed", ex);
            sendExceptionMessage(ex);
        }
        return new Pair<>(statuses, hasMorePages);
    }
}
