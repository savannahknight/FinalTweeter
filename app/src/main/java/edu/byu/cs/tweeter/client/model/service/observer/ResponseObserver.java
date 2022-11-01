package edu.byu.cs.tweeter.client.model.service.observer;

public interface ResponseObserver<T> extends ServiceObserver {
    void handleSuccess(T response);
}
