package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.BackgroundTask;
import edu.byu.cs.tweeter.client.model.service.observer.ResponseObserver;

public class ResponseHandler<T> extends BackgroundTaskHandler<ResponseObserver<T>> {

    public ResponseHandler(ResponseObserver<T> observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(ResponseObserver<T> observer, Bundle data) {
        T response = (T) data.getSerializable(BackgroundTask.RESPONSE_KEY);
        observer.handleSuccess(response);
    }
}
