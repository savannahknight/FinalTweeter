package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.observer.ResponseObserver;
import edu.byu.cs.tweeter.client.presenter.template.AuthenticationView;
import edu.byu.cs.tweeter.model.domain.User;

public class AuthenticationPresenter extends BasePresenter<AuthenticationView> {

    public AuthenticationPresenter(AuthenticationView view) {
        super(view);
    }

    public String validateLogin(String username, String password) {
        if (username.length() == 0) {
            return "Alias cannot be empty.";
        }
        if (username.charAt(0) != '@') {
            return "Alias must begin with @.";
        }
        if (username.length() < 2) {
            return "Alias must contain 1 or more characters after the @.";
        }
        if (password.length() == 0) {
            return "Password cannot be empty.";
        }
        return null;
    }

    public class AuthenticationObserver implements ResponseObserver<User> {

        @Override
        public void handleSuccess(User user) {
            view.displayInfoMessage("Hello " + Cache.getInstance().getCurrUser().getName());
            view.clearErrorMessage();
            view.navigateToUser(user);
        }

        @Override
        public void handleFailure(String message) {
            view.clearInfoMessage();
            view.displayErrorMessage(message);
        }

        @Override
        public void handleException(Exception exception) {
            view.clearInfoMessage();
            view.displayErrorMessage(exception.getMessage());
        }
    }
}
