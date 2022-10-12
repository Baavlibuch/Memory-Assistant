package com.memory_athlete.memoryassistant.lessons;

import static com.memory_athlete.memoryassistant.Helper.clickableViewAnimation;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.memory_athlete.memoryassistant.Helper;
import com.memory_athlete.memoryassistant.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import timber.log.Timber;

public class LessonFragment extends Fragment {
    private Activity activity;
    private int textColor;
    private int dropDownResId;
    private String themeForWebView;

    public LessonFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_lesson, container, false);
        Bundle bundle = getArguments();
        activity = getActivity();
        theme();

        // Initialized as an empty string to distinguish from null that might be returned after file is read
        StringBuilder sb = new StringBuilder();

        assert bundle != null;
        int fileInt = bundle.getInt(Helper.RAW_RESOURCE_ID_KEY, 0);
        if (fileInt != 0 && bundle.getBoolean("resource", true)) {  // Raw

            if (bundle.getBoolean("list", false)) {                 // list
                ListView listView = rootView.findViewById(R.id.lesson_list);
                ArrayList<Item> list = readResourceList(fileInt);
                assert list != null;
                Timber.v("list length = %s", list.size());
                LessonAdapter lessonAdapter = new LessonAdapter(activity, list);
                Timber.v("adapter set");
                listView.setAdapter(lessonAdapter);
                listView.setVisibility(View.VISIBLE);
                Timber.v("listView set");
                return rootView;

            } else {

                sb = readResource(fileInt);                                      // non-list
                if (sb == null) throw new RuntimeException("Got null stringBuilder");
            }

        } else {                                                                    // Asset

            if (bundle.getBoolean("list", false)) {                  // list
                Timber.i("activity = " + activity.getLocalClassName());
                Timber.i("bundle = " + bundle);
                Timber.i("title = " + activity.getTitle());

                ArrayList<Item> list = readAssetList(bundle);

                ListView listView = rootView.findViewById(R.id.lesson_list);
                //Timber.v("list length = " + list.size());
                LessonAdapter lessonAdapter = new LessonAdapter(activity, list);
                Timber.v("adapter set");
                listView.setAdapter(lessonAdapter);
                listView.setVisibility(View.VISIBLE);
                Timber.v("listView set");
                return rootView;
            }

            readAsset(sb, bundle);                                                  // non-list

        }

        if (bundle.getBoolean("webView", false)) {                  // JQMath
            setWebView(sb, rootView);
            return rootView;
        }

        Timber.v("list is false");
        ((TextView) rootView.findViewById(R.id.lesson)).setText(Html.fromHtml(sb.toString()));
        rootView.findViewById(R.id.lesson_scroll).setVisibility(View.VISIBLE);
        return rootView;
    }

    protected void theme() {
        String theme = PreferenceManager.getDefaultSharedPreferences(requireActivity())
                .getString(getString(R.string.theme), "AppTheme");
        String[] themes = getResources().getStringArray(R.array.themes);
        if (themes[1].equals(theme)) {
            textColor = Color.LTGRAY;
            dropDownResId = R.drawable.ic_arrow_drop_down_dark;
            themeForWebView = "<style>\n" +
                    "body {backgroundString-color: #303030;}\n" +
                    "p    {color: #c1c1c1;}\n" +
                    "</style>\n";
        } else if (themes[2].equals(theme)) {
            textColor = Color.LTGRAY;
            dropDownResId = R.drawable.ic_arrow_drop_down_pitch;
            themeForWebView = "<style>\n" +
                    "body {backgroundString-color: #000000;}\n" +
                    "p    {color: #c1c1c1;}\n" +
                    "</style>\n";
        } else {
            textColor = Color.DKGRAY;
            dropDownResId = R.drawable.ic_arrow_drop_down_light;
            themeForWebView = "";
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setWebView(StringBuilder sb, View rootView) {
        WebView webView = rootView.findViewById(R.id.web_view);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        StringBuilder sb1 = readResource(R.raw.jqmath_beg);
        if (sb1 == null) throw new RuntimeException("jqMath read error");
        StringBuilder sb2 = readResource(R.raw.jqmath_end);
        if (sb2 == null) throw new RuntimeException("jqMath read error");
        String js = sb1.toString() + themeForWebView + sb.toString() + sb2.toString();

        webView.loadDataWithBaseURL("file:///android_asset/jqmath/", js, "mText/html", "UTF-8", null);
        webView.setVisibility(View.VISIBLE);
        webView.setWebChromeClient(new WebChromeClient() {
            public boolean onConsoleMessage(ConsoleMessage cm) {
                Timber.d("WebView Log: " + cm.message() + " -- From line "
                        + cm.lineNumber() + " of " + cm.sourceId());
                return true;
            }
        });

    }

    private StringBuilder readResource(int path) {
        if (path == 0) return null;
        BufferedReader reader = null;
        String line;
        StringBuilder sb = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(getResources().openRawResource(path)));
            while ((line = reader.readLine()) != null) sb.append(line);
            reader.close();
            return sb;
        } catch (IOException e) {
            Timber.e(e);
            try {
                reader.close();
            } catch (Exception e1) {
                Timber.e(e1);
            }
            Toast.makeText(activity, "Try again", Toast.LENGTH_SHORT).show();
            activity.finish();
            // need to return something in case of an error. Probably null
            return sb;
        }
    }

    private ArrayList<Item> readResourceList(int path) {
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
            Toast.makeText(activity, "Try again", Toast.LENGTH_SHORT).show();
            activity.finish();
            return null;
        }
    }

    private ArrayList<Item> readAssetList(Bundle bundle) {
        Timber.v("readAssetList() entered");
        String header = "", line = bundle.getString("fileString"); //For assets and filesDir
        if (line == null || line.equals("")) throw new RuntimeException("Got null in bundle!");
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        ArrayList<Item> letterList = new ArrayList<>();
        try {
            reader = new BufferedReader(new InputStreamReader(activity.getAssets().open(line)));
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
            Toast.makeText(activity, R.string.dl_from_play, Toast.LENGTH_SHORT).show();
            try {
                if (reader != null) reader.close();
            } catch (IOException e1) {
                Timber.e(e1);
            }
            activity.finish();
        } catch (Exception e) {
            throw new RuntimeException("failure", e);
        }
        throw new RuntimeException("What a terrible failure");
    }

    private void readAsset(StringBuilder sb, Bundle intent) {
        String line = intent.getString("fileString");           // For assets and filesDir
        BufferedReader bufferedReader = null;
        try {
            assert line != null;
            bufferedReader = new BufferedReader(new InputStreamReader(activity.getAssets().open(line)));
            while ((line = bufferedReader.readLine()) != null) sb.append(line);
        } catch (IOException e) {
            Toast.makeText(activity, "Try again", Toast.LENGTH_SHORT).show();
            activity.finish();
        }
        try {
            if (bufferedReader != null)
                bufferedReader.close();
        } catch (IOException e) {
            throw new RuntimeException("Failed to close the buffer");
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
            Timber.v("LessonAdapter() entered");
        }

        @Override
        @NonNull
        @SuppressLint("InflateParams")  // null is passed as rootView in inflate().
        // Will not cause error as a custom view is used later
        public View getView(int position, View listItemView, @NonNull ViewGroup parent) {
            Timber.v("getView() entered");
            if (listItemView == null)
                listItemView = LayoutInflater.from(getContext()).inflate(
                        R.layout.item_lesson_list, null, true);

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

            if (position > 0) {
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
                        arrowImageView.setScaleY(-1f);                      // vertical flip
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
}
