package com.prepo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.prepo.ui.adapter.HotelMenuListAdapter;
import com.prepo.view.SlidingTabLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HotelMenuActivity extends AppCompatActivity {

    private static Context _context;
    private String _hotelId;

    ActionBar mActionBar;

    ListView menuList;
    String bookingId;

    ViewPager sm_pager;

    SlidingTabLayout tabs;

    static HashMap<String, ArrayList<ParseObject>> menuListMap = new HashMap<String, ArrayList<ParseObject>>();
    ArrayList<String> categories = new ArrayList<String>();

    MenuCategoryPagerAdapter mMenuCategoryPagerAdapter;


    static String _hotelPic = "";
    static String _hotelName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_menu);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        _context = this;

        Intent intent = getIntent();
        _hotelId = intent.getStringExtra("HotelId");
        _hotelPic = intent.getStringExtra("HotelPic");
        _hotelName = intent.getStringExtra("HotelName");


        ArrayList<ParseObject> hotelMenuInfo = new ArrayList<ParseObject>();
        sm_pager = (ViewPager) findViewById(R.id.hotelsm_pager);

        getHotelMenuItem(_hotelId);

        mActionBar = getSupportActionBar();
        mMenuCategoryPagerAdapter =
                new MenuCategoryPagerAdapter(
                        getSupportFragmentManager());

        tabs = (SlidingTabLayout) findViewById(R.id.hotelmenutabs);
        tabs.setDistributeEvenly(true);
        //tabs.setOnPageChangeListener(page_listener);

        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.reserve_gold);
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
        actionbar_name.setText(_hotelName + " Menu");
    }

    private void getHotelMenuItem(String hotelId) {
        ParseQuery<ParseObject> hotelItemQuery = ParseQuery.getQuery("HotelMenu");
        ParseObject hotel = ParseObject.createWithoutData("Hotel", hotelId);
        hotelItemQuery.whereEqualTo("Hotel", hotel);

        Log.d("SHARED MENU Hotel ", hotelId);


        hotelItemQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> hotelItemList, ParseException e) {
                if (e == null) {

                    Log.d("SHARED MENU Cat Size", hotelItemList.size() + " ");

                    for (int i = 0; i < hotelItemList.size(); ++i) {

                        ParseObject menuItem = hotelItemList.get(i);

                        if (menuListMap.containsKey(menuItem.getString("Category"))) {
                            Log.d("SHARED MENU Cat OLD", menuItem.getString("Category"));
                            String category = menuItem.getString("Category");
                            ParseObject hotelObj = hotelItemList.get(i);

                            if (!menuListMap.get(category).contains(hotelObj))
                                menuListMap.get(category).add(hotelObj);

                            if (!categories.contains(category))
                                categories.add(category);
                        } else {
                            ArrayList<ParseObject> obj = new ArrayList<ParseObject>();
                            String category = menuItem.getString("Category");
                            obj.add(hotelItemList.get(i));
                            menuListMap.put(category, obj);

                            Log.d("SHARED MENU Cat", category);

                            categories.add(category);
                        }

                        String objId = hotelItemList.get(i).getObjectId();


                    }

                    sm_pager.setAdapter(mMenuCategoryPagerAdapter);
                    tabs.setViewPager(sm_pager);


                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
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

            Intent hotel_profile_intent = new Intent(this, HotelProfileActivity.class);
            hotel_profile_intent.putExtra("HotelId", _hotelId);
            NavUtils.navigateUpTo(this, hotel_profile_intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Since this is an object collection, use a FragmentStatePagerAdapter,
// and NOT a FragmentPagerAdapter.
    public class MenuCategoryPagerAdapter extends FragmentStatePagerAdapter {
        public MenuCategoryPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment = new MenuCategoryFragment();
            Bundle args = new Bundle();
            // Our object is just an integer :-P
            Log.d("SHARED MENU SIZE", categories.size() + " ");
            if (categories.size() > 0) {
                args.putString(MenuCategoryFragment.ARG_CAT, categories.get(i));
                fragment.setArguments(args);
            }

            return fragment;
        }

        @Override
        public int getCount() {
            return categories.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return categories.get(position);
        }
    }

    // Instances of this class are fragments representing a single
    // object in our collection.
    public static class MenuCategoryFragment extends Fragment {
        public static final String ARG_CAT = "category";

        HotelMenuListAdapter mc_menuAdapter;
        ListView mc_menu_list;

        @Override
        public View onCreateView(LayoutInflater inflater,
                                 ViewGroup container, Bundle savedInstanceState) {
            // The last two arguments ensure LayoutParams are inflated
            // properly.
            View rootView = inflater.inflate(
                    R.layout.fragment_menu_tab_view, container, false);
            Bundle args = getArguments();
            String category = args.getString(ARG_CAT);

            setImage(rootView, _hotelName, _hotelPic);

            mc_menuAdapter = new HotelMenuListAdapter(_context, 0, menuListMap.get(category), null);
            mc_menu_list = (ListView) rootView.findViewById(R.id.tab_listViewMenu);

            if (mc_menuAdapter != null)
                mc_menu_list.setAdapter(mc_menuAdapter);
            else
                Log.d("SharedMenu ",  "mc_menuAdapter is null");

            return rootView;
        }

        private void setImage(View rootView, String hotelName, String pic) {
            View main_header_view = rootView.findViewById(R.id.menu_tab_header);
            ImageView header_image_view = (ImageView) main_header_view.findViewById(R.id.background_image);
            Picasso.with(_context).load(pic).resize(400, 300)
                    .centerCrop().into(header_image_view);

            main_header_view.findViewById(R.id.cost_text_view).setVisibility(View.VISIBLE);

            TextView header_text_view = (TextView) main_header_view.findViewById(R.id.centered_text_view);
            header_text_view.setText(hotelName);
        }
    }

}
