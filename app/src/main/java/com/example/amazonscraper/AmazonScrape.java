package com.example.amazonscraper;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class AmazonScrape extends AsyncTask {
    public static final String SHARED_PREF = "sharedPrefs";
    private Context mContext;
    String urlToWatch;
    SharedPreferences sharedPreferences;
    private static final String KEY_TITLE = "title";
    private static final String KEY_PRICE = "price";
    private static final String KEY_PRICE_BEFORE_DISCOUNT = "price_before_discount";
    private static final String KEY_PRICE_AFTER_DISCOUNT = "price_after_discount";
    private static final String KEY_ITEM_URL = "item_url";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String compareURL;
    //need to set default constructor to recieve a context from the main
    //in order to access the same sharedPreferences object using the same context
    public AmazonScrape (Context context){
        mContext = context;
    }
    public AmazonScrape (Context context, String url){
        mContext = context;
        compareURL = url;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(compareURL == null){
            Context context = mContext;
            sharedPreferences = context.getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
            String url = sharedPreferences.getString("url", "");
            urlToWatch = url;
        }
        else{
            urlToWatch = compareURL;
        }

    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
    }

    //main method of the async class
    @Override
    protected Object doInBackground(Object[] objects) {
        if(sharedPreferences != null){
            SharedPreferences.Editor editor = sharedPreferences.edit();
        }
        try {
            Document document = Jsoup.connect(urlToWatch).get();

            //Get all elements needed(cant go straight to String objects because they could be null == crash
            Element itemTitle = document.getElementById("productTitle");
            Element noDiscountItemPrice = document.getElementById("priceblock_ourprice");
            Elements onDiscount_BeforeDiscount = document.getElementsByClass("priceBlockStrikePriceString a-text-strike");
            Element onDiscount_AfterDiscount = document.getElementById("priceblock_dealprice");

            //Item Title
            String itemTitleS = itemTitle.text();


            if(noDiscountItemPrice == null){
                //Discounted item prices(before and after)
                String onDiscount_BeforeDiscountS = onDiscount_BeforeDiscount.text();
                String  onDiscount_AfterDiscountS = onDiscount_AfterDiscount.text();

                //Substring past dollar sign
                String formattedBeforeDiscount = onDiscount_BeforeDiscountS.substring(3);
                String formattedAfterDiscount = onDiscount_AfterDiscountS.substring(3);

                saveToOnSaleFireBase(itemTitleS, formattedBeforeDiscount, formattedAfterDiscount);

            }
            else{
                //noDiscount item price
                String noDiscountItemPriceS = document.getElementById("priceblock_ourprice").text();
                String formattedPrice = noDiscountItemPriceS.substring(3);
                saveToNotOnSaleFireBase(itemTitleS, formattedPrice);
            }

        } catch (IOException e) {
            Log.d("JAKE", "doInBackground: Failed you ididot");
            e.printStackTrace();
        }
        return null;
    }

    public void saveToNotOnSaleFireBase(String itemTitle, String formattedPrice){
        //create container to store in firebase
        Map<String, Object> itemContainer = new HashMap<>();
        itemContainer.put(KEY_TITLE, itemTitle);
        itemContainer.put(KEY_PRICE, formattedPrice);
        itemContainer.put(KEY_ITEM_URL, urlToWatch);

        //pass the map the firestore database
        //create AmazonPrice collection, document and send the key value pair to the document
//        db.document("AmazonPrice/AmazonScraper"); this does the same thing as below; shortcut
        db.collection("NotOnSale").document(itemTitle).set(itemContainer)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(mContext, "Data saved to FireBase", Toast.LENGTH_SHORT).show();
                        Log.d("jake","jere");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext, "Error occured. Data not saved to FireBase", Toast.LENGTH_SHORT).show();
                        Log.d("AmazonScrape.java", e.toString());
                    }
                });
    }
    public void saveToOnSaleFireBase(String itemTitle, String onDiscount_BeforeDiscount , String onDiscount_AfterDiscount){
        //create container to store in firebase
        Map<String, Object> itemContainer = new HashMap<>();
//        priceObject.put(KEY_TITLE, itemTitle);
        itemContainer.put(KEY_TITLE, itemTitle);
        itemContainer.put(KEY_PRICE_BEFORE_DISCOUNT, onDiscount_BeforeDiscount);
        itemContainer.put(KEY_PRICE_AFTER_DISCOUNT, onDiscount_AfterDiscount);
        itemContainer.put(KEY_ITEM_URL, urlToWatch);

        //pass the map the firestore database
        //create AmazonPrice collection, document and send the key value pair to the document
//        db.document("AmazonPrice/AmazonScraper"); this does the same thing as below; shortcut
        db.collection("OnSale").document(itemTitle).set(itemContainer)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(mContext, "Data saved to FireBase", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext, "Error occured. Data not saved to FireBase", Toast.LENGTH_SHORT).show();
                        Log.d("AmazonScrape.java", e.toString());
                    }
                });
    }
}