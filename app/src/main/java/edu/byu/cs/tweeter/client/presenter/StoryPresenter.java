package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.presenter.template.PagedView;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StoryPresenter extends PagedPresenter<Status> {

    private StatusService statusService;

    public StoryPresenter(PagedView<Status> view) {
        super(view);
        this.statusService = new StatusService();
    }

    @Override
    void getItems(AuthToken authToken, User targetUser, int pageSize, Status lastItem) {
        statusService.loadMoreStoryItems(authToken, targetUser, pageSize, lastItem, new GetItemsObserver());
    }
}
