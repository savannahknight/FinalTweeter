package edu.byu.cs.tweeter.client.model.service;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.BackgroundTaskUtils;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LogoutTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.RegisterTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.AuthenticationHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.NotificationHandler;
import edu.byu.cs.tweeter.client.model.service.observer.NotificationObserver;
import edu.byu.cs.tweeter.client.model.service.observer.ResponseObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;

public class UserService extends BackgroundTaskUtils {

    public void login(String username, String password, ResponseObserver loginObserver) {
        LoginTask loginTask = new LoginTask(username,
                password, new AuthenticationHandler(loginObserver));
        BackgroundTaskUtils.runTask(loginTask);
    }

    public void register(String firstName, String lastName, String username, String password, Bitmap image, ResponseObserver registerObserver) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] imageBytes = bos.toByteArray();

        // Intentionally, Use the java Base64 encoder so it is compatible with M4.
        String imageBytesBase64 = Base64.getEncoder().encodeToString(imageBytes);

        RegisterTask registerTask = new RegisterTask(firstName, lastName, username, password, imageBytesBase64, new AuthenticationHandler(registerObserver));
        BackgroundTaskUtils.runTask(registerTask);
    }

    public void logout(AuthToken currUserAuthToken, NotificationObserver logoutObserver) {
        LogoutTask logoutTask = new LogoutTask(currUserAuthToken, new NotificationHandler(logoutObserver));
        BackgroundTaskUtils.runTask(logoutTask);
    }
}