package com.memory_athlete.memoryassistant.lessons;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.memory_athlete.memoryassistant.Helper;
import com.memory_athlete.memoryassistant.R;
import com.memory_athlete.memoryassistant.mySpace.MySpace;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Objects;

import timber.log.Timber;

import static com.memory_athlete.memoryassistant.Helper.clickableViewAnimation;

/**
 * Created by Manik on 14/04/17.
 */

public class Lessons extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        theme(intent);

        StringBuilder sb = new StringBuilder();   // Empty string used to distinguish from null
                                                    // that might be returned after file is read
        int fileInt = intent.getIntExtra("file", 0);
        if (fileInt != 0 && intent.getBooleanExtra("resource", true)) { // Raw

            if (intent.getBooleanExtra("list", false)) {                // list
                ListView listView = findViewById(R.id.lesson_list);
                ArrayList<Item> list = readResourceList(fileInt);
                Timber.v("list length = %s", list.size());
                LessonAdapter lessonAdapter = new LessonAdapter(this, list);
                Timber.v("adapter set");
                listView.setAdapter(lessonAdapter);
                listView.setVisibility(View.VISIBLE);
                Timber.v("listView set");
                return;
            }

            sb = readResource(fileInt);                                                 // non-list
        } else {                                                                        // Asset

            if (intent.getBooleanExtra("list", false)) {             // list
                ListView listView = findViewById(R.id.lesson_list);
                ArrayList<Item> list = readAssetList(intent);
                //Timber.v("list length = " + list.size());
                LessonAdapter lessonAdapter = new LessonAdapter(this, list);
                Timber.v("adapter set");
                listView.setAdapter(lessonAdapter);
                listView.setVisibility(View.VISIBLE);
                Timber.v("listView set");
                return;
            }

            sb = readAsset(sb, intent);                                                 // non-list
        }

        Objects.requireNonNull(sb );                // null if error in reading file

        if (intent.getBooleanExtra("webView", false)) {             // JQMath
            setWebView(sb);
        } else {
            Timber.v("list is false");
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.visit_my_space:
                startActivity(new Intent(this, MySpace.class));
                break;
            case android.R.id.home:
        }
        return super.onOptionsItemSelected(item);
    }

    protected void theme(Intent intent) {
        String theme = PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.theme), "AppTheme");
        switch (theme) {
            case "Dark":
                setTheme(R.style.dark);
                break;
            case "Night":
                setTheme(R.style.pitch);
                (this.getWindow().getDecorView()).setBackgroundColor(0xff000000);
                break;
            default:
                setTheme(R.style.light);
        }
        int header = intent.getIntExtra("mHeader", 0);
        if (header != 0)
            setTitle(getString(header));
        else setTitle(intent.getStringExtra("headerString"));

        Timber.v("theme = %s", theme);
        setContentView(R.layout.activity_lesson);
    }

    @SuppressLint("SetJavaScriptEnabled")
    void setWebView(StringBuilder sb) {
        WebView webView = findViewById(R.id.web_view);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        StringBuilder sb1 = readResource(R.raw.jqmath_beg);
        if (sb1 == null) {
            Timber.e("jqmath read error");
            finish();
            return;
        }
        StringBuilder sb2 = readResource(R.raw.jqmath_end);
        if (sb2 == null) {
            Timber.e("jqmath read error");
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

    ArrayList<Item> readResourceList(int path) {
        Timber.v("readResource(int, Intent) entered");
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

    ArrayList<Item> readAssetList(Intent intent) {
        Timber.v("readAssetList() entered");
        String header = "", line = intent.getStringExtra("fileString"); //For assets and filesDir
        if (line == null || line.equals("")) return null;
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        ArrayList<Item> letterList = new ArrayList<>();
        try {
            reader = new BufferedReader(new InputStreamReader(getAssets().open(line)));
            while ((line = reader.readLine()) != null) {
                if (line.length() > 6 && line.charAt(0) == '<' && line.charAt(1) == 'h') {
                    letterList.add(new Item(header, sb.toString()));
                    header = line;
                    sb = new StringBuilder();
                } else sb.append(line);
            }
            letterList.add(new Item(header, sb.toString()));
            try {
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return letterList;

        } catch (IOException e) {
            Toast.makeText(this, "Try again", Toast.LENGTH_SHORT).show();
            finish();
        }
        try {
            if (reader != null)
                reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    StringBuilder readAsset(StringBuilder sb, Intent intent) {
        String line = intent.getStringExtra("fileString"); //For assets and filesDir
        BufferedReader bufferedReader = null;
        try {
            assert line != null;
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
            Timber.v("LessonAdapter() entered");
        }

        @SuppressLint("InflateParams")
        @NonNull
        @Override
        public View getView(final int position, View listItemView, @NonNull final ViewGroup parent) {
            Timber.v("getView() entered");
            if (listItemView == null)
                listItemView = LayoutInflater.from(getContext()).inflate(
                        R.layout.item_lesson_list, null, true);

            final Item item = getItem(position);
            final TextView textView1 = listItemView.findViewById(R.id.lesson_item_body);
            final View progressBar = listItemView.findViewById(R.id.lesson_list_progress_bar);
            TextView textView = listItemView.findViewById(R.id.lesson_item_header_text);
            View headerLayout = listItemView.findViewById(R.id.lesson_item_header_layout);

            clickableViewAnimation(headerLayout, getContext(), Helper.ClickableType.SHORT);
            clickableViewAnimation(textView1, getContext(), Helper.ClickableType.LONG);

            if (position == 0) textView1.setMovementMethod(LinkMovementMethod.getInstance());
            else {
                textView1.setVisibility(View.GONE);
                textView1.setOnLongClickListener(view -> {
                    textView1.setVisibility(View.GONE);
                    return false;
                });
            }
            assert item != null;
            Timber.v("mHeader = %s", item.mHeader);

            if (item.mHeader == null || item.mHeader.equals("")) {
                headerLayout.setVisibility(View.GONE);
                textView1.setText(Html.fromHtml("<font color=#111111>"
                        + item.mText + "</font>"));
                textView1.setVisibility(View.VISIBLE);
                Timber.v("body = %s", item.mText);
            } else {
                textView.setText(Html.fromHtml("<font color=#000000>"
                        + item.mHeader + "</font>"));
                headerLayout.setVisibility(View.VISIBLE);
                headerLayout.setOnClickListener(view -> {
                    if (textView1.getVisibility() == View.GONE) {
                        progressBar.setVisibility(View.VISIBLE);
                        textView1.post(() -> {
                            Timber.v("mText = %s", item.mText);
                            textView1.setText(Html.fromHtml("<font color=#111111>"
                                    + item.mText + "</font>"));
                            progressBar.setVisibility(View.GONE);
                            textView1.setVisibility(View.VISIBLE);
                            ((ListView) parent).smoothScrollToPosition(position);
                        });
                    } else textView1.setVisibility(View.GONE);
                });
            }

            return listItemView;
        }
    }
}
