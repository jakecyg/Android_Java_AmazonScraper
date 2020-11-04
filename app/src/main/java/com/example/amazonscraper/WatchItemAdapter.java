package com.example.amazonscraper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.amazonscraper.R;
import com.example.amazonscraper.WatchItem;
import java.util.ArrayList;

//custom ArrayAdapter for our ListView
public class WatchItemAdapter extends ArrayAdapter<WatchItem> {

    private ArrayList<WatchItem> items;
    public Context c;
    public WatchItemAdapter(Context context, int textViewResourceId, ArrayList<WatchItem> items) {
        super(context, textViewResourceId, items);
        this.items = items;
        this.c = context;

    }

    //This method is called once for every item in the ArrayList as the list is loaded.
    //It returns a View -- a list item in the ListView -- for each item in the ArrayList
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.watchitem_listview, null);
        }
        WatchItem o = items.get(position);
        if (o != null) {
            TextView tt = (TextView) v.findViewById(R.id.toptext);
            TextView bt = (TextView) v.findViewById(R.id.bottomtext);
            if (tt != null) {
                tt.setText("Price: " + o.getPrice());
            }
            if (bt != null) {
                bt.setText("Link: " + o.getUrl());
            }
        }
        return v;
    }
}