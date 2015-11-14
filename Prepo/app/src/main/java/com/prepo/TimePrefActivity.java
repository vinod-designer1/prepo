package com.prepo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TimePrefActivity extends AppCompatActivity {
    private Context _context;

    Boolean afterTime = true;
    int persons = 1;
    Calendar cal;
    Calendar today;

    public int s = 0;

    SharedPreferences.Editor myPrefEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_pref);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        _context = this;

        cal = Calendar.getInstance();
        today = Calendar.getInstance();

        final SharedPreferences myPref = _context.getSharedPreferences("MyPref", _context.MODE_PRIVATE);
        myPrefEditor = myPref.edit();


        final String hotel_name = myPref.getString("HotelName", "");
        String hotel_url = myPref.getString("HotelImageUrl", "");

        setImage(hotel_name, hotel_url);
        showPeopleChooser();
        showDateChooser();
        showTimeChooser();

        Button request_table_btn = (Button) findViewById(R.id.continue_button);

        request_table_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myPrefEditor.putInt("Persons", persons);
                myPrefEditor.putLong("Date", cal.getTimeInMillis());
                myPrefEditor.commit();

                String hotelId = myPref.getString("HotelId", "");
                ParseUser user = ParseUser.getCurrentUser();

                ParseQuery<ParseObject> hotelQuery = ParseQuery.getQuery("Hotel");

                ParseObject hotelObj = ParseObject.createWithoutData("Hotel", hotelId);
