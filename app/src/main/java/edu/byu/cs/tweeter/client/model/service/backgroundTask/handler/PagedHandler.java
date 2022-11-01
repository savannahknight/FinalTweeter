package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.PagedTask;
import edu.byu.cs.tweeter.client.model.service.observer.PagedObserver;

public class PagedHandler<T> extends BackgroundTaskHandler<PagedObserver<T>> {

    public PagedHandler(PagedObserver<T> observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(PagedObserver<T> observer, Bundle data) {
        List<T> pageItems = (List<T>) data.getSerializable(PagedTask.ITEMS_KEY);
        boolean hasMorePages = data.getBoolean(PagedTask.MORE_PAGES_KEY);
        observer.handleSuccess(pageItems, hasMorePages);
    }
}
