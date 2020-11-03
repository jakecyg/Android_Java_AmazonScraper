package com.example.amazonscraper;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class MainActivity extends AppCompatActivity {
    TextView textView;
    ListView listView;
    public static final String SHARED_PREF = "sharedPrefs";
    ArrayList<String> arrayList;
    String[] tt = {"One","two","tjree","fpir","give","six","seven","ekgjt","nine","tne","eleven","twev;ev","thirteen","furteen"};
    String[] bt = {"sdfsd","asfdase","awefawef","wefasf","awefawef","zxcvxcv","etnrtn","ea5rherh","xzfvzd","aerherg","awraweg","awfaewf;ev","awegf","aeherg"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListViewAdapter adatper = new ListViewAdapter(MainActivity.this, tt, bt);
//        listView.setAdapter(adatper);
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
}