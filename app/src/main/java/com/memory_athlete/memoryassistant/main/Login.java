package com.memory_athlete.memoryassistant.main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.memory_athlete.memoryassistant.R;


public class Login extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    void click(View view){
        Toast.makeText(this, "Sorry, cloud is only for pro users!", Toast.LENGTH_SHORT).show();
    }
}
