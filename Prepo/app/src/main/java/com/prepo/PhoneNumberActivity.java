package com.prepo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class PhoneNumberActivity extends AppCompatActivity {

    private EditText mPhoneNumberView;
    private Button mRegisterMobileBtn;
    private ProgressBar mNumberProgress;
    private Context _context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        _context = this;

        attachViewEvents();
    }

    protected void showToast(CharSequence text) {
        Toast.makeText(_context, text, Toast.LENGTH_SHORT).show();
    }

    private void attachViewEvents() {
        mPhoneNumberView = (EditText) findViewById(R.id.register_mobile_number);
        mRegisterMobileBtn = (Button) findViewById(R.id.register_number_button);
        mNumberProgress = (ProgressBar) findViewById(R.id.number_progress);

        mRegisterMobileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressBar();

                ParseUser user = ParseUser.getCurrentUser();

                if (user != null) {
                    String phone = mPhoneNumberView.getText().toString();

                    if (phone.isEmpty() || phone.length() != 10) hideProgressBar();
                    else {
                        user.put("Phone", phone);
                        user.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Intent homeIntent = new Intent(_context, HomeActivity.class);
                                    _context.startActivity(homeIntent);
                                } else {
                                    showToast("An error occured!");
                                    hideProgressBar();
                                }
                            }
                        });
                    }
                } else {
                    Intent loginIntent = new Intent(_context, ParseLoginActivity.class);
                    _context.startActivity(loginIntent);
                }
            }
        });

    }

    private void showProgressBar() {
        mRegisterMobileBtn.setVisibility(View.GONE);
        mNumberProgress.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        mRegisterMobileBtn.setVisibility(View.VISIBLE);
        mNumberProgress.setVisibility(View.GONE);
    }

}
