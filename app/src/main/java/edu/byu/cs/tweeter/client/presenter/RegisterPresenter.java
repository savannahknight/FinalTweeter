package edu.byu.cs.tweeter.client.presenter;

import android.graphics.Bitmap;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.presenter.template.AuthenticationView;

public class RegisterPresenter extends AuthenticationPresenter {

    private UserService userService;

    public RegisterPresenter(AuthenticationView view) {
        super(view);
        this.userService = new UserService();
    }

    public void register(String firstName, String lastName, String alias, String password, Bitmap image) {
        String message = validateRegistration(firstName, lastName, alias, password, image);
        if (message == null) {
            view.clearErrorMessage();
            view.displayInfoMessage("Registering...");
            userService.register(firstName, lastName, alias, password, image, new AuthenticationObserver());
        }
        else {
            view.clearInfoMessage();
            view.displayErrorMessage(message);
        }
    }

    public String validateRegistration(String firstName, String lastName, String alias, String password, Bitmap imageToUpload ) {
        if (validateLogin(alias, password) == "") {
            return validateLogin(alias, password);
        }
        if (firstName.length() == 0) {
            return "First Name cannot be empty.";
        }
        if (lastName.length() == 0) {
            return "Last Name cannot be empty.";
        }
        if (imageToUpload == null) {
            return "Profile image must be uploaded.";
        }
        return null;
    }
}
