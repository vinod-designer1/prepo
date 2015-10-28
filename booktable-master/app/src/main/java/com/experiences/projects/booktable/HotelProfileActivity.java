package com.experiences.projects.booktable;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HotelProfileActivity extends AppCompatActivity {

    Context context;
    private HotelMenuListAdapter hotelItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_profile);

        context = this;

        Intent intent = getIntent();
        String hotelid = intent.getStringExtra("HotelId");

        ArrayList<ParseObject> hotelMenuInfo = new ArrayList<ParseObject>();

        hotelItemAdapter = new HotelMenuListAdapter(context, 0, hotelMenuInfo);

        if (hotelid == null) {
            Log.d("DATA CALLS", "Hello!");
        } else {
            Log.d("DATA CALLS", hotelid);
            fetchHotelInfo(hotelid);
        }

        setUpActionBar();

    }

    private void setUpActionBar() {
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);
//        LayoutInflater mInflater = LayoutInflater.from(this);

//        View mCustomView = mInflater.inflate(R.layout.actionbar_home, null);
//        TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.tv_action_title_text);
//        mTitleTextView.setText("BookTable");
//
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
//
//        ImageButton refreshButton = (ImageButton) mCustomView
//                .findViewById(R.id.ib_actionbarRefreshBtn);
//        refreshButton.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(getApplicationContext(), "Refresh Clicked!",
//                        Toast.LENGTH_LONG).show();
//            }
//        });
//
//        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);


    }

    private void fetchHotelInfo(String hotelId) {
        ParseQuery<ParseObject> hotelQuery = ParseQuery.getQuery("Hotel");
        hotelQuery.getInBackground(hotelId, new GetCallback<ParseObject>() {
            public void done(final ParseObject hotel, ParseException e) {
                if (e == null) {
                    ParseQuery<ParseObject> hotelItemQuery = ParseQuery.getQuery("HotelMenu");
                    hotelItemQuery.whereEqualTo("Hotel", hotel);

                    fillHotelInfo(hotel);


                    hotelItemQuery.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> hotelItemList, ParseException e) {
                            if (e == null) {
                                for (int i = 0; i < hotelItemList.size(); ++i) {
                                    hotelItemAdapter.insert(hotelItemList.get(i), hotelItemAdapter.getCount());
                                }

                                hotelItemAdapter.notifyDataSetChanged();
                            } else {
                                Log.d("score", "Error: " + e.getMessage());
                            }
                        }
                    });

                } else {
                    // something went wrong

                    Log.d("DATA CALLSAS", e.getMessage());
                }
            }
        });
    }

    private void fillHotelInfo(final ParseObject hotel) {
        String name = hotel.getString("Name");
        Double rating = hotel.getDouble("Rating");
        final JSONArray pictures = hotel.getJSONArray("Pictures");
        String desc = hotel.getString("Description");
        String location = hotel.getString("LocationName");
        String price = hotel.getString("CostForTwo");

        String pic = "";
        try {
            pic = pictures.getString(0);
        } catch (JSONException e) {

        }

        View venu_image_view = (View) findViewById(R.id.include_venue_image);

        ImageView iv_hotel_pic = (ImageView) venu_image_view.findViewById(R.id.background_image);

        Picasso.with(context).load(pic).resize(400, 300)
                .centerCrop().into(iv_hotel_pic);

        TextView tv_hotel_name = (TextView) findViewById(R.id.venue_info_text);
        tv_hotel_name.setText(name);

        TextView tv_hotel_desc = (TextView) findViewById(R.id.venue_description);
        tv_hotel_desc.setText(desc);

        TextView tv_hotel_loc = (TextView) findViewById(R.id.address_label);
        tv_hotel_loc.setText(location);

        TextView tv_hotel_attire = (TextView) findViewById(R.id.venue_attire);
        tv_hotel_attire.setText("Casual");

        TextView tv_hotel_price = (TextView) findViewById(R.id.venue_price);
        tv_hotel_price.setText(price);

        LinearLayout ll_hotel_menu = (LinearLayout) findViewById(R.id.venue_menu_button);
        ll_hotel_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent menuIntent = new Intent(context, HotelMenuActivity.class);

                context.startActivity(menuIntent);
            }
        });


        Button btn_confirm = (Button) findViewById(R.id.continue_button);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences myPref = context.getSharedPreferences("MyPref", context.MODE_PRIVATE);
                SharedPreferences.Editor userPrefEditor = myPref.edit();

                userPrefEditor.putString("HotelId", hotel.getObjectId());
                userPrefEditor.putString("HotelName", hotel.getString("Name"));

                String pic = "";
                try {
                    pic = pictures.getString(0);
                } catch (JSONException er) {

                }

                userPrefEditor.putString("HotelImageUrl", pic);

                userPrefEditor.commit();

                Intent reserveIntent = new Intent(context, TimePrefActivity.class);

                context.startActivity(reserveIntent);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_hotel_profile, menu);
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
        } else if (id == android.R.id.home) {
            NavUtils.navigateUpTo(this,
                    new Intent(this, HomeActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            Log.e("src",src);
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            Log.e("Bitmap","returned");
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Exception",e.getMessage());
            return null;
        }
    }
}
