package com.prepo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.HashMap;

public class CartActivity extends AppCompatActivity {

    private Context _context;

    ArrayList<HashMap<String, String>> list;
    String bookingId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent catIntent = getIntent();
        list = (ArrayList<HashMap<String, String>>) catIntent.getSerializableExtra("order");
        bookingId = catIntent.getStringExtra("BookingId");


        _context = this;

        setUpActionBar();

        getMenuList();

        setCartView();
    }

    private void setCartView() {
        ListView cartList = (ListView) findViewById(R.id.lv_cart_list);


    }

    private void getMenuList() {
        for (int cati = 0; cati < list.size(); ++cati) {
            HashMap<String, String> itemInfo = list.get(cati);


        }
    }

    private void setUpActionBar() {
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayShowCustomEnabled(true);

        TextView actionbar_name = (TextView) findViewById(R.id.toolbar_title);
        actionbar_name.setText("Cart");
    }

//    public class CartAdapter extends ArrayAdapter<HashMap<String, String>> {
//        public CartAdapter(Context context,  ArrayAdapter<HashMap<String, String>> menuList) {
//            super(context, 0, menuList);
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
////            // Get the data item for this position
////            HashMap<String,String> user = getItem(position);
////            // Check if an existing view is being reused, otherwise inflate the view
////            if (convertView == null) {
////                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_user, parent, false);
////            }
////            // Lookup view for data population
////            TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
////            TextView tvHome = (TextView) convertView.findViewById(R.id.tvHome);
////            // Populate the data into the template view using the data object
////            tvName.setText(user.name);
////            tvHome.setText(user.hometown);
////            // Return the completed view to render on screen
////            return convertView;
//        }
//    }

}
