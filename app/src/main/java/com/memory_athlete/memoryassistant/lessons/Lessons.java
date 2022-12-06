package com.memory_athlete.memoryassistant.lessons;

import static com.memory_athlete.memoryassistant.Helper.clickableViewAnimation;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.memory_athlete.memoryassistant.Helper;
import com.memory_athlete.memoryassistant.language.LocaleHelper;
import com.memory_athlete.memoryassistant.R;
import com.memory_athlete.memoryassistant.mySpace.MySpace;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Objects;

import timber.log.Timber;

/**
 * Created by Manik on 14/04/17.
 */

public class Lessons extends AppCompatActivity {
    int textColor;
    int dropDownResId;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        theme(intent);

        // Initialized as an empty string to distinguish from null that might be returned after file is read
        StringBuilder sb = new StringBuilder();

        int fileInt = intent.getIntExtra(Helper.RAW_RESOURCE_ID_KEY, 0);
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

        Objects.requireNonNull(sb);                // null if error in reading file

        if (intent.getBooleanExtra("webView", false)) {             // JQMath
            setWebView(sb);
            return;
        }

        // Display the plain-text/HTML read above into sb in the textView inside the ScrollView
        Timber.v("list is false");
        ((TextView) findViewById(R.id.lesson)).setText(Html.fromHtml(sb.toString()));
        findViewById(R.id.lesson_scroll).setVisibility(View.VISIBLE);
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
        String[] themes = getResources().getStringArray(R.array.themes);
        if (themes[1].equals(theme)) {
            setTheme(R.style.dark);
            textColor = Color.LTGRAY;
            dropDownResId = R.drawable.ic_arrow_drop_down_dark;
        } else if (themes[2].equals(theme)) {
            setTheme(R.style.pitch);
            (this.getWindow().getDecorView()).setBackgroundColor(0xff000000);
            textColor = Color.LTGRAY;
            dropDownResId = R.drawable.ic_arrow_drop_down_pitch;
        } else {
            setTheme(R.style.light);
            textColor = Color.DKGRAY;
            dropDownResId = R.drawable.ic_arrow_drop_down_light;
        }
        int header = intent.getIntExtra("mHeader", 0);
        if (header != 0)
            setTitle(getString(header));
        else setTitle(intent.getStringExtra("headerString"));

        Timber.v("theme = %s", theme);
        try {
            setContentView(R.layout.activity_lesson);
        } catch (Resources.NotFoundException e) {
            Toast.makeText(this, R.string.dl_from_play, Toast.LENGTH_LONG).show();
            Timber.i("resource not found");
            finish();
        }
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
            reader.close();
            return letterList;
        } catch (IOException e) {
            Toast.makeText(this, "Try again", Toast.LENGTH_SHORT).show();
            try {
                if (reader != null) reader.close();
            } catch (IOException e1) {
                Timber.e(e1);
            }
            finish();
        }
        return null;
    }

    StringBuilder readAsset(StringBuilder sb, Intent intent) {
        String line = intent.getStringExtra("fileString"); //For assets and filesDir
        BufferedReader bufferedReader = null;
        try {
            if (line == null) {
                Toast.makeText(getApplicationContext(), R.string.dl_from_play, Toast.LENGTH_LONG).show();
                throw new RuntimeException(
                        "\nAsset_path - " + line + "" +
                                "fileInt = " + intent.getIntExtra(Helper.RAW_RESOURCE_ID_KEY, 0) +
                                "resources_boolean = " + intent.getBooleanExtra("resource", true) +
                                "list_boolean = " + intent.getBooleanExtra("list", false) +
                                "webView_boolean = " + intent.getBooleanExtra("webView", false) +
                                "headerInt = " + intent.getIntExtra("mHeader", 0) +
                                "headerString" + intent.getStringExtra("headerString"));
            }

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
            throw new RuntimeException("buffer not closed", e);
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
            try {
                if (listItemView == null)
                    listItemView = LayoutInflater.from(getContext()).inflate(
                            R.layout.item_lesson_list, null, true);
            } catch (Resources.NotFoundException e) {
                // Timber.e(e);
                Toast.makeText(getContext(), R.string.dl_from_play, Toast.LENGTH_LONG).show();
                throw new RuntimeException("Not downloaded from the Play Store");
            }

            final Item item = getItem(position);
            final ImageView arrowImageView = listItemView.findViewById(R.id.arrow_image_view);
            final TextView contentTextView = listItemView.findViewById(R.id.lesson_item_body);
            final View progressBar = listItemView.findViewById(R.id.lesson_list_progress_bar);
            TextView headerTextView = listItemView.findViewById(R.id.lesson_item_header_text);
            View headerLayout = listItemView.findViewById(R.id.lesson_item_header_layout);

            arrowImageView.setImageResource(dropDownResId);
            arrowImageView.setScaleY(1f);
            clickableViewAnimation(headerLayout, getContext());
            clickableViewAnimation(contentTextView, getContext());

            if (position == 0) contentTextView.setMovementMethod(LinkMovementMethod.getInstance());
            else {
                contentTextView.setVisibility(View.GONE);
                contentTextView.setOnLongClickListener(view -> {
                    contentTextView.setVisibility(View.GONE);
                    arrowImageView.setScaleY(1f);
                    return false;
                });
            }
            assert item != null;
            Timber.v("mHeader = %s", item.mHeader);

            if (item.mHeader == null || item.mHeader.equals("")) {
                headerLayout.setVisibility(View.GONE);
                contentTextView.setText(Html.fromHtml(item.mText));
                contentTextView.setTextColor(textColor);
                contentTextView.setVisibility(View.VISIBLE);
                Timber.v("body = %s", item.mText);
            } else {
                headerTextView.setText(Html.fromHtml(item.mHeader));
                headerTextView.setTextColor(textColor);
                headerLayout.setVisibility(View.VISIBLE);
                headerLayout.setOnClickListener(view -> {
                    if (contentTextView.getVisibility() == View.GONE) {
                        progressBar.setVisibility(View.VISIBLE);
                        arrowImageView.setImageResource(dropDownResId);
                        arrowImageView.setScaleY(-1f);                          // vertical flip
                        contentTextView.post(() -> {
                            Timber.v("mText = %s", item.mText);
                            contentTextView.setText(Html.fromHtml(item.mText));
                            contentTextView.setTextColor(textColor);
                            progressBar.setVisibility(View.GONE);
                            contentTextView.setVisibility(View.VISIBLE);
                            ((ListView) parent).smoothScrollToPosition(position);
                        });
                    } else {
                        contentTextView.setVisibility(View.GONE);
                        arrowImageView.setImageResource(dropDownResId);
                        arrowImageView.setScaleY(1f);
                    }
                });
            }

            return listItemView;
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base, "en"));
    }
}
