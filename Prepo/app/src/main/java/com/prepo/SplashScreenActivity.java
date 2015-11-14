package com.prepo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;

public class SplashScreenActivity extends Activity {

    ViewPager viewPager;
    PagerAdapter adapter;
    int[] screenIds;
    Context _context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        _context = this;

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "nyNhfpzs0RL8ayy0312eyFfuJYFXmjuyFkYjVn9T", "k4ViZtiqik0iJRSq9uPkhjcztLioimWcAlNhIDqo");

        ParseFacebookUtils.initialize(getApplicationContext());
        ParseInstallation.getCurrentInstallation().saveInBackground();

//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        Intent ilogin = new Intent(_context, ParseLoginActivity.class);
        _context.startActivity(ilogin);
    }
}
