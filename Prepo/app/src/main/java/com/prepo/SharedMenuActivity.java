package com.prepo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.prepo.ui.adapter.HotelMenuListAdapter;
import com.prepo.view.SlidingTabLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SharedMenuActivity extends AppCompatActivity {

    static Context _context;
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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final SharedPreferences myPref = getSharedPreferences("MyPref", _context.MODE_PRIVATE);

        String hotelid = myPref.getString("HotelId", "");
        bookingId = myPref.getString("BookingId", "");

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        _context = this;

        ArrayList<ParseObject> hotelMenuInfo = new ArrayList<ParseObject>();
        itemQtyList = new HashMap<String, Integer>();

        sm_pager = (ViewPager) findViewById(R.id.sm_pager);

        getHotelMenuItem(hotelid);
        setUpActionBar();

        smActionBar = getSupportActionBar();

        mMenuCategoryPagerAdapter =
                new MenuCategoryPagerAdapter(
                        getSupportFragmentManager());

        tabs = (SlidingTabLayout) findViewById(R.id.menutabs);
        tabs.setDistributeEvenly(true);

        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.reserve_gold);
            }
        });

        assignActionstoView();
    }

    private void setUpActionBar() {
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayShowCustomEnabled(true);

        TextView actionbar_name = (TextView) findViewById(R.id.toolbar_title);
        actionbar_name.setText("Menu");
    }


    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    private void addBookingMenu(View v) {
        for (int i=0; i < mMenuCategoryPagerAdapter.getCount(); ++i) {
//            ParseObject userbooking = new ParseObject("UserBooking");
//            userbooking.setObjectId(bookingId);
//
//            if (itemQtyList.get(i) > 0) {
//                ParseObject userOrder = new ParseObject("UserOrder");
//                userOrder.put("OrderedUser", ParseUser.getCurrentUser());
//                userOrder.put("Booking", userbooking); //@TODO: check key
//                userOrder.put("Item", hotelItemAdapter.getItem(i));
//                userOrder.put("Qty", itemQtyList.get(i));
//                userOrder.saveInBackground();
//            }
        }

//        Intent homeIntent = new Intent(_context, TimePrefActivity.class);
//        _context.startActivity(homeIntent);
    }

    private ArrayList getMenuList() {

        ArrayList<HashMap<String, String>> list = new ArrayList<>();

        for (int cati = 0; cati < categories.size(); ++cati) {
            ArrayList<ParseObject> catMenuList = menuListMap.get(categories.get(cati));

            for (int itemi=0; itemi < catMenuList.size(); ++itemi) {
                ParseObject pObj = catMenuList.get(itemi);
                String objI = pObj.getObjectId();


                if (itemQtyList.get(objI) > 0) {
                    HashMap<String, String> ItemInfo = new HashMap<>();
                    ItemInfo.put("ObjectId", objI);
                    ItemInfo.put("Name", pObj.getString("Item"));
                    ItemInfo.put("Qty", ""+itemQtyList.get(objI));

                    list.add(ItemInfo);
                }
            }
        }

        return list;
    }

    private void assignActionstoView() {
//        Button btnAddPerson = (Button) findViewById(R.id.sm_btn_add_person);
//        Button btnConfirmMenu = (Button) findViewById(R.id.menu_continue_button);
//        btnConfirmMenu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                addBookingMenu(v);
//            }
//        });

        Button btnCartMenu = (Button) findViewById(R.id.menu_cart_button);
        btnCartMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<HashMap<String, String>> list = getMenuList();

                Intent cartIntent = new Intent(_context, CartActivity.class);
                cartIntent.putExtra("order", list);
                cartIntent.putExtra("BookingId", bookingId);
                _context.startActivity(cartIntent);
            }
        });




//        btnAddPerson.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final EditText personMail = (EditText) v.findViewById(R.id.sm_et_frnd_search);
//
//                if (isValidEmail(personMail.getText())) {
//                    ParseQuery<ParseUser> userquery = ParseUser.getQuery();
//                    userquery.whereEqualTo("username", personMail.getText());
//
//                    userquery.getFirstInBackground(new GetCallback<ParseUser>() {
//                        public void done(final ParseUser user, ParseException e) {
//                            if (user == null) {
//                                ParseUser newuser = new ParseUser();
//                                newuser.setUsername(personMail.getText().toString());
//                                newuser.setPassword("AwesomeUser@salt");
//                                newuser.signUpInBackground(new SignUpCallback() {
//                                    @Override
//                                    public void done(ParseException e) {
//                                        if (e == null) {
//                                            addUserToShareList(user);
//                                            friendListAdapter.insert(personMail.getText().toString(), friendListAdapter.getCount());
//                                            friendListAdapter.notifyDataSetChanged();
//                                        } else {
//                                            Log.d("SHAREDMENU", e.getMessage());
//                                        }
//                                    }
//                                });
//                            } else {
//                                addUserToShareList(user);
//                            }
//                        }
//                    });
//                }
//            }
//        });
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
        ParseObject hotel = ParseObject.createWithoutData("Hotel", hotelId);
//        hotel.setObjectId(hotelId);
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

//                            smActionBar.addTab(smActionBar.newTab().setText(category)
//                                    .setTabListener(new ActionBar.TabListener() {
//                                        @Override
//                                        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
//
//                                            sm_pager.setCurrentItem(tab.getPosition());
//                                        }
//
//                                        @Override
//                                        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
//
//                                        }
//
//                                        @Override
//                                        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
//
//                                        }
//                                    }));
                        }

                        String objId = hotelItemList.get(i).getObjectId();

                        itemQtyList.put(objId, 0);


                    }

                    Log.d("COUNT MENU ", "" + mMenuCategoryPagerAdapter.getCount());

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
        }  else if (id == android.R.id.home) {
            Intent userBookingIntent = new Intent(this, UserBookingActivity.class);
            userBookingIntent.putExtra("bookingId", bookingId);
            NavUtils.navigateUpTo(this, userBookingIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Since this is an object collection, use a FragmentStatePagerAdapter,
// and NOT a FragmentPagerAdapter.
    public class MenuCategoryPagerAdapter extends FragmentStatePagerAdapter {

        ArrayList<Fragment> menuCategoryFragmentsList;


        public MenuCategoryPagerAdapter(FragmentManager fm) {
            super(fm);

            menuCategoryFragmentsList = new ArrayList<Fragment>();
        }

        @Override
        public Fragment getItem(int i) {

            if (menuCategoryFragmentsList.size() < (i+1)) {
                Fragment fragment = new MenuCategoryFragment();
                Bundle args = new Bundle();
                // Our object is just an integer :-P
                Log.d("SHARED MENU SIZE", categories.size() + " ");
                if (categories.size() > 0) {
                    args.putString(MenuCategoryFragment.ARG_CAT, categories.get(i));

                    fragment.setArguments(args);
                }

                menuCategoryFragmentsList.add(i, fragment);

                return fragment;
            } else {
                return menuCategoryFragmentsList.get(i);
            }


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

            final SharedPreferences myPref = _context.getSharedPreferences("MyPref", _context.MODE_PRIVATE);

            String hotel_name = myPref.getString("HotelName", "");
            String hotel_url = myPref.getString("HotelImageUrl", "");

            setImage(rootView, hotel_name, hotel_url);

            mc_menuAdapter = new HotelMenuListAdapter(_context, 0, menuListMap.get(category), itemQtyList);
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
