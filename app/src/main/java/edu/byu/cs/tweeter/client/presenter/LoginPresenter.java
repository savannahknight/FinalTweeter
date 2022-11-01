package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.presenter.template.AuthenticationView;

public class LoginPresenter extends AuthenticationPresenter{

    private UserService userService;

    public LoginPresenter(AuthenticationView view) {
        super(view);
        this.userService = new UserService();
    }

    public void initiateLogin(String username, String password) {
        String message = validateLogin(username, password);
        if (message == null) {
            view.clearErrorMessage();
            view.displayInfoMessage("Logging in ...");
            userService.login(username, password, new AuthenticationObserver());
        }
        else {
            view.clearInfoMessage();
            view.displayErrorMessage(message);
        }
    }
}
