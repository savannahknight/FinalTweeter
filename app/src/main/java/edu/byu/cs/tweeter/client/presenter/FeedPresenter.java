package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.presenter.template.PagedView;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FeedPresenter extends PagedPresenter<Status>{

    private StatusService statusService;

    public FeedPresenter(PagedView<Status> view) {
        super(view);
        this.statusService = new StatusService();
    }

    @Override
    void getItems(AuthToken authToken, User targetUser, int pageSize, Status lastItem) {
        statusService.loadMoreFeedItems(authToken, targetUser, pageSize, lastItem, new GetItemsObserver());
    }
}

