package com.prepo;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.os.CountDownTimer;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class UserBookingActivity extends AppCompatActivity {

    private Context _context;

    private TextView textViewTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_booking);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        _context = this;

        attachActions();

        Intent intent = getIntent();

        String bookingId = intent.getStringExtra("bookingId");

        ParseQuery<ParseObject> userbookingQuery = ParseQuery.getQuery("UserBooking");
        userbookingQuery.include("BookedHotel");

        userbookingQuery.getInBackground(bookingId, new GetCallback<ParseObject>() {

            @Override
            public void done(final ParseObject userbooking, ParseException e) {
                if (e == null) {

                    Integer bookingState = userbooking.getInt("BookingState");

                    if (bookingState == 1) {
                        Date reservationDate  = userbooking.getDate("ReservationTime");
                        long currentTime = Calendar.getInstance().getTimeInMillis();

                        long diffTime = (currentTime - reservationDate.getTime());

                        if ( diffTime <= 900000 && diffTime >= 0) {
                            new CounterClass(diffTime, 1000).start();
                        } else {
                            textViewTimer.setText("Not Confirmed!");
                        }
                    } else if (bookingState == 2) {
                        textViewTimer.setText("Booked!");
                    } else if (bookingState == -1) {
                        textViewTimer.setText("Not Confirmed!");
                    }

                } else {
                    Log.d("DATA CALLSAS UB", e.getMessage());
                }
            }
        });


        final SharedPreferences myPref = _context.getSharedPreferences("MyPref", _context.MODE_PRIVATE);

        String hotel_name = myPref.getString("HotelName", "");
        String hotel_url = myPref.getString("HotelImageUrl", "");


        setImage(hotel_name, hotel_url);

        setUpActionBar();
    }

    private void attachActions() {
        TextView tvPreorder = (TextView) findViewById(R.id.ub_preorder_text);

        tvPreorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent menuIntent = new Intent(_context, SharedMenuActivity.class);
                _context.startActivity(menuIntent);
            }
        });

        textViewTimer = (TextView) findViewById(R.id.ub_status_text);

    }

    private void setUpActionBar() {
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(true);
        mActionBar.setDisplayShowTitleEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayShowCustomEnabled(true);

        TextView actionbar_name = (TextView) findViewById(R.id.toolbar_title);
        actionbar_name.setText("Your Booking");
    }

    private void setImage(String hotelName, String pic) {
        View main_header_view = findViewById(R.id.ub_venue_header);
        ImageView header_image_view = (ImageView) main_header_view.findViewById(R.id.background_image);
        Picasso.with(_context).load(pic).resize(400, 300)
                .centerCrop().into(header_image_view);

        TextView header_text_view = (TextView) main_header_view.findViewById(R.id.centered_text_view);
        header_text_view.setText(hotelName);
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
        }  else if (id == android.R.id.home) {
            NavUtils.navigateUpTo(this,
                    new Intent(this, UserBookingHistoryActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class CounterClass extends CountDownTimer {

        public CounterClass(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            textViewTimer.setText("Completed.");
        }

        @Override
        public void onTick(long millisUntilFinished) {
            long millis = millisUntilFinished;
            String hms = String.format(
                    "%02d:%02d:%02d",
                    TimeUnit.MILLISECONDS.toHours(millis),
                    TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                    TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
            );
            textViewTimer.setText(hms);
        }
    }

}
