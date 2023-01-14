package com.memory_athlete.memoryassistant.preferences;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.memory_athlete.memoryassistant.Helper;
import com.memory_athlete.memoryassistant.main.Preferences;

public class SignInActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Helper.theme(this, SignInActivity.this);
        //setContentView(R.layout.activity_preferences);
        setTitle("Google authentication");

        //google sign in
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken("520025171011-f9v8n1d0l7jtm249rj7u9em5att3d8bj.apps.googleusercontent.com")
                .build();
        GoogleSignInClient gsc = GoogleSignIn.getClient(this, gso);
        Intent intent = gsc.getSignInIntent();
        startActivityForResult(intent, 1000);

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
//                        AuthCredential authCredential= GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(),null);
//                        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                            @Override
//                            public void onComplete(@NonNull Task<AuthResult> task) {
//                                if(task.isSuccessful())
//                                {
//                                    Toast.makeText(Preferences.this, "Signed in!", Toast.LENGTH_SHORT).show();
//                                }
//                                else
//                                {
//                                    Toast.makeText(Preferences.this,"Not signed in", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });

                        Toast.makeText(SignInActivity.this, "Signed in!", Toast.LENGTH_SHORT).show();

                        finish();
                        Intent i = new Intent(this, Preferences.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);

                    }
                }
                catch (ApiException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }


}
