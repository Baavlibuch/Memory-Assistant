package com.memory_athlete.memoryassistant.main;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;

import com.memory_athlete.memoryassistant.R;

public class PrivacyPolicy extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        ((WebView) findViewById(R.id.privacy_policy_view))
                .loadUrl("file:///android_res/raw/privacy_policy.html");
        setTitle(R.string.privacy_policy);

        findViewById(R.id.privacy_policy_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://memory-athlete.com/privacy_policy.html"));
                startActivity(browserIntent);
            }
        });
    }
}
