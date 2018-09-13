package com.memory_athlete.memoryassistant.lessons;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.memory_athlete.memoryassistant.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */

public class LessonFragment extends Fragment {
    private Activity activity;
    public LessonFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_lesson, container, false);
        Bundle bundle = getArguments();
        activity = getActivity();

        StringBuilder sb = new StringBuilder("");
        assert bundle != null;
        int fileInt = bundle.getInt("file", 0);
        if (fileInt != 0 && bundle.getBoolean("resource", true)) {
            if (bundle.getBoolean("list", false)) {
                ListView listView = rootView.findViewById(R.id.lesson_list);
                ArrayList<Item> list = readResourceList(fileInt);
                assert list != null;
                Timber.v("list length = " + list.size());
                LessonAdapter lessonAdapter = new LessonAdapter(activity, list);
                Timber.v("adapter set");
                listView.setAdapter(lessonAdapter);
                listView.setVisibility(View.VISIBLE);
                Timber.v("listView set");
                return rootView;
            } else sb = readResource(fileInt);//For raw files
        } else {
            if (bundle.getBoolean("list", false)) {
                ListView listView = rootView.findViewById(R.id.lesson_list);
                ArrayList<Item> list = readAssetList(bundle);
                //Timber.v("list length = " + list.size());
                LessonAdapter lessonAdapter = new LessonAdapter(activity, list);
                Timber.v("adapter set");
                listView.setAdapter(lessonAdapter);
                listView.setVisibility(View.VISIBLE);
                Timber.v("listView set");
                return rootView;
            }
            sb = readAsset(sb, bundle);
        }
        if (sb == null) {
            Timber.e("String Builder sb is empty");
            activity.finish();
            return rootView;
        }

        if (bundle.getBoolean("webView", false)) {     //For JQMath
            setWebView(sb, rootView);
        } else {
            Timber.v("list is false");
            ((TextView) rootView.findViewById(R.id.lesson)).setText(Html.fromHtml(sb.toString()));
            rootView.findViewById(R.id.lesson_scroll).setVisibility(View.VISIBLE);
        }
        return rootView;
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setWebView(StringBuilder sb, View rootView) {
        WebView webView = rootView.findViewById(R.id.web_view);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        StringBuilder sb1 = readResource(R.raw.jqmath_beg);
        if (sb1 == null) throw new RuntimeException("jqMath read error");
        StringBuilder sb2 = readResource(R.raw.jqmath_end);
        if (sb2 == null) throw new RuntimeException("jqmath read error");
        String js = sb1.toString() + themeForWebView() + sb.toString() + sb2.toString();
        //String js = JQMATH_BEG + "asfd $$u_n/v_n$$" + JQMATH_END;
        //webView.loadUrl("file:///android_asset/jqmath/index.html");
        //((TextView) findViewById(R.id.lesson)).setText(js);
        //findViewById(R.id.lesson_scroll).setVisibility(View.VISIBLE);

        webView.loadDataWithBaseURL("file:///android_asset/jqmath/", js, "mText/html", "UTF-8", null);
        webView.setVisibility(View.VISIBLE);
    }

    private String themeForWebView(){
        String theme = PreferenceManager.getDefaultSharedPreferences(activity)
                .getString(getString(R.string.theme), "AppTheme");
        switch (theme) {
            case "Dark":
                return "<style>\n" +
                        "body {backgroundString-color: #303030;}\n" +
                        "p    {color: #c1c1c1;}\n" +
                        "</style>\n";
            case "Night":
                return "<style>\n" +
                        "body {backgroundString-color: #000000;}\n" +
                        "p    {color: #c1c1c1;}\n" +
                        "</style>\n";
            default: return "";
        }
    }

    private StringBuilder readResource(int path) {
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
            Toast.makeText(activity, "Try again", Toast.LENGTH_SHORT).show();
            activity.finish();
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
        if (line == null || line.equals("")) return null;
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
            try {
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return letterList;

        } catch (IOException e) {
            Toast.makeText(activity, "Try again", Toast.LENGTH_SHORT).show();
            activity.finish();
        }
        try {
            if (reader != null)
                reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private StringBuilder readAsset(StringBuilder sb, Bundle intent) {
        String line = intent.getString("fileString"); //For assets and filesDir
        BufferedReader bufferedReader = null;
        try {
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

        @SuppressLint("InflateParams")  // null is passed as rootView in inflate().
                                        // Will not cause error as a custom view is used later
        @NonNull
        @Override
        public View getView(int position, View listItemView, @NonNull ViewGroup parent) {
            Timber.v("getView() entered");
            if (listItemView == null)
                listItemView = LayoutInflater.from(getContext()).inflate(
                        R.layout.item_lesson_list, null, true);

            final Item item = getItem(position);
            //if (item==null){
            //  finish();
            //return listItemView;
            //}
            final TextView textView1 = listItemView.findViewById(R.id.lesson_item_body);
            final View progressBar = listItemView.findViewById(R.id.lesson_list_progress_bar);

            if (position > 0) {
                textView1.setVisibility(View.GONE);
                textView1.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        textView1.setVisibility(View.GONE);
                        return false;
                    }
                });
                //Timber.v("listItem " + position + "-1 visibility=" + getItem(position-1).)
            }

            assert item != null;
            Timber.v("mHeader = " + item.mHeader);

            TextView textView = listItemView.findViewById(R.id.lesson_item_header_text);
            View headerLayout = listItemView.findViewById(R.id.lesson_item_header_layout);
            if (item.mHeader == null || item.mHeader.equals("")) {
                headerLayout.setVisibility(View.GONE);
                textView1.setText(Html.fromHtml(item.mText));
                textView1.setVisibility(View.VISIBLE);
                Timber.v("body = " + item.mText);
            } else {
                textView.setText(Html.fromHtml(item.mHeader));
                headerLayout.setVisibility(View.VISIBLE);
                headerLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (textView1.getVisibility() == View.GONE) {
                            progressBar.setVisibility(View.VISIBLE);
                            textView1.post(new Runnable() {
                                public void run() {
                                    Timber.v("mText = " + item.mText);
                                    textView1.setText(Html.fromHtml(item.mText));
                                    progressBar.setVisibility(View.GONE);
                                    textView1.setVisibility(View.VISIBLE);
                                }
                            });
                        } else textView1.setVisibility(View.GONE);
                    }
                });
            }
            return listItemView;
        }
    }
}
