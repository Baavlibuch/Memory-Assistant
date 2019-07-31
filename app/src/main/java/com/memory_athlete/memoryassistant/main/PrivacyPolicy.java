package com.memory_athlete.memoryassistant.main;

import android.os.Bundle;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import com.memory_athlete.memoryassistant.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class PrivacyPolicy extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        ((WebView) findViewById(R.id.privacy_policy_view)).loadData(readTextFromResource(),
                "text/html", "utf-8");
        setTitle(R.string.privacy_policy);
    }

    private String readTextFromResource() {
        InputStream raw = getResources().openRawResource(R.raw.privacy_policy);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        int i;
        try {
            i = raw.read();
            while (i != -1) {
                stream.write(i);
                i = raw.read();
            }
            raw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stream.toString();
    }

}
