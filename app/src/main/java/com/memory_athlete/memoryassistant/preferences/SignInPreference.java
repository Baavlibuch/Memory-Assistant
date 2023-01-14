package com.memory_athlete.memoryassistant.preferences;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.Preference;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class SignInPreference extends Preference {

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    FirebaseAuth firebaseAuth;


    public SignInPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onClick() {
        super.onClick();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
        firebaseAuth = FirebaseAuth.getInstance();

        //LOGOUT
        if(account!=null){
            gsc = GoogleSignIn.getClient(getContext(),GoogleSignInOptions.DEFAULT_SIGN_IN);

            gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        // firebaseAuth.signOut();
                        Toast.makeText(getContext(),"Logged out", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

        //LOGIN
        else {
            Intent i = new Intent(getContext(), SignInActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(i);
        }

    }

}
