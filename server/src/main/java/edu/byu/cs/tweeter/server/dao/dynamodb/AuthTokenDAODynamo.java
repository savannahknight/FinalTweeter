package edu.byu.cs.tweeter.server.dao.dynamodb;

import com.amazonaws.services.dynamodbv2.document.AttributeUpdate;
import com.amazonaws.services.dynamodbv2.document.DeleteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.KeyAttribute;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.google.gson.Gson;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.server.dao.AuthTokenDAO;

public class AuthTokenDAODynamo implements AuthTokenDAO {
    final String TableName = "AuthToken";
    Table table = dynamoDB.getTable(TableName);

    //final static long INACTIVITY_MIN_LIMIT = 5;
    @Override
    public AuthToken generateAuthToken(User user) {
        AuthToken authToken = getNewAuthToken();

        try {
            System.out.println("Adding a new item...");
            LocalDateTime now = LocalDateTime.now();
            now = now.plusMinutes(INACTIVITY_MIN_LIMIT);
            Gson gson = new Gson();
            String json = gson.toJson(now);
            PutItemOutcome outcome = table
                    .putItem(new Item().withPrimaryKey("token", authToken.getToken())
                            .withString("timestamp", json).withString("user_alias", user.getAlias()));

            System.out.println("PutItem succeeded:\n" + outcome.getPutItemResult());

            return authToken;

        } catch (Exception e) {
            System.err.println("Unable to add item: " + authToken.getToken());
            System.err.println(e.getMessage());
            throw new RuntimeException("[DBError] AuthToken generation failed");
        }
    }
    private static AuthToken getNewAuthToken() {
        try {
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "SUN");
            byte[] salt = new byte[16];
            sr.nextBytes(salt);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            now = now.plusMinutes(INACTIVITY_MIN_LIMIT);
            return new AuthToken(Base64.getEncoder().encodeToString(salt), dtf.format(now));
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public LogoutResponse logout(LogoutRequest request, boolean timedOut) {
        try {
            System.out.println("Clearing out old authToken");
            DeleteItemOutcome outcome = table
                    .deleteItem(new KeyAttribute("token", request.getAuthToken().getToken()));

            System.out.println("DeleteItem succeeded:\n" + outcome.getDeleteItemResult().toString());

            LogoutResponse response = timedOut ? new LogoutResponse(true, "User session has timed out. Please login again.") : new LogoutResponse(true);
            return response;

        } catch (Exception e) {
            System.err.println("Unable to add item: " + request.getAuthToken().getToken());
            System.err.println(e.getMessage());
            throw new RuntimeException("[DBError] AuthToken generation failed");
        }
    }

    @Override
    public String getCurrUserAlias(AuthToken authToken) {
        try {
            KeyAttribute itemToGet = new KeyAttribute("token", authToken.getToken());
            Item authTokenItem = table.getItem(itemToGet);

            return (String) authTokenItem.get("user_alias");
        }
        catch (Exception e) {
            throw new RuntimeException("[DBError] Failed to get current user for session");
        }
    }

    @Override
    public boolean authenticateCurrentUser(AuthToken authToken) {
        try {
            System.out.println(authToken.toString());
            KeyAttribute itemToGet = new KeyAttribute("token", authToken.getToken());
            Item authTokenItem = table.getItem(itemToGet);
            Gson gson = new Gson();
            LocalDateTime time = gson.fromJson(authTokenItem.getString("timestamp"), LocalDateTime.class);
            LocalDateTime now = LocalDateTime.now();

            System.out.println(time.toString());
            System.out.println(now.toString());

            if (now.isAfter(time)) {
                System.out.println("The authToken has timed out.");
                return false;
            }

            System.out.println("Updating authToken timestamp");

            now = now.plusMinutes(INACTIVITY_MIN_LIMIT);
            String json = gson.toJson(now);

            UpdateItemOutcome outcome = table.updateItem(new PrimaryKey("token", authToken.getToken()),
                    new AttributeUpdate("timestamp").put(json));

            System.out.println("UpdateItem succeeded:\n" + outcome.getUpdateItemResult().toString());

            return true;
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("[DBError] Failed to authenticate current user session");
        }
    }
}
