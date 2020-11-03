package com.example.amazonscraper;

import android.content.Context;
import android.view.LayoutInflater;

public class ListViewAdapter {
    Context context;
    LayoutInflater layoutInflater;
    String[] tt;
    String[] bt;

    public ListViewAdapter(Context c, String[] tt, String[] bt){
        context = c;
        this.tt = tt;
        this.bt = bt;
    }


}
