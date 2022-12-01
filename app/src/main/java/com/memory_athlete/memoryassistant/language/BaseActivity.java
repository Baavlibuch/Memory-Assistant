package com.memory_athlete.memoryassistant.language;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.memory_athlete.memoryassistant.R;
import com.memory_athlete.memoryassistant.main.MainActivity;

public class BaseActivity extends AppCompatActivity
{
    String[] lang = {"Select one!","Default (English)","Hindi","Bengali","Arabic","Czech","German","Spanish","Filipino","French","Italian","Japanese",
    "Korean","Malay","Norwegian","Portuguese","Portuguese-Brazil","Russian","Swahili","Turkish","Chinese"};

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        Spinner spin = findViewById(R.id.spinner2);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(BaseActivity.this, android.R.layout.simple_spinner_item, lang);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);

        //google sign in
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken("520025171011-f9v8n1d0l7jtm249rj7u9em5att3d8bj.apps.googleusercontent.com")
                .build();
        gsc = GoogleSignIn.getClient(BaseActivity.this,gso);
        Intent intent = gsc.getSignInIntent();
        firebaseAuth = FirebaseAuth.getInstance();
        startActivityForResult(intent, 1000);


            spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    String value = adapterView.getItemAtPosition(i).toString();

                    if (!value.equals("Select one!")) {

                        SettingLanguage sl = new SettingLanguage();
                        String string_to_locale = sl.setLang(value);
                        LocaleHelper.setLocale(BaseActivity.this, string_to_locale);

                        SharedPreferences shrd = getSharedPreferences("LANGUAGE",MODE_PRIVATE);
                        SharedPreferences.Editor editor = shrd.edit();
                        editor.putString("str",value);
                        editor.apply();

                        Toast.makeText(BaseActivity.this, value + " language will be applied", Toast.LENGTH_SHORT).show();


                        finish();
                        Intent intent1 = new Intent(BaseActivity.this, MainActivity.class);
                        intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent1);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

        }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1000)
        {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            if(task.isSuccessful())
            {
                try {
                    GoogleSignInAccount googleSignInAccount = task.getResult(ApiException.class);

                    if(googleSignInAccount!=null)
                    {
                        AuthCredential authCredential= GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(),null);
                        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful())
                                        {
                                            Toast.makeText(BaseActivity.this, "Signed in!", Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                        {
                                            Toast.makeText(BaseActivity.this,"Not signed in", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                    }
                }
                catch (ApiException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }



    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base, "en"));
    }

}


