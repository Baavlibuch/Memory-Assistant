package com.memory_athlete.memoryassistant.main;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.memory_athlete.memoryassistant.language.LocaleHelper;
import com.memory_athlete.memoryassistant.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import timber.log.Timber;

public class PrivacyPolicy extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_privacy_policy);
        } catch (Resources.NotFoundException e){                                     // Does it even work!?
            Toast.makeText(this, R.string.wait, Toast.LENGTH_SHORT).show();
            finish();
        }
        ((WebView) findViewById(R.id.privacy_policy_view)).loadData(readTextFromResource(),
                "text/html", "utf-8");
        setTitle(R.string.privacy_policy);
        FirebaseAnalytics.getInstance(this).logEvent("checked_privacy_policy", null);
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
            Timber.e(e);
        }
        return stream.toString();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base, "en"));
    }

}
