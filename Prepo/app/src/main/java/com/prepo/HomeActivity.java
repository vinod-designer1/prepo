package com.prepo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ParseUser _user;
    HotelListAdapter hotelAdapter;
    private Context _context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getUserPhoneNumber();

        setContentView(R.layout.activity_home);

        _user = ParseUser.getCurrentUser();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        _context = this;

        fillNavHeader();
        fillHotelList();
    }

    private void fillHotelList() {
        ArrayList<ParseObject> hotelInfo = new ArrayList<ParseObject>();

        hotelAdapter = new HotelListAdapter(_context, 0, hotelInfo);

        ListView hotelList = (ListView) findViewById(R.id.lv_hoste_list);
        hotelList.setAdapter(hotelAdapter);

        hotelList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent hotelIntent = new Intent(_context, HotelProfileActivity.class);
                hotelIntent.putExtra("HotelId", hotelAdapter.getItem(position).getObjectId());
                _context.startActivity(hotelIntent);
            }
        });

        fetchHotelList();
    }

    private void fetchHotelList() {
        ParseQuery<ParseObject> hotelQuery = ParseQuery.getQuery("Hotel");
        hotelQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> hotelList, ParseException e) {
                if (e == null) {
//                    for (int i=0; i < hotelList.size(); ++i) {
//                        hotelAdapter.insert(hotelList.get(i), hotelAdapter.getCount());
//                    }

                    for (int i=0; i < 3; ++i) {
                        hotelAdapter.insert(hotelList.get(0), hotelAdapter.getCount());
                    }

                    hotelAdapter.notifyDataSetChanged();
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });
    }

    private void fillNavHeader() {
        TextView tvUserName = (TextView) findViewById(R.id.tv_username);
        TextView tvUserEmail = (TextView) findViewById(R.id.tv_useremail);

        tvUserName.setText(_user.getString("name"));
        tvUserEmail.setText(_user.getEmail());
    }

    public void getUserPhoneNumber() {
        ParseUser user = ParseUser.getCurrentUser();
        String number = user.getString("Phone");
        System.out.println("Number " + number);

        if (number == null || number == "") {
            startActivityForResult(new Intent(this, PhoneNumberActivity.class), 1);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.find_restaurants) {
            // Handle the camera action
        } else if (id == R.id.your_bookings) {
            Intent userBookingHistoryIntent = new Intent(_context, UserBookingHistoryActivity.class);
            _context.startActivity(userBookingHistoryIntent);

        } else if (id == R.id.profile) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 1) {

        }
    }

    public class HotelListAdapter extends ArrayAdapter<ParseObject> {

        public HotelListAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        public HotelListAdapter(Context context, int resource, List<ParseObject> items) {
            super(context, resource, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View v = convertView;

            if (v == null) {
                LayoutInflater vi;
                vi = LayoutInflater.from(getContext());
                v = vi.inflate(R.layout.hotel_list_item,null);
            }

            final ParseObject hotel = (ParseObject) getItem(position);

            if (hotel != null) {
                ImageView resPicView = (ImageView) v.findViewById(R.id.venue_image);
                TextView resNameView = (TextView) v.findViewById(R.id.venue_name);
                TextView resLocView = (TextView) v.findViewById(R.id.venue_cuisine_location);
                TextView resCuisinesView = (TextView) v.findViewById(R.id.venue_brag_text);

                JSONArray resPicturesAry = hotel.getJSONArray("Pictures");
                JSONArray resCuisinesAry = hotel.getJSONArray("Cuisines");

                String respic = "",
                        resname = hotel.get("Name").toString(),
                        rescuisines = "",
                        reslocation = hotel.get("LocationName").toString();
                try {
                    respic = resPicturesAry.getString(0);

                    for (int i = 0; i < resCuisinesAry.length(); ++i) {
                        rescuisines += resCuisinesAry.getString(i) + ", ";
                    }

                    rescuisines = rescuisines.substring(0, rescuisines.length()-2);

                } catch (JSONException e) {

                }

                Picasso.with(_context).load(respic).resize(1850, 900)
                        .centerCrop().into(resPicView);
                resNameView.setText(resname);
                resCuisinesView.setText(rescuisines);
                resLocView.setText(reslocation);

            }

            return v;
        }

    }
}
