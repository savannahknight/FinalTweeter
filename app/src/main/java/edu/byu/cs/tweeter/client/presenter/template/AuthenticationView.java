package edu.byu.cs.tweeter.client.presenter.template;

import edu.byu.cs.tweeter.model.domain.User;

public interface AuthenticationView extends BaseView {
    void navigateToUser(User user);

    void displayInfoMessage(String message);

    void clearErrorMessage();

    void clearInfoMessage();
}
