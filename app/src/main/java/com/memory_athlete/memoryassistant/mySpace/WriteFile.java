package com.memory_athlete.memoryassistant.mySpace;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.preference.PreferenceManager;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.memory_athlete.memoryassistant.Encryption;
import com.memory_athlete.memoryassistant.Helper;
import com.memory_athlete.memoryassistant.R;
import com.memory_athlete.memoryassistant.language.LocaleHelper;
import com.memory_athlete.memoryassistant.reminders.ReminderUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.Objects;

import timber.log.Timber;

public class WriteFile extends AppCompatActivity {
    private boolean name = false;
    String path;
    String oldName = null;
    boolean deleted = false;
    EditText searchEditText;
    EditText mySpaceEditText;
    int searchIndex;// index in string to start searching from
    boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Helper.theme(this, WriteFile.this);
        setContentView(R.layout.activity_write_file);
        String header = intent.getStringExtra("mHeader");

        if (header == null) header = "New";
        else oldName = header;
        //header = header.substring(0, header.length() - 4);
        setTitle(header);

        mySpaceEditText = findViewById(R.id.my_space_editText);
        searchEditText = findViewById(R.id.search_edit_text);

        path = intent.getStringExtra("fileName");
        if (intent.getBooleanExtra("name", true)) {
            ((EditText) findViewById(R.id.f_name)).setText(getTitle().toString());
            StringBuilder text = new StringBuilder();
            try {
                BufferedReader br = new BufferedReader(new FileReader(new File(
                        path + File.separator + getTitle().toString() + ".txt")));
                String line;

                while ((line = br.readLine()) != null) {
                    text.append(line);
                    text.append('\n');
                }
                br.close();

                //decrypt the file

                //take salt from database
                GoogleSignInAccount account1 = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
                if (account1 != null) {
                    String id_from_account1 = account1.getId();
                    assert id_from_account1 != null;
                    DatabaseReference databaseReferenceKey = FirebaseDatabase.getInstance().getReference("MySpaceFiles")
                            .child(id_from_account1).child("UNIQUE_KEY");

                    databaseReferenceKey.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (snapshot.exists()) {
                                String salt = Objects.requireNonNull(snapshot.getValue()).toString();
//                                StringBuilder text1 = new StringBuilder(Encryption.decrypt(text,salt));
                                String text1 = Encryption.decrypt(text.toString(), salt);
                                mySpaceEditText.setText(text1);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }

                else{
                    mySpaceEditText.setText(text);
                }


                //findViewById(R.id.saveFAB).setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), R.string.try_again, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        //intent.getStringExtra()

        // reset search index to zero whenever text changes
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0) {
                    searchIndex = 0;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search(searchEditText);
                return true;
            }
            return false;
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_write_file, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                Timber.d(getTitle().toString());
                File file = new File(path + File.separator + getTitle().toString() + ".txt");
                deleted = true;
                deleteFromFirebase(getTitle().toString());
                finish();
                return !file.exists() || file.delete();
            case R.id.dont_save:
                flag = true;
                NavUtils.navigateUpFromSameTask(this);
                break;
            case android.R.id.home:
                if (save()) NavUtils.navigateUpFromSameTask(this);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        // hide searchEditText if it is visible
        if (searchEditText.getVisibility() == View.VISIBLE) {
            searchEditText.setVisibility(View.GONE);
            return;
        }
        if(flag){
            super.onBackPressed();
        }
        // save() returns false if save was rejected to notify the user at most once.
        if (save()) super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(flag){
            return;
        }
        if (!deleted) save();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public boolean save() {

        String string = mySpaceEditText.getText().toString();
        String fname = ((EditText) findViewById(R.id.f_name)).getText().toString();
        if (fname.length() == 0 && string.length() == 0) {
            if (!name) {
                ((EditText) findViewById(R.id.f_name)).setError("please enter a name");
                findViewById(R.id.f_name).requestFocus();
                //Toast.makeText(this, "please enter a name", Toast.LENGTH_SHORT).show();
                name = true;
                return false;
            }
            Toast.makeText(this, R.string.nameless_file, Toast.LENGTH_SHORT).show();
            return true;
        }
        String dirPath = path;
        if (fname.length() > 250) {
            if (name) return true;

            Toast.makeText(this, "Try again with a shorter name", Toast.LENGTH_SHORT).show();
            name = true;
            return false;
        }

        if (oldName != null && !fname.equals(oldName)) {
            File from = new File(path + File.separator + oldName + ".txt");
            if (from.exists()) {
                File to = new File(path + File.separator + fname + ".txt");
                from.renameTo(to);
            }
        }
        String f_head = fname;
        fname = path + File.separator + fname + ".txt";
        Timber.v("fname = %s", fname);
        if (!Helper.mayAccessStorage(this)) {
            if (name) {
                Timber.i(getString(R.string.storage_permissions));
                Toast.makeText(this, R.string.storage_permissions,
                        Toast.LENGTH_SHORT).show();
                return true;
            }

            name = true;
            return false;
        }
        if (Helper.externalStorageNotWritable()) {
            Timber.i("externalStorageNotWritable");
            Toast.makeText(this, R.string.check_storage, Toast.LENGTH_SHORT).show();
            if (name) return true;

            name = true;
            return false;
        }
        if (Helper.makeDirectory(dirPath, getApplicationContext())) {
            try {

                //encrypt the file
                //take salt from firebase
                GoogleSignInAccount account1 = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
                if (account1 != null) {
                    String id_from_account1 = account1.getId();

                    FileOutputStream outputStream = new FileOutputStream(new File(fname));

                    assert id_from_account1 != null;
                    DatabaseReference databaseReferenceKey = FirebaseDatabase.getInstance().getReference("MySpaceFiles")
                            .child(id_from_account1);

                    databaseReferenceKey.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                if(snapshot.hasChild("UNIQUE_KEY")) {
                                    String salt = Objects.requireNonNull(snapshot.child("UNIQUE_KEY").getValue()).toString();
                                    String string1 = Encryption.encrypt(string, salt);
                                    try {
                                        outputStream.write(string1.getBytes());
                                        outputStream.close();
                                    } catch (Exception e) {
                                        Toast.makeText(WriteFile.this, "Please sign in!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }

                    });



                }

                else{
                    FileOutputStream outputStream = new FileOutputStream(new File(fname));
                    outputStream.write(string.getBytes());
                    outputStream.close();
                }


                SharedPreferences.Editor editor = PreferenceManager
                        .getDefaultSharedPreferences(this).edit();
                editor.putLong(fname, System.currentTimeMillis());
                Timber.v(fname + "made at " + System.currentTimeMillis());
                editor.apply();

                //firebase storage
                Uri uri_file = Uri.fromFile(new File(fname));
                Intent intent = getIntent();
                String disciplineHeader = intent.getStringExtra("disciplineHeader");

                if(uri_file!=null) {

                    GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());

                    if (account != null) {
                        String id_from_account = account.getId();
                        saveToFirebase(id_from_account, uri_file, f_head, disciplineHeader);
                        Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(this, "Saved offline", Toast.LENGTH_SHORT).show();
                    }
                }

                ReminderUtils.mySpaceReminder(this, fname);

            } catch (Exception e) {
                Timber.e(e);
                //Toast.makeText(getApplicationContext(), R.string.try_again, Toast.LENGTH_SHORT).show();
            }
        }
        Timber.v("fileName = %s", path);
        return true;
    }

    // returns true if found
    private boolean search(String stringToSearch) {
        if (stringToSearch.equals("")) return false;                            // don't search

        stringToSearch = stringToSearch.toLowerCase();
        String fullText = mySpaceEditText.getText().toString().toLowerCase();
        boolean hasText = fullText.contains(stringToSearch);
        Timber.d("hasText = %s", hasText);
        if (hasText) {
            searchIndex = fullText.indexOf(stringToSearch, searchIndex);        // index in string
            // -1 : not found. Happens after the last
            if (searchIndex == -1) {
                searchIndex = 0;
                Toast.makeText(this, R.string.search_from_start, Toast.LENGTH_SHORT).show();
                return false;
            }

            // scroll to location
            int lineNumber = mySpaceEditText.getLayout().getLineForOffset(searchIndex);
            int totalLines = mySpaceEditText.getLayout().getLineCount();
            int editTextViewBottom = findViewById(R.id.my_space_editText).getBottom();
            ((ScrollView) findViewById(R.id.my_space_scroll_view))
                    .smoothScrollTo(0, editTextViewBottom * (lineNumber-1) / totalLines);
            // highlight
            mySpaceEditText.setSelection(searchIndex, searchIndex + stringToSearch.length());
            mySpaceEditText.requestFocus();

            searchIndex++;
            return true;
        }
        Toast.makeText(getApplicationContext(), R.string.not_found, Toast.LENGTH_SHORT).show();
        return false;
    }

    public void search(View view) {
        String stringToSearch = searchEditText.getText().toString();
        searchIndex++;
        searchEditText.setVisibility(View.VISIBLE);
        if (!search(stringToSearch))
            searchEditText.requestFocus();
    }

    public void deleteFromFirebase(String fileHeading){
        //delete from firebase
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());

        if (account == null) {
            Toast.makeText(WriteFile.this, "Not signed in!", Toast.LENGTH_SHORT).show();
        }

        else {

            Intent intent = getIntent();
            String disciplineHeader = intent.getStringExtra("disciplineHeader");

            String id_from_account = account.getId();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            StorageReference upload_ref = storageReference.child("MySpaceFiles/" + id_from_account + "/" + getString(R.string.my_space)
                    + "/" + disciplineHeader + "/" + fileHeading + ".txt");

            assert id_from_account != null;

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("MySpaceFiles")
                    .child(id_from_account).child(getString(R.string.my_space)).child(disciplineHeader).child(fileHeading);

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String url_file = snapshot.child("a").getValue(String.class);

                    snapshot.getRef().removeValue();

                    if(url_file!=null){

                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference httpsReference = storage.getReferenceFromUrl(url_file);

                        httpsReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                            }
                        });

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });

        }

    }

    public void saveToFirebase(String id_from_account, Uri uri_file,  String f_head, String disciplineHeader){

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference upload_ref = storageReference.child("MySpaceFiles/" + id_from_account + "/" + getString(R.string.my_space)
                + "/" + disciplineHeader + "/" + f_head + ".txt");

        assert id_from_account != null;

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("MySpaceFiles")
                .child(id_from_account).child(getString(R.string.my_space)).child(disciplineHeader).child(f_head);

        upload_ref.putFile(uri_file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                upload_ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        ModelForSavingFiles modelForSavingFiles = new ModelForSavingFiles(uri.toString());
                        databaseReference.setValue(modelForSavingFiles);

                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(WriteFile.this, "Please sign in!", Toast.LENGTH_SHORT).show();
            }
        });


    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base, "en"));
    }
}
