package com.experiences.projects.booktable;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.app.Activity;
import android.app.ExpandableListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.experiences.projects.booktable.view.SlidingTabLayout;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SharedMenu extends ActionBarActivity {

    static Context context;
    private HotelMenuListAdapter hotelItemAdapter;
    private ArrayAdapter<String> friendListAdapter;
    private static HashMap<String, Integer> itemQtyList;

    String bookingId;

//    ListView menuList;
    ListView menuList;
    ActionBar smActionBar;

    ViewPager sm_pager;

    SlidingTabLayout tabs;

    static HashMap<String, ArrayList<ParseObject>> menuListMap = new HashMap<String, ArrayList<ParseObject>>();


    MenuCategoryPagerAdapter mMenuCategoryPagerAdapter;


    ArrayList<String> categories = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_menu);


        final SharedPreferences myPref = getSharedPreferences("MyPref", context.MODE_PRIVATE);

        String hotelid = myPref.getString("HotelId", "");
        bookingId = myPref.getString("BookingId", "");


        context = this;

        //setImage(hotel_name, hotel_url);

        ArrayList<ParseObject> hotelMenuInfo = new ArrayList<ParseObject>();
        itemQtyList = new HashMap<String, Integer>();

//        hotelItemAdapter = new HotelMenuListAdapter(context, 0, hotelMenuInfo, itemQtyList);
//        menuList = (ListView) findViewById(R.id.listViewMenu);
//        menuList.setAdapter(hotelItemAdapter);

        sm_pager = (ViewPager) findViewById(R.id.sm_pager);

        getHotelMenuItem(hotelid);

        smActionBar = getSupportActionBar();
        smActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // ViewPager and its adapters use support library
        // fragments, so use getSupportFragmentManager.
        mMenuCategoryPagerAdapter =
                new MenuCategoryPagerAdapter(
                        getSupportFragmentManager());

        ViewPager.OnPageChangeListener page_listener = new ViewPager.OnPageChangeListener() {

            /**
             * on swipe select the respective tab
             * */
            @Override
            public void onPageSelected(int position) {
                smActionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        };

        sm_pager.setOnPageChangeListener(page_listener);


        tabs = (SlidingTabLayout) findViewById(R.id.menutabs);
        tabs.setDistributeEvenly(true);
        tabs.setOnPageChangeListener(page_listener);

        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.reserve_gold);
            }
        });


        // Setting the ViewPager For the SlidingTabsLayout





        //assignActionstoView();
    }


    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    private void addBookingMenu(View v) {
        for (int i=0; i < menuList.getCount(); ++i) {
            ParseObject userbooking = new ParseObject("UserBooking");
            userbooking.setObjectId(bookingId);

            if (itemQtyList.get(i) > 0) {
                ParseObject userOrder = new ParseObject("UserOrder");
                userOrder.put("OrderedUser", ParseUser.getCurrentUser());
                userOrder.put("Booking", userbooking); //@TODO: check key
                userOrder.put("Item", hotelItemAdapter.getItem(i));
                userOrder.put("Qty", itemQtyList.get(i));
                userOrder.saveInBackground();
            }
        }

        Intent homeIntent = new Intent(context, TimePrefActivity.class);
        context.startActivity(homeIntent);
    }

    private void assignActionstoView() {
        Button btnAddPerson = (Button) findViewById(R.id.sm_btn_add_person);
        Button btnConfirmMenu = (Button) findViewById(R.id.sm_btn_confirm_menu);

        btnConfirmMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBookingMenu(v);
            }
        });



        btnAddPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText personMail = (EditText) v.findViewById(R.id.sm_et_frnd_search);

                if (isValidEmail(personMail.getText())) {
                    ParseQuery<ParseUser> userquery = ParseUser.getQuery();
                    userquery.whereEqualTo("username", personMail.getText());

                    userquery.getFirstInBackground(new GetCallback<ParseUser>() {
                        public void done(final ParseUser user, ParseException e) {
                            if (user == null) {
                                ParseUser newuser = new ParseUser();
                                newuser.setUsername(personMail.getText().toString());
                                newuser.setPassword("AwesomeUser@salt");
                                newuser.signUpInBackground(new SignUpCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            addUserToShareList(user);
                                            friendListAdapter.insert(personMail.getText().toString(), friendListAdapter.getCount());
                                            friendListAdapter.notifyDataSetChanged();
                                        } else {
                                            Log.d("SHAREDMENU", e.getMessage());
                                        }
                                    }
                                });
                            } else {
                                addUserToShareList(user);
                            }
                        }
                    });
                }
            }
        });
    }

    private void addUserToShareList(final ParseUser user) {
        ParseQuery<ParseObject> bookingQuery = ParseQuery.getQuery("UserBooking");
        bookingQuery.getInBackground(bookingId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject booking, ParseException e) {
                if (e == null) {
                    ParseRelation peopleComing = booking.getRelation("PeopleComing");
                    peopleComing.add(user);
                    booking.saveInBackground();
                } else {
                    Log.d("SHAREDMENU", e.getMessage());
                }
            }
        });
    }

    private void getHotelMenuItem(String hotelId) {
        ParseQuery<ParseObject> hotelItemQuery = ParseQuery.getQuery("HotelMenu");
        ParseObject hotel = new ParseObject("Hotel");
        hotel.setObjectId(hotelId);
        hotelItemQuery.whereEqualTo("Hotel", hotel);

        Log.d("SHARED MENU Hotel ", hotelId);


        hotelItemQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> hotelItemList, ParseException e) {
                if (e == null) {

                    Log.d("SHARED MENU Cat Size", hotelItemList.size() + " ");

                    for (int i = 0; i < hotelItemList.size(); ++i) {

                        ParseObject menuItem = hotelItemList.get(i);

                        if ( menuListMap.containsKey(menuItem.getString("Category")) ) {
                            Log.d("SHARED MENU Cat OLD", menuItem.getString("Category"));
                            menuListMap.get(menuItem.getString("Category")).add(hotelItemList.get(i));
                        } else {
                            ArrayList<ParseObject> obj = new ArrayList<ParseObject>();
                            String category = menuItem.getString("Category");
                            obj.add(hotelItemList.get(i));
                            menuListMap.put(category, obj);

                            Log.d("SHARED MENU Cat", category);

                            categories.add(category);

                            smActionBar.addTab(smActionBar.newTab().setText(category)
                                    .setTabListener(new ActionBar.TabListener() {
                                        @Override
                                        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {

                                            sm_pager.setCurrentItem(tab.getPosition());
                                        }

                                        @Override
                                        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

                                        }

                                        @Override
                                        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

                                        }
                                    }));
                        }

                        String objId = hotelItemList.get(i).getObjectId();

                        itemQtyList.put(objId, 0);


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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_shared_menu, menu);
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
            return "OBJECT " + (position + 1);
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

            final SharedPreferences myPref = context.getSharedPreferences("MyPref", context.MODE_PRIVATE);

            String hotel_name = myPref.getString("HotelName", "");
            String hotel_url = myPref.getString("HotelImageUrl", "");

            setImage(rootView, hotel_name, hotel_url);

            mc_menuAdapter = new HotelMenuListAdapter(context, 0, menuListMap.get(category), itemQtyList);
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
            Picasso.with(context).load(pic).resize(400, 300)
                    .centerCrop().into(header_image_view);

            main_header_view.findViewById(R.id.cost_text_view).setVisibility(View.VISIBLE);

            TextView header_text_view = (TextView) main_header_view.findViewById(R.id.centered_text_view);
            header_text_view.setText(hotelName);
        }
    }


}
