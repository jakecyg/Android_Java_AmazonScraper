package com.example.amazonscraper;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import static android.content.Context.MODE_PRIVATE;

public class AmazonScrape extends AsyncTask {
    public static final String SHARED_PREF = "sharedPrefs";
    private Context mContext;
    String urlToWatch;
    SharedPreferences sharedPreferences;
    //need to set default constructor to recieve a context from the main
    //in order to access the same sharedPreferences object using the same context
    public AmazonScrape (Context context){
        mContext = context;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Context context = mContext;
        sharedPreferences = context.getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        String url = sharedPreferences.getString("url", "");
        urlToWatch = url;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
    }

    //main method of the async class
    @Override
    protected Object doInBackground(Object[] objects) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        try {
            Document document = Jsoup.connect(urlToWatch).get();
            //no sale item
            Element noDiscount = document.getElementById("priceblock_ourprice");

            //sale item
            Elements onDiscount_BeforeDiscount = document.getElementsByClass("priceBlockStrikePriceString a-text-strike");
            Element  onDiscount_AfterDiscount = document.getElementById("priceblock_dealprice");

            if(noDiscount == null){
                String beforeDiscount = onDiscount_BeforeDiscount.text();
                String formattedBeforeDiscount = beforeDiscount.substring(5);
                String afterDiscount = onDiscount_AfterDiscount.text();
                String formattedAfterDiscount = afterDiscount.substring(5);

                editor.putString("beforeDiscount", formattedBeforeDiscount);
                editor.putString("afterDiscount", formattedAfterDiscount);
                editor.apply();
            }
            else{
                String price = noDiscount.text();
                String formattedPrice = price.substring(5);
                editor.putString("normalPrice", formattedPrice);
                editor.apply();

                editor.putString("noDiscount", formattedPrice);
                editor.apply();
                Log.d("Jake", formattedPrice);
            }


        } catch (IOException e) {
            Log.d("JAKE", "doInBackground: Failed you ididot");
            e.printStackTrace();
        }
        return null;
    }
}