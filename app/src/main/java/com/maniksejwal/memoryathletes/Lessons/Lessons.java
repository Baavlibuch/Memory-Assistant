package com.maniksejwal.memoryathletes.lessons;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.maniksejwal.memoryathletes.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Manik on 14/04/17.
 */

public class Lessons extends AppCompatActivity {
    private static final String LOG_TAG = "\tLog : Lessons : ";
    private static final String TAG = "String builder =";

    /*final String JQMATH_BEG = "<!DOCTYPE html>" +
            "<html>\n" +
            "    <head>\n" +
            "        <meta charset=\"utf-8\">\n" +
            "        <link rel=\"stylesheet\" href=\"http://fonts.googleapis.com/css?family=UnifrakturMaguntia\">\n" +
            "        <link rel=\"stylesheet\" href=\"../jqmath/jqmath-0.4.3.css\">\n" +
            "        <script src=\"../jqmath/jquery-1.4.3.min.js\"></script>\n" +
            "        <script src=\"../jqmath/jqmath-etc-0.4.6.min.js\" charset=\"utf-8\"></script>\n" +
            "    </head>\n" +
            "    <body>";
    final String JQMATH_END = "</body>" +
            "</html>";*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson);
        Intent intent = getIntent();
        int header = intent.getIntExtra("mHeader", 0);
        if (header != 0)
            setTitle(getString(header));
        else setTitle(intent.getStringExtra("headerString"));

        StringBuilder sb;
        int fileInt = intent.getIntExtra("file", 0);
        if (fileInt != 0) {
            if (intent.getBooleanExtra("list", false)) {
                ListView listView = (ListView) findViewById(R.id.lesson_list);
                ArrayList<Item> list = readFile(fileInt, intent);
                Log.v(LOG_TAG, "list length = " + list.size());
                LessonAdapter lessonAdapter = new LessonAdapter(this, list);
                Log.v(LOG_TAG, "adapter set");
                listView.setAdapter(lessonAdapter);
                listView.setVisibility(View.VISIBLE);
                Log.v(LOG_TAG, "listView set");
                return;
            } else sb = readFile(fileInt);//For raw files
        } else {
            String s = intent.getStringExtra("fileString"); //For assets and filesDir
            BufferedReader bufferedReader = null;
            sb = new StringBuilder("");
            try {
                bufferedReader = new BufferedReader(new InputStreamReader(getAssets().open(s)));
                while ((s = bufferedReader.readLine()) != null) sb.append(s);
            } catch (IOException e) {
                Toast.makeText(this, "Couldn't open the file", Toast.LENGTH_SHORT).show();
            }
            try {
                if (bufferedReader != null)
                    bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (sb == null) {
            Log.w(LOG_TAG, "String Builder sb is empty");
            finish();
            return;
        }

        if (intent.getBooleanExtra("webView", false)) {     //For JQMath
            WebView webView = (WebView) findViewById(R.id.web_view);
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            StringBuilder sb1 = readFile(R.raw.jqmath_beg);
            if (sb1 == null) {
                Log.e(LOG_TAG, "jqmath read error");
                finish();
                return;
            }
            StringBuilder sb2 = readFile(R.raw.jqmath_end);
            if (sb2 == null) {
                Log.e(LOG_TAG, "jqmath read error");
                finish();
                return;
            }
            String js = sb1.toString() + sb.toString() + sb2.toString();
            //String js = JQMATH_BEG + "asfd $$u_n/v_n$$" + JQMATH_END;
            //webView.loadUrl("file:///android_asset/jqmath/index.html");
            //((TextView) findViewById(R.id.lesson)).setText(js);
            //findViewById(R.id.lesson_scroll).setVisibility(View.VISIBLE);

            webView.loadDataWithBaseURL("file:///android_asset/jqmath/", js, "mText/html", "UTF-8", null);
            webView.setVisibility(View.VISIBLE);
        } else {
            Log.v(LOG_TAG, "list is false");
            ((TextView) findViewById(R.id.lesson)).setText(Html.fromHtml(sb.toString()));
            findViewById(R.id.lesson_scroll).setVisibility(View.VISIBLE);
        }
    }

    StringBuilder readFile(int path) {
        if (path == 0) return null;
        BufferedReader reader = null;
        String line;
        StringBuilder sb = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(getResources().openRawResource(path)));
            while ((line = reader.readLine()) != null) {
                sb.append(line);//.append("\n\n");
            }
            try {
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return sb;
        } catch (IOException e) {
            try {
                reader.close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            Toast.makeText(this, "Couldn't open the lesson", Toast.LENGTH_SHORT).show();
            finish();
            return sb;
        }
    }

    ArrayList<Item> readFile(int path, Intent intent) {
        Log.v(LOG_TAG, "readFile(int, Intent) entered");
        if (path == 0) return null;
        BufferedReader reader = null;
        String line, header = "";
        StringBuilder sb = new StringBuilder();
        ArrayList<Item> lessonList = new ArrayList<>();
        try {
            reader = new BufferedReader(new InputStreamReader(getResources().openRawResource(path)));
            while ((line = reader.readLine()) != null) {
                if (line.length() > 6 && line.charAt(0) == '<' && line.charAt(1) == 'h') {
                    lessonList.add(new Item(header, sb.toString()));
                    header = line;
                    sb = new StringBuilder();
                } else sb.append(line);
            }
            lessonList.add(new Item(header, sb.toString()));
            try {
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return lessonList;
        } catch (IOException e) {
            try {
                reader.close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            Toast.makeText(this, "Couldn't open the lesson", Toast.LENGTH_SHORT).show();
            finish();
            return null;
        }
    }

    private class Item {
        String mHeader, mText;

        Item(String header, String text) {
            mHeader = header;
            mText = text;
        }
    }

    private class LessonAdapter extends ArrayAdapter<Item> {

        LessonAdapter(Activity context, ArrayList<Item> list) {
            super(context, 0, list);
            Log.v(LOG_TAG, "LessonAdapter() entered");
        }

        @Override
        public View getView(int position, View listItemView, ViewGroup parent) {
            Log.v(LOG_TAG, "getView() entered");
            if (listItemView == null) {
                listItemView = LayoutInflater.from(getContext()).inflate(R.layout.lesson_list_item, null, true);
            }

            Item item = getItem(position);
            final TextView textView1 = listItemView.findViewById(R.id.lesson_item_body);
            textView1.setText(Html.fromHtml(item.mText));
            if(position>0) textView1.setVisibility(View.GONE);
            Log.v(LOG_TAG, "mText = \t" + item.mText);
            Log.v(LOG_TAG, "mHeader = \t" + item.mHeader);

            TextView textView = listItemView.findViewById(R.id.lesson_item_header);
            if (item.mHeader == null || item.mHeader == "") textView.setVisibility(View.GONE);
            else {
                textView.setText(Html.fromHtml(item.mHeader));
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (textView1.getVisibility() == View.GONE)
                            textView1.setVisibility(View.VISIBLE);
                        else textView1.setVisibility(View.GONE);
                    }
                });
            }
            return listItemView;
        }
    }
}
