package com.memory_athlete.memoryassistant.main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import com.memory_athlete.memoryassistant.R;

public class PrivacyPolicy extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        ((WebView) findViewById(R.id.privacy_policy_view))
                .loadUrl("file:///android_res/raw/privacy_policy.html");
        setTitle("Privacy Policy");
    }
}
