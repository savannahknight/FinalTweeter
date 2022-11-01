package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.presenter.template.BaseView;

public abstract class BasePresenter<T extends BaseView> {
    protected T view;

    public BasePresenter(T view) {
        this.view = view;
    }
}
