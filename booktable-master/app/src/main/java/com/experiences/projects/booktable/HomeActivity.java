package com.experiences.projects.booktable;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;
import java.util.concurrent.Callable;


public class HomeActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private CharSequence mTitle;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    TextView maTitleTextView;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        context = this;

        setUpActionBar();
        setUpNavigationMenu();
    }

    private void setUpNavigationMenu() {
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout),
                new Callable<Integer>() {
                    public Integer call() {
                        return drawOpenCallback();
                    }
                },
                new Callable<Integer>() {
                    public Integer call() {
                        return drawCloseCallback();
                    }
                });
    }

    private int drawOpenCallback() {
        maTitleTextView.setText("Prepo");
        return 0;
    }

    private int drawCloseCallback() {
        maTitleTextView.setText("Restuarent");
        return 0;
    }

    private void setUpActionBar() {
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
//        mActionBar.setDisplayHomeAsUpEnabled(true);
//        mActionBar.setHomeButtonEnabled(true);
        LayoutInflater mInflater = LayoutInflater.from(this);

        View mCustomView = mInflater.inflate(R.layout.actionbar_home, null);
        maTitleTextView = (TextView) mCustomView.findViewById(R.id.tv_action_title_text);
        maTitleTextView.setText("Restuarents");

//        ImageView logoView = (ImageView) mCustomView
//                .findViewById(R.id.iv_logoView);
//        logoView.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(getApplicationContext(), "Title",
//                        Toast.LENGTH_LONG).show();
//            }
//        });

        ImageButton refreshButton = (ImageButton) mCustomView
                .findViewById(R.id.ib_actionbarRefreshBtn);
        refreshButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Refresh Clicked!",
                        Toast.LENGTH_LONG).show();
            }
        });

        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);
    }

    private void getMyBookingList(ParseUser user) {
        ParseQuery<ParseObject> bookingQuery = ParseQuery.getQuery("UserBooking");
        bookingQuery.whereEqualTo("User", user);
        bookingQuery.whereEqualTo("BookingState", 1);
        bookingQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {

                } else {

                }
            }
        });
    }

    private  void getSharedBookingList(ParseUser user) {
        ParseQuery<ParseObject> sharedBookingQuery = ParseQuery.getQuery("UserBooking");
        sharedBookingQuery.whereEqualTo("PeopleComing", user);
        sharedBookingQuery.whereEqualTo("BookingState", 1);
        sharedBookingQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {

                } else {

                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        if (position == 0) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.homecontainer, new HotelList())
                    .commit();
        } else if (position == 1) {
            Intent userBookingHistoryIntent = new Intent(context, UserBookingHistoryActivity.class);
            context.startActivity(userBookingHistoryIntent);
        } else if (position == 2) {

        } else if (position == 3) {
            ParseUser.logOut();
            Intent splash = new Intent(context, App.class);
            context.startActivity(splash);
        }

    }
}
