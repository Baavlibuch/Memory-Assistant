package com.maniksejwal.memoryathletes.Lessons;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.widget.TextView;
import android.widget.Toast;

import com.maniksejwal.memoryathletes.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Manik on 14/04/17.
 */

public class Lessons extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson);
        Intent intent = getIntent();
        setTitle(getString(intent.getIntExtra("header", R.string.learn)));

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(getResources().openRawResource(
                    intent.getIntExtra("file", 0))));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line);//.append("\n\n");
            }
            ((TextView) findViewById(R.id.lesson)).setText(Html.fromHtml(sb.toString()));

        } catch (IOException e) {
            Toast.makeText(this, "Couldn't open the lesson", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        try {
            reader.close(); //had if (dict!=null)
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