//                hotelObj.setObjectId(hotelId);

                final ParseObject hotelBookingObj = new ParseObject("UserBooking");
                hotelBookingObj.put("ReservationTime", cal.getTime());
                hotelBookingObj.put("BookedHotel", hotelObj);
                hotelBookingObj.put("User", user);
                hotelBookingObj.put("BookingState", 1);
                hotelBookingObj.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {

                        if (e == null) {

                            Intent bookingIntent = new Intent(_context, UserBookingActivity.class);
                            bookingIntent.putExtra("bookingId", hotelBookingObj.getObjectId());
                            _context.startActivity(bookingIntent);

                        } else {

                        }
                    }
                });
            }
        });

        setUpActionBar();
    }

    private void setUpActionBar() {
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayShowCustomEnabled(true);

        TextView actionbar_name = (TextView) findViewById(R.id.toolbar_title);
        actionbar_name.setText("Your Booking Preference");
    }

    private void setImage(String hotelName, String pic) {
        View main_header_view = findViewById(R.id.venue_header);
        ImageView header_image_view = (ImageView) main_header_view.findViewById(R.id.background_image);
        Picasso.with(_context).load(pic).resize(400, 300)
                .centerCrop().into(header_image_view);

        TextView header_text_view = (TextView) main_header_view.findViewById(R.id.centered_text_view);
        header_text_view.setText(hotelName);
    }

    private SpannableString getTextViewStyle(String view_text) {
        SpannableString text = new SpannableString(view_text);



        text.setSpan(new TextAppearanceSpan(_context, R.style.ReqTableTextView), 0, view_text.indexOf('\n'), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        text.setSpan(new TextAppearanceSpan(_context, R.style.ReqTableSubTextView), view_text.indexOf('\n'), view_text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return text;
    }

    private void showPeopleChooser() {
        ImageButton people_up_btn = (ImageButton) findViewById(R.id.people_up_button);
        ImageButton people_down_btn = (ImageButton) findViewById(R.id.people_down_button);
        final TextView people_text = (TextView) findViewById(R.id.people_text);

        final String suffix_1 = "person", suffix_2 = "people";


        people_text.setText( getTextViewStyle(persons + "\n" + suffix_1), TextView.BufferType.SPANNABLE);

        people_up_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                persons += 1;

                Log.d("TimePref", "Up Persons " + persons);

                if (persons > 1) {
                    String view_text = persons + "\n" + suffix_2;

                    SpannableString text = getTextViewStyle(view_text);

                    people_text.setText(text, TextView.BufferType.SPANNABLE);
                }

            }
        });


        people_down_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TimePref", "Down Persons " + persons);
                if (persons > 1) {
                    persons -= 1;

                    Log.d("TimePref", "Down Persons " + persons);

                    String suffix = "";


                    if (persons > 1) {
                        suffix = suffix_2;
                    } else {
                        suffix = suffix_1;
                    }

                    String view_text = persons + "\n" + suffix;

                    SpannableString text = getTextViewStyle(view_text);

                    people_text.setText(text, TextView.BufferType.SPANNABLE);
                }

            }
        });
    }

    private String getDayString(int day) {
        switch (day) {
            case 1:
                return "SUNDAY";
            case 2:
                return "MONDAY";
            case 3:
                return "TUESDAY";
            case 4:
                return "WEDNESDAY";
            case 5:
                return "THURSDAY";
            case 6:
                return "FRIDAY";
            case 7:
                return "SATURDAY";
            default:
                return "NO DATE";
        }
    }

    private void showDateChooser() {
        ImageButton date_up_btn = (ImageButton) findViewById(R.id.date_up_button);
        ImageButton date_down_btn = (ImageButton) findViewById(R.id.date_down_button);
        final TextView date_text_view = (TextView) findViewById(R.id.date_text);

        final SimpleDateFormat formatter=new SimpleDateFormat("MMM dd, yyyy");

        String date_text = "TODAY\n";
        date_text += formatter.format(cal.getTime());

        date_text_view.setText(getTextViewStyle(date_text));

        date_up_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cal.add(Calendar.DATE, 1);

                String date_text = "";

                int days = cal.get(Calendar.DAY_OF_YEAR) - today.get(Calendar.DAY_OF_YEAR);

                if (days == 1) {
                    date_text += "TOMORROW\n";
                } else {
                    date_text = getDayString(cal.get(Calendar.DAY_OF_WEEK)) + "\n";
                }

                date_text += formatter.format(cal.getTime());

                date_text_view.setText(getTextViewStyle(date_text));

            }
        });

        date_down_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String date_text = "";

                int days = cal.get(Calendar.DAY_OF_YEAR) - today.get(Calendar.DAY_OF_YEAR);

                if (days >= 1) {
                    cal.add(Calendar.DATE, -1);

                    if (days == 1)
                        date_text += "TODAY\n";
                    else if (days == 2)
                        date_text += "TOMORROW\n";
                    else
                        date_text += getDayString(cal.get(Calendar.DAY_OF_WEEK)) + "\n";

                    date_text += formatter.format(cal.getTime());

                    date_text_view.setText(getTextViewStyle(date_text));
                }



            }
        });
    }

    private void showTimeChooser() {
        ImageButton late_time_up_btn = (ImageButton) findViewById(R.id.late_time_up_button);
        ImageButton late_time_down_btn = (ImageButton) findViewById(R.id.late_time_down_button);
        final TextView late_time_text_view = (TextView) findViewById(R.id.late_time_text);

        final SimpleDateFormat formatter = new SimpleDateFormat("hh:mm");


        int minutes = cal.get(Calendar.MINUTE);

        if (minutes > 30) {
            cal.add(Calendar.MINUTE, 60-minutes);
        } else {
            cal.add(Calendar.MINUTE, 30-minutes);
        }

        String time_text = formatter.format(cal.getTime());
        String time_zone = cal.get(Calendar.AM_PM) == 0 ? "AM" : "PM";

        time_text += "\n" + time_zone;

        late_time_text_view.setText(getTextViewStyle(time_text));

        late_time_down_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cal.add(Calendar.MINUTE, -30);

                long time = cal.getTime().getTime() - today.getTime().getTime();

                if (time < 0) {
                    cal.add(Calendar.MINUTE, 30);
                }

                String time_text = formatter.format(cal.getTime());
                String time_zone = cal.get(Calendar.AM_PM) == 0 ? "AM" : "PM";

                time_text += "\n" + time_zone;
                late_time_text_view.setText(getTextViewStyle(time_text));
            }
        });

        late_time_up_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cal.add(Calendar.MINUTE, 30);

                String time_text = formatter.format(cal.getTime());
                String time_zone = cal.get(Calendar.AM_PM) == 0 ? "AM" : "PM";

                time_text += "\n" + time_zone;
                late_time_text_view.setText(getTextViewStyle(time_text));
            }
        });

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
        } else if (id == android.R.id.home) {
            SharedPreferences myPref = _context.getSharedPreferences("MyPref", _context.MODE_PRIVATE);
            String hotelId = myPref.getString("HotelId", "");

            Intent hotel_profile_intent = new Intent(this, HotelProfileActivity.class);
            hotel_profile_intent.putExtra("HotelId", hotelId);

            NavUtils.navigateUpTo(this, hotel_profile_intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
