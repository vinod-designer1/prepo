package com.experiences.projects.booktable;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

public class UserBookingHistoryActivity extends AppCompatActivity {

    Context context;

    UserBookingListAdapter userboookingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_booking_history);

        context = this;

        ArrayList<ParseObject> userbookingInfo = new ArrayList<ParseObject>();

        userboookingAdapter = new UserBookingListAdapter(context, 0, userbookingInfo);

        ListView userbookingList = (ListView) findViewById(R.id.user_booking_history_list);
        userbookingList.setAdapter(userboookingAdapter);

        userbookingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent userbookingIntent = new Intent(context, UserBookingActivity.class);
                userbookingIntent.putExtra("bookingId", userboookingAdapter.getItem(position).getObjectId());
                context.startActivity(userbookingIntent);
            }
        });

        setUpActionBar();

        getBookingList();
    }

    private void getBookingList() {
        ParseUser currentUser = ParseUser.getCurrentUser();

        ParseQuery<ParseObject> userbookingQuery = ParseQuery.getQuery("UserBooking");
        userbookingQuery.whereEqualTo("User", currentUser);
        userbookingQuery.include("BookedHotel");
        userbookingQuery.orderByDescending("ReservationTime");
        userbookingQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> bookings, ParseException e) {
                for (int i = 0; i < bookings.size(); ++i) {
                    userboookingAdapter.add(bookings.get(i));
                }
            }
        });
    }

    private void setUpActionBar() {
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayShowCustomEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_booking_history, menu);
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
        }  else if (id == android.R.id.home) {
            NavUtils.navigateUpTo(this,
                    new Intent(this, HomeActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class UserBookingListAdapter extends ArrayAdapter<ParseObject> {

        public UserBookingListAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        public UserBookingListAdapter(Context context, int resource, List<ParseObject> items) {
            super(context, resource, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View v = convertView;

            if (v == null) {
                LayoutInflater vi;
                vi = LayoutInflater.from(getContext());
                v = vi.inflate(R.layout.item_userbookinghistory, null);
            }

            final ParseObject userbooking = (ParseObject) getItem(position);

            if (userbooking != null) {
                TextView tv_bookedTime = (TextView) v.findViewById(R.id.tv_hotel_booked_time);
                TextView tv_hotel_name = (TextView) v.findViewById(R.id.tv_hotel_name);
                TextView tv_status = (TextView) v.findViewById(R.id.tv_status);

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss");
                ParseObject hotel = userbooking.getParseObject("BookedHotel");

                Date reservationTime = userbooking.getDate("ReservationTime");
                tv_bookedTime.setText(formatter.format(reservationTime));

                tv_hotel_name.setText(hotel.getString("Name"));

                int status = userbooking.getInt("BookingState");
                String status_message = "";

                if (status == 1) {
                    status_message = "Under Process";
                } else if (status == 2) {
                    status_message = "Confirmed";
                } else if (status == -1) {
                    status_message = "Canceled";
                } else if (status == -2) {
                    status_message = "Not Confirmed";
                }

                tv_status.setText(status_message);
            }

            return v;
        }

    }
}
