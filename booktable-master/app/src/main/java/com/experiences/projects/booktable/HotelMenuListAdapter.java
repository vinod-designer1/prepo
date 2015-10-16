package com.experiences.projects.booktable;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.parse.ParseObject;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by thinkplaces on 09/09/15.
 */
public class HotelMenuListAdapter extends ArrayAdapter<ParseObject> {

    public HashMap<String, Integer> item_qty_list;
    public int total_cost = 0;

    Context mcontext;

    public HotelMenuListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);

        mcontext = context;
    }

    public HotelMenuListAdapter(Context context, int resource, List<ParseObject> items) {
        super(context, resource, items);

        mcontext = context;
    }

    public HotelMenuListAdapter(Context context, int resource, List<ParseObject> items, HashMap<String, Integer> itemqtylist) {
        super(context, resource, items);

        item_qty_list = itemqtylist;
        mcontext = context;

        Log.d("Hotel Menu List ",  itemqtylist.size() + " " + items.size());
    }

    private SpannableString getTextViewStyle(String view_text) {
        SpannableString text = new SpannableString(view_text);


        text.setSpan(new TextAppearanceSpan(mcontext, R.style.ReqTableSubTextView), 0, view_text.indexOf('\n'), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        text.setSpan(new TextAppearanceSpan(mcontext, R.style.ReqTableTextView),  view_text.indexOf('\n'), view_text.indexOf('\n', view_text.indexOf('\n')+1), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        text.setSpan(new TextAppearanceSpan(mcontext, R.style.ReqTableSubTextView), view_text.indexOf('\n', view_text.indexOf('\n')+1), view_text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return text;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.menuitem, null);
        }

        final ParseObject p = (ParseObject) getItem(position);

        if (p != null) {
            final TextView tv_item_name = (TextView) v.findViewById(R.id.menu_item_name);
            tv_item_name.setText(p.getString("Item"));
            Log.d("HotelMenu ", p.getString("Item"));

            final TextView tv_item_desc = (TextView) v.findViewById(R.id.menu_item_desc);
            tv_item_desc.setText(p.get("LongDescription").toString());
            Log.d("HotelMenu ", p.get("LongDescription").toString());

            final TextView tv_qty = (TextView) v.findViewById(R.id.menu_qty_text);

            final int price = p.getInt("Price");
            Log.d("HotelMenu ", price + " ");

            int item_qty = item_qty_list.get(p.getObjectId());

            String myText = price + "\n" + item_qty + "\n" + (item_qty*price);
            tv_qty.setText(getTextViewStyle(myText), TextView.BufferType.SPANNABLE);



            ImageButton menu_down_btn = (ImageButton) v.findViewById(R.id.menu_down_button);
            menu_down_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("MenuAdap " , item_qty_list.keySet().toString());
                    int item_qty = item_qty_list.get(p.getObjectId());

                    if (item_qty > 0) {
                        item_qty -= 1;
                        total_cost -= price;
                    }

                    Log.d("MenuAdap", "Item Down " + item_qty);


                    item_qty_list.put(p.getObjectId(), item_qty);

                    String myText = price + "\n" + item_qty + "\n" + (item_qty*price);

                    tv_qty.setText(getTextViewStyle(myText), TextView.BufferType.SPANNABLE);

//                    SharedMenu menuActivity = (SharedMenu) mcontext;
//
//                    View main_header_view = menuActivity.findViewById(R.id.menu_header);
//                    TextView cost_view = (TextView) main_header_view.findViewById(R.id.cost_text_view);
//                    cost_view.setText(String.valueOf(total_cost));
                }
            });

            ImageButton menu_up_btn = (ImageButton) v.findViewById(R.id.menu_up_button);
            menu_up_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int item_qty = item_qty_list.get(p.getObjectId());

                    item_qty += 1;

                    total_cost += price;

                    Log.d("MenuAdap", "Item Up " + item_qty);

                    item_qty_list.put(p.getObjectId(), item_qty);

                    String myText = price + "\n" + item_qty + "\n" + (item_qty*price);

                    tv_qty.setText(getTextViewStyle(myText), TextView.BufferType.SPANNABLE);

                    SharedMenu menuActivity = (SharedMenu) mcontext;

//                    View main_header_view = menuActivity.findViewById(R.id.menu_header);
//                    TextView cost_view = (TextView) main_header_view.findViewById(R.id.cost_text_view);
//                    cost_view.setText(String.valueOf(total_cost));
                }
            });
        }

        return v;
    }

}