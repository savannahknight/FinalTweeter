package edu.byu.cs.tweeter.server.dao.dynamodb;

import com.amazonaws.services.dynamodbv2.document.AttributeUpdate;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.KeyAttribute;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Base64;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetFollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.GetFollowersCountResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowingCountResponse;
import edu.byu.cs.tweeter.server.dao.UserDAO;

public class UserDAODynamo extends BaseDAODynamo implements UserDAO {
    private final String TableName = "User";
    Table table = dynamoDB.getTable(TableName);

    @Override
    public User login(LoginRequest request) {
        try {
            KeyAttribute itemToGet = new KeyAttribute("user_alias", request.getUsername());
            Item userItem = table.getItem(itemToGet);
            String databasePassword = (String) userItem.get("password");
            String databaseSalt = (String) userItem.get("salt");
            String firstName = (String) userItem.get("first_name");
            String lastName = (String) userItem.get("last_name");
            String imageURL = (String) userItem.get("image_url");

            // Given at login
            String suppliedPassword = request.getPassword();
            String regeneratedPasswordToVerify = getSecurePassword(suppliedPassword, databaseSalt);

            if (databasePassword.equals(regeneratedPasswordToVerify)) {

                return new User(firstName, lastName, request.getUsername(), imageURL);
            }
            else {
                throw new RuntimeException("[BadRequest] Invalid Credentials");
            }
        }
        catch (Exception e) {
            throw new RuntimeException("[DBError] Failed to get user");
        }
    }

    @Override
    public User register(RegisterRequest request) {
        // Given at registration
        String password = request.getPassword();

        // Store this in the database
        String salt = getSalt();

        // Store this in the database
        String securePassword = getSecurePassword(password, salt);

        try {
            System.out.println("Adding a new item...");
            PutItemOutcome outcome = table
                    .putItem(new Item().withPrimaryKey("user_alias", request.getUsername())
                            .withString("password", securePassword).withString("salt", salt)
                            .withString("first_name", request.getFirstName()).withString("last_name", request.getLastName())
                            .withString("image_url", request.getImage()).withInt("followers_count", 0)
                            .withInt("following_count", 0));

            System.out.println("PutItem succeeded:\n" + outcome.getPutItemResult().toString());
            User user = new User(request.getFirstName(), request.getLastName(), request.getUsername(), request.getImage());

            return user;

        } catch (Exception e) {
            System.err.println("Unable to add item: " + request.getUsername());
            System.err.println(e.getMessage());
            throw new RuntimeException("[DBError] register failed");
        }
    }

    @Override
    public User getUser(String userAlias) {
        try {
            KeyAttribute itemToGet = new KeyAttribute("user_alias", userAlias);
            Item userItem = table.getItem(itemToGet);
            String firstName = (String) userItem.get("first_name");
            String lastName = (String) userItem.get("last_name");
            String imageURL = (String) userItem.get("image_url");

            return new User(firstName, lastName, userAlias, imageURL);
        }
        catch (Exception e) {
            throw new RuntimeException("[DBError] Failed to get user");
        }
    }

    @Override
    public GetFollowersCountResponse getFollowersCount(GetFollowersCountRequest request) {
        try {
            KeyAttribute itemToGet = new KeyAttribute("user_alias", request.getTargetUser().getAlias());
            Item userItem = table.getItem(itemToGet);
            int followersCount = userItem.getInt("followers_count");

            return new GetFollowersCountResponse(followersCount);
        }
        catch (Exception e) {
            throw new RuntimeException("[DBError] Failed to get followers count");
        }
    }

    @Override
    public GetFollowingCountResponse getFollowingCount(GetFollowingCountRequest request) {
        try {
            KeyAttribute itemToGet = new KeyAttribute("user_alias", request.getTargetUser().getAlias());
            Item userItem = table.getItem(itemToGet);
            int followingCount = userItem.getInt("following_count");

            return new GetFollowingCountResponse(followingCount);
        }
        catch (Exception e) {
            throw new RuntimeException("[DBError] Failed to get following count");
        }
    }

    @Override
    public boolean incrementDecrementFollowCount(String userAlias, boolean increment, String attributeToChange) {
        int followingCount;
        int change = increment ? 1 : -1;
        try {
            KeyAttribute itemToGet = new KeyAttribute("user_alias", userAlias);
            Item userItem = table.getItem(itemToGet);
            followingCount = userItem.getInt(attributeToChange);

            UpdateItemOutcome outcome = table.updateItem(new PrimaryKey("user_alias", userAlias),
                    new AttributeUpdate(attributeToChange).put(followingCount + change));

            System.out.println("UpdateItem succeeded:\n" + outcome.getUpdateItemResult().toString());
            return true;
        } catch (Exception e) {
            System.err.println("Unable to update item: " + userAlias + " with change: " + change + " for attribute: " + attributeToChange);
            System.err.println(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean checkIfUserInDB(String userAlias) {
        try {
            KeyAttribute itemToGet = new KeyAttribute("user_alias", userAlias);
            Item userItem = table.getItem(itemToGet);

            return userItem != null;
        }
        catch (Exception e) {
            throw new RuntimeException("[DBError] Failed to check if alias in use");
        }
    }

    private static String getSecurePassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes());
            byte[] bytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "FAILED TO HASH PASSWORD";
    }

    private static String getSalt() {
        try {
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "SUN");
            byte[] salt = new byte[16];
            sr.nextBytes(salt);
            return Base64.getEncoder().encodeToString(salt);
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }
        return "FAILED TO GET SALT";
    }
}
