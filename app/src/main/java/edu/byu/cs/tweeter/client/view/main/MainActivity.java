package edu.byu.cs.tweeter.client.view.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;

import edu.byu.cs.tweeter.R;
import edu.byu.cs.tweeter.client.presenter.MainActivityPresenter;
import edu.byu.cs.tweeter.client.presenter.template.MainView;
import edu.byu.cs.tweeter.client.view.login.LoginActivity;
import edu.byu.cs.tweeter.client.view.login.StatusDialogFragment;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * The main activity for the application. Contains tabs for feed, story, following, and followers.
 */
public class MainActivity extends AppCompatActivity implements StatusDialogFragment.Observer, MainView {

    private static final String LOG_TAG = "MainActivity";

    public static final String CURRENT_USER_KEY = "CurrentUser";

    private Toast infoToast;
    private User selectedUser;
    private TextView followeeCount;
    private TextView followerCount;
    private Button followButton;

    private MainActivityPresenter presenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selectedUser = (User) getIntent().getSerializableExtra(CURRENT_USER_KEY);
        if (selectedUser == null) {
            throw new RuntimeException("User not passed to activity");
        }

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager(), selectedUser);
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setOffscreenPageLimit(1);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StatusDialogFragment statusDialogFragment = new StatusDialogFragment();
                statusDialogFragment.show(getSupportFragmentManager(), "post-status-dialog");
            }
        });

        presenter = new MainActivityPresenter(this);
        updateSelectedUserFollowingAndFollowers();

        TextView userName = findViewById(R.id.userName);
        userName.setText(selectedUser.getName());

        TextView userAlias = findViewById(R.id.userAlias);
        userAlias.setText(selectedUser.getAlias());

        ImageView userImageView = findViewById(R.id.userImage);
        Picasso.get().load(selectedUser.getImageUrl()).into(userImageView);

        followeeCount = findViewById(R.id.followeeCount);
        followeeCount.setText(getString(R.string.followeeCount, "..."));

        followerCount = findViewById(R.id.followerCount);
        followerCount.setText(getString(R.string.followerCount, "..."));

        followButton = findViewById(R.id.followButton);

        if (selectedUser.compareTo(presenter.getCurrUser()) == 0) {
            followButton.setVisibility(View.GONE);
        } else {
            followButton.setVisibility(View.VISIBLE);
            presenter.isFollower(selectedUser);
        }

        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                followButton.setEnabled(false);

                if (followButton.getText().toString().equals(v.getContext().getString(R.string.following))) {
                    presenter.unfollow(selectedUser);
                } else {
                    presenter.follow(selectedUser);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logoutMenu) {
            presenter.logout();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void logoutUser() {
        //Revert to login screen.
        Intent intent = new Intent(this, LoginActivity.class);
        //Clear everything so that the main activity is recreated with the login page.
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //Clear user data (cached data).
        presenter.clearCache();
        startActivity(intent);
    }

    @Override
    public void onStatusPosted(String post) {
//        postingToast = Toast.makeText(this, "Posting Status...", Toast.LENGTH_LONG);
//        postingToast.show();

        try {
            Status newStatus = new Status(post, presenter.getCurrUser(), presenter.getFormattedDateTime(), presenter.parseURLs(post), presenter.parseMentions(post));
            presenter.postStatus(newStatus);
        } catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage(), ex);
            Toast.makeText(this, "Failed to post the status because of exception: " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void updateSelectedUserFollowingAndFollowers() {
        // Get count of most recently selected user's followers.
        presenter.getFollowersCount(selectedUser);

        // Get count of most recently selected user's followees (who they are following)
        presenter.getFollowingCount(selectedUser);
    }

    public void updateFollowButton(boolean removed) {
        // If follow relationship was removed.
        if (removed) {
            followButton.setText(R.string.follow);
            followButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        } else {
            followButton.setText(R.string.following);
            followButton.setBackgroundColor(getResources().getColor(R.color.white));
            followButton.setTextColor(getResources().getColor(R.color.lightGray));
        }
    }

    @Override
    public void handleUnFollowSuccess() {
        updateSelectedUserFollowingAndFollowers();
        updateFollowButton(true);
    }

    @Override
    public void handleFollowSuccess() {
        updateSelectedUserFollowingAndFollowers();
        updateFollowButton(false);
        resetFollowButton();
    }

    @Override
    public void handleLogoutSuccess() {
        logoutUser();
    }

    @Override
    public void handleGetFollowersCountSuccess(int count) {
        followerCount.setText(getString(R.string.followerCount, String.valueOf(count)));
    }

    @Override
    public void handleGetFollowingCountSuccess(int count) {
        followeeCount.setText(getString(R.string.followeeCount, String.valueOf(count)));
    }

    @Override
    public void handleIsFollowerSuccess(boolean isFollower) {
        // If logged in user if a follower of the selected user, display the follow button as "following"
        if (isFollower) {
            followButton.setText(R.string.following);
            followButton.setBackgroundColor(getResources().getColor(R.color.white));
            followButton.setTextColor(getResources().getColor(R.color.lightGray));
        } else {
            followButton.setText(R.string.follow);
            followButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        }
    }

    @Override
    public void handlePostStatusSuccess() {
        infoToast.cancel();
        Toast.makeText(MainActivity.this, "Successfully Posted!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void resetFollowButton() {
        followButton.setEnabled(true);
    }

    @Override
    public void displayInfoMessage(String message) {
        infoToast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        infoToast.show();
    }

    @Override
    public void clearInfoMessage() {
        infoToast.cancel();
    }

    @Override
    public void displayErrorMessage(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();

    }
}
