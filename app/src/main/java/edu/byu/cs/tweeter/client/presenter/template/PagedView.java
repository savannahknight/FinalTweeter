package edu.byu.cs.tweeter.client.presenter.template;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;

public interface PagedView<T> extends BaseView {
    void setLoadingStatus(boolean isLoading);
    void addItems(List<T> items);
    void redirectUser(User user);
}
