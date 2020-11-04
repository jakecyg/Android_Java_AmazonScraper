package com.example.amazonscraper;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class MainActivity extends AppCompatActivity {
    TextView textView;
    ListView listView;
    public static final String SHARED_PREF = "sharedPrefs";
    private static final String KEY_PRICE_BEFORE_DISCOUNT = "price_before_discount";
    private static final String KEY_PRICE_AFTER_DISCOUNT = "price_after_discount";
    private static final String KEY_ITEM_URL = "item_url";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<String> urlInDbList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.watchItem_list);
        //life saver code!! finally get to scroll in my nestedScrollview!!
        ViewCompat.setNestedScrollingEnabled(listView, true);
        WatchItem watchItem1 = new WatchItem("title","1", "qwe.com");
        WatchItem watchItem2 = new WatchItem("2222","2222", "qwe.com");
        ArrayList<WatchItem> watchItemArrayList = new ArrayList<>();
        watchItemArrayList.add(watchItem1);
        watchItemArrayList.add(watchItem1);
        watchItemArrayList.add(watchItem1);
        watchItemArrayList.add(watchItem1);
        watchItemArrayList.add(watchItem1);
        watchItemArrayList.add(watchItem1);
        watchItemArrayList.add(watchItem1);
        watchItemArrayList.add(watchItem1);
        watchItemArrayList.add(watchItem2);
        watchItemArrayList.add(watchItem1);
        watchItemArrayList.add(watchItem1);
        watchItemArrayList.add(watchItem1);
        watchItemArrayList.add(watchItem1);
        watchItemArrayList.add(watchItem1);
        watchItemArrayList.add(watchItem1);
        watchItemArrayList.add(watchItem1);
        watchItemArrayList.add(watchItem1);
        watchItemArrayList.add(watchItem1);
        WatchItemAdapter watchItemAdapter = new WatchItemAdapter(this, R.layout.watchitem_listview, watchItemArrayList);

        listView.setAdapter(watchItemAdapter);

    }

    //called when a url is entered and the get price button is clicked
    public void getPrice(View view) {
        TextView urlText = findViewById(R.id.urlText);
        if(urlText.length() == 0)
            Toast.makeText(this, "Please enter a URL", Toast.LENGTH_SHORT).show();

        else {
            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            String urlToPass = urlText.getText().toString();
            editor.putString("url", urlToPass);
            editor.apply();
            AmazonScrape amazonScrape = new AmazonScrape(getApplicationContext());
            amazonScrape.execute();
        }
    }

    //Allows direct paste from clipboard; saves hassel of long click => paste
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void pasteFromClipBoard(View view) {
        //get clipboard manager
        ClipData clip= getSystemService(ClipboardManager.class).getPrimaryClip();

        //get primary clip element
        String test = clip.getItemAt(0).getText().toString();

        //convert to string
        TextView urlText = findViewById(R.id.urlText);

        //populate url text field
        urlText.setText(test);
    }

    //Clear url input box(saves hassel of holding the delete key)
    public void clearText(View view) {
        TextView urlText = findViewById(R.id.urlText);
        urlText.setText("");
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        //get all urls in all documents and store them in list
        getUrls();
        for(String url : urlInDbList){
            AmazonScrape amazonScrape = new AmazonScrape(MainActivity.this, url);
            amazonScrape.execute();
        }
    }

    private void getUrls() {
        CollectionReference notOnSaleRef = db.collection("NotOnSale");
        notOnSaleRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();
                        for(DocumentSnapshot doc : documentSnapshotList){
                            if(doc.get(KEY_ITEM_URL) != null){
                                urlInDbList.add(doc.get(KEY_ITEM_URL).toString());
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Error occured. Data not saved to FireBase", Toast.LENGTH_SHORT).show();
                        Log.d("AmazonScrape.java", e.toString());
                    }
                });

        CollectionReference onSaleRef = db.collection("OnSale");
        onSaleRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();
                        for(DocumentSnapshot doc : documentSnapshotList){
                            if(doc.get(KEY_ITEM_URL) != null){
                                urlInDbList.add(doc.get(KEY_ITEM_URL).toString());
                            }
                        }
                        Toast.makeText(MainActivity.this, "Database updated;url count=" + urlInDbList.size(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Error occured. Data not saved to FireBase", Toast.LENGTH_SHORT).show();
                        Log.d("AmazonScrape.java", e.toString());
                    }
                });
    }
}