package com.memory_athlete.memoryassistant.lessons;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.compat.BuildConfig;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.memory_athlete.memoryassistant.R;
import com.memory_athlete.memoryassistant.mySpace.MySpace;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import timber.log.Timber;

/**
 * Created by Manik on 14/04/17.
 */

public class Lessons extends AppCompatActivity {
    private static final String LOG_TAG = "\tLessons";
    private static final String TAG = "String builder =";

    ///*final String JQMATH_BEG = "<!DOCTYPE html>" +
      //      "<html>\n" +
        //    "    <head>\n" +
          //  "        <meta charset=\"utf-8\">\n" +
            //"        <link rel=\"stylesheet\" href=\"http://fonts.googleapis.com/css?family=UnifrakturMaguntia\">\n" +
            //"        <link rel=\"stylesheet\" href=\"../jqmath/jqmath-0.4.3.css\">\n" +
            //"        <script src=\"../jqmath/jquery-1.4.3.min.js\"></script>\n" +
            //"        <script src=\"../jqmath/jqmath-etc-0.4.6.min.js\" charset=\"utf-8\"></script>\n" +
            //"    </head>\n" +
            //"    <body>";
    //final String JQMATH_END = "</body>" +
      //      "</html>";*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(BuildConfig.DEBUG) Timber.plant(new Timber.DebugTree());
        Intent intent = getIntent();
        theme(intent);

        StringBuilder sb= new StringBuilder("");
        int fileInt = intent.getIntExtra("file", 0);
        if (fileInt != 0) {
            if (intent.getBooleanExtra("list", false)) {
                ListView listView = (ListView) findViewById(R.id.lesson_list);
                ArrayList<Item> list = readList(fileInt);
                if (BuildConfig.DEBUG) Log.v(LOG_TAG, "list length = " + list.size());
                LessonAdapter lessonAdapter = new LessonAdapter(this, list);
                if (BuildConfig.DEBUG) Log.v(LOG_TAG, "adapter set");
                listView.setAdapter(lessonAdapter);
                listView.setVisibility(View.VISIBLE);
                if (BuildConfig.DEBUG) Log.v(LOG_TAG, "listView set");
                return;
            } else sb = readResource(fileInt);//For raw files
        } else {
            sb = readAsset(sb, intent);
        }
        if (sb == null) {
            Timber.e("String Builder sb is empty");
            finish();
            return;
        }

        if (intent.getBooleanExtra("webView", false)) {     //For JQMath
            setWebView(sb);
        } else {
            if (BuildConfig.DEBUG) Log.v(LOG_TAG, "list is false");
            ((TextView) findViewById(R.id.lesson)).setText(Html.fromHtml(sb.toString()));
            findViewById(R.id.lesson_scroll).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_my_space, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //switch (item.getItemId()) {
        //    case R.id.action_delete:
        startActivity(new Intent(this, MySpace.class));
        return true;
    }

    protected void theme(Intent intent){
        String theme = PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.theme), "AppTheme"), title="";
        switch (theme){
            case "Dark":
                setTheme(R.style.dark);
                break;
            case "Night":
                setTheme(R.style.pitch);
                (this.getWindow().getDecorView()).setBackgroundColor(0xff000000);
                break;
            default:
                setTheme(R.style.light);
                title="<font color=#FFFFFF>";
        }
        int header = intent.getIntExtra("mHeader", 0);
        if (header != 0)
            setTitle(Html.fromHtml(title + getString(header)));
        else setTitle(Html.fromHtml(title + intent.getStringExtra("headerString")));

        if (BuildConfig.DEBUG) Log.v(LOG_TAG, "theme = " + theme);
        setContentView(R.layout.activity_lesson);
    }

    void setWebView(StringBuilder sb){
        WebView webView = (WebView) findViewById(R.id.web_view);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        StringBuilder sb1 = readResource(R.raw.jqmath_beg);
        if (sb1 == null) {
            if (BuildConfig.DEBUG) Log.e(LOG_TAG, "jqmath read error");
            finish();
            return;
        }
        StringBuilder sb2 = readResource(R.raw.jqmath_end);
        if (sb2 == null) {
            if (BuildConfig.DEBUG) Log.e(LOG_TAG, "jqmath read error");
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
    }

    StringBuilder readResource(int path) {
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
            Toast.makeText(this, "Try again", Toast.LENGTH_SHORT).show();
            finish();
            return sb;
        }
    }

    ArrayList<Item> readList(int path) {
        if (BuildConfig.DEBUG) Log.v(LOG_TAG, "readResource(int, Intent) entered");
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
            Toast.makeText(this, "Try again", Toast.LENGTH_SHORT).show();
            finish();
            return null;
        }
    }

    StringBuilder readAsset(StringBuilder sb, Intent intent){
        String line = intent.getStringExtra("fileString"); //For assets and filesDir
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(getAssets().open(line)));
            while ((line = bufferedReader.readLine()) != null) sb.append(line);
        } catch (IOException e) {
            Toast.makeText(this, "Try again", Toast.LENGTH_SHORT).show();
            finish();
        }
        try {
            if (bufferedReader != null)
                bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb;
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
            if (BuildConfig.DEBUG) Log.v(LOG_TAG, "LessonAdapter() entered");
        }

        @Override
        public View getView(int position, View listItemView, ViewGroup parent) {
            if (BuildConfig.DEBUG) Log.v(LOG_TAG, "getView() entered");
            if (listItemView == null) {
                listItemView = LayoutInflater.from(getContext()).inflate(R.layout.lesson_list_item, null, true);
            }

            Item item = getItem(position);
            final TextView textView1 = listItemView.findViewById(R.id.lesson_item_body);
            textView1.setText(Html.fromHtml(item.mText));
            if(position>0) textView1.setVisibility(View.GONE);
            if (BuildConfig.DEBUG) Log.v(LOG_TAG, "mText = \t" + item.mText);
            if (BuildConfig.DEBUG) Log.v(LOG_TAG, "mHeader = \t" + item.mHeader);

            TextView textView = listItemView.findViewById(R.id.lesson_item_header_text);
            View view = listItemView.findViewById(R.id.lesson_item_header_layout);
            if (item.mHeader == null || item.mHeader == "")
                view.setVisibility(View.GONE);
            else {
                textView.setText(Html.fromHtml(item.mHeader));
                view.setOnClickListener(new View.OnClickListener() {
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
