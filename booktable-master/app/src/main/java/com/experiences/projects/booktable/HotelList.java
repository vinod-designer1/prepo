package com.experiences.projects.booktable;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HotelList extends ActionBarActivity {

    Context context;
    HotelListAdapter hotelAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_list);

        context = this;


        ArrayList<ParseObject> hotelInfo = new ArrayList<ParseObject>();

        hotelAdapter = new HotelListAdapter(context, 0, hotelInfo);

        ListView hotelList = (ListView) findViewById(R.id.lv_hoste_list);
        hotelList.setAdapter(hotelAdapter);

        hotelList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent homeIntent = new Intent(context, HotelProfileActivity.class);
                homeIntent.putExtra("HotelId", hotelAdapter.getItem(position).getObjectId());
                context.startActivity(homeIntent);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_hotel_list, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = searchView.getQuery().toString();

                ParseQuery<ParseObject> hotelQuery = ParseQuery.getQuery("Hotel");

                if (!query.isEmpty()) {
                    hotelQuery.whereContains("Name", query);
                    hotelQuery.whereContains("LocationName", query);
                    hotelQuery.whereContains("Cuisines", query);
                }

                hotelQuery.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> hotelList, ParseException e) {
                        if (e == null) {
                            hotelAdapter.clear();

                            for (int i=0; i < hotelList.size(); ++i) {
                                hotelAdapter.insert(hotelList.get(i), hotelAdapter.getCount());
                            }

                            hotelAdapter.notifyDataSetChanged();
                        } else {
                            Log.d("score", "Error: " + e.getMessage());
                        }
                    }
                });
            }
        });

        return super.onCreateOptionsMenu(menu);
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
                v = vi.inflate(R.layout.hotellistitem, null);
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

                Picasso.with(context).load(respic).resize(1850, 900)
                        .centerCrop().into(resPicView);
                resNameView.setText(resname);
                resCuisinesView.setText(rescuisines);
                resLocView.setText(reslocation);

            }

            return v;
        }

    }
}
