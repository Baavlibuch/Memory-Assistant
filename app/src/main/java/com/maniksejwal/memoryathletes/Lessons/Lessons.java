package com.maniksejwal.memoryathletes.Lessons;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.maniksejwal.memoryathletes.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Manik on 14/04/17.
 */

public class Lessons extends AppCompatActivity {


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
        setTitle(getString(intent.getIntExtra("header", R.string.learn)));

        StringBuilder sb = readFile(intent.getIntExtra("file", 0));
        if (sb == null) {
            finish();
            return;
        }

        if (intent.getBooleanExtra("webView", false)) {
            WebView webView = (WebView) findViewById(R.id.web_view);
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            StringBuilder sb1 = readFile(R.raw.jqmath_beg);
            if (sb1 == null) {
                finish();
                return;
            }
            StringBuilder sb2 = readFile(R.raw.jqmath_end);
            if (sb2 == null) {
                finish();
                return;
            }
            String js = sb1.toString() + sb.toString() + sb2.toString();
            //String js = JQMATH_BEG + "asfd $$u_n/v_n$$" + JQMATH_END;
            //webView.loadUrl("file:///android_asset/jqmath/index.html");
            //((TextView) findViewById(R.id.lesson)).setText(js);
            //findViewById(R.id.lesson_scroll).setVisibility(View.VISIBLE);

            webView.loadDataWithBaseURL("file:///android_asset/jqmath/", js, "text/html", "UTF-8", null);
            webView.setVisibility(View.VISIBLE);
        } else {
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
}
