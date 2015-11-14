package com.prepo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.prepo.ui.adapter.HotelMenuListAdapter;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HotelProfileActivity extends AppCompatActivity {

    private Context _context;

    private HotelMenuListAdapter hotelItemAdapter;

    String hotelid;

    String hotel_name;
    String hotel_pic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        _context = this;

        Intent intent = getIntent();
        hotelid = intent.getStringExtra("HotelId");

        ArrayList<ParseObject> hotelMenuInfo = new ArrayList<ParseObject>();

        hotelItemAdapter = new HotelMenuListAdapter(_context, 0, hotelMenuInfo);

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
        final ParseGeoPoint locationGeo = hotel.getParseGeoPoint("Location");
        String attire = hotel.getString("Attire");

        JSONArray cuisines = hotel.getJSONArray("Cuisines");
        String cuisine_text = "";


        String pic = "";
        try {
            pic = pictures.getString(0);
            cuisine_text = cuisines.join(", ").replace("\"", "");
        } catch (JSONException e) {

        }

        hotel_name = name;
        hotel_pic = pic;

        TextView actionbar_name = (TextView) findViewById(R.id.toolbar_title);

        actionbar_name.setText(name);

        View venu_image_view = (View) findViewById(R.id.include_venue_image);

        ImageView iv_hotel_pic = (ImageView) venu_image_view.findViewById(R.id.background_image);

        Picasso.with(_context).load(pic).resize(400, 300)
                .centerCrop().into(iv_hotel_pic);

        TextView tv_hotel_name = (TextView) findViewById(R.id.venue_name);
        tv_hotel_name.setText(name);

        TextView tv_hotel_cuisine = (TextView) findViewById(R.id.venue_cuisines);
        tv_hotel_cuisine.setText(cuisine_text);

        TextView tv_hotel_desc = (TextView) findViewById(R.id.venue_description);
        tv_hotel_desc.setText(desc);

        TextView tv_hotel_loc = (TextView) findViewById(R.id.address_label);
        tv_hotel_loc.setText(location);

        tv_hotel_loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = String.format(Locale.ENGLISH, "geo:%f,%f", locationGeo.getLatitude(), locationGeo.getLongitude());
                System.out.println("uri: "+uri);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                mapIntent.setPackage("com.google.android.apps.maps");
                _context.startActivity(mapIntent);
            }
        });

        TextView tv_hotel_attire = (TextView) findViewById(R.id.venue_attire);
        tv_hotel_attire.setText(attire);

        TextView tv_hotel_price = (TextView) findViewById(R.id.venue_price);
        tv_hotel_price.setText(price);

        LinearLayout ll_hotel_menu = (LinearLayout) findViewById(R.id.venue_menu_button);
        ll_hotel_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent menuIntent = new Intent(_context, HotelMenuActivity.class);
                menuIntent.putExtra("HotelId", hotelid);
                menuIntent.putExtra("HotelName", hotel_name);
                menuIntent.putExtra("HotelPic", hotel_pic);
                _context.startActivity(menuIntent);
            }
        });


        Button btn_confirm = (Button) findViewById(R.id.continue_button);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences myPref = _context.getSharedPreferences("MyPref", _context.MODE_PRIVATE);
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

                Intent reserveIntent = new Intent(_context, TimePrefActivity.class);

                _context.startActivity(reserveIntent);
            }
        });


    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            Log.e("src", src);
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

            Intent home_intent = new Intent(this, HomeActivity.class);

            NavUtils.navigateUpTo(this, home_intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
