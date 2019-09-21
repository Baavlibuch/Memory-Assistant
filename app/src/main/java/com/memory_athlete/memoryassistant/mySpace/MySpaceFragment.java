package com.memory_athlete.memoryassistant.mySpace;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.memory_athlete.memoryassistant.Helper;
import com.memory_athlete.memoryassistant.R;
import com.memory_athlete.memoryassistant.reminders.ReminderUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import timber.log.Timber;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MySpaceFragment extends Fragment {
    public int MIN_DYNAMIC_VIEW_ID = 2;

    public int fragListViewId = 0;
    private File dir = null;
    private String title = "", fileName, oldTabTitle, oldName = null;
    private Boolean name;
    private int searchIndex;

    private View rootView;
    private EditText searchEditText;
    private EditText mySpaceEditText;
    private LinearLayout editLayout;
    private View fab;

    private View.OnClickListener addClickListener;
    private View.OnClickListener searchClickListener;

    private Activity activity;

    public interface TabTitleUpdater {
        void tabTitleUpdate(String title);
    }

    private TabTitleUpdater mCallback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (TabTitleUpdater) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement TabTitleUpdater");
        }
    }

    public MySpaceFragment() {
    }

    public boolean save() {
        if (rootView.findViewById(R.id.f_name).getVisibility() != VISIBLE) return true;
        Timber.v("Received back from the activity");
        return save(rootView);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Timber.v("onCreateView() started");
        //if(savedInstanceState != null){}
        try {
            rootView = inflater.inflate(R.layout.fragment_my_space, container, false);
        } catch (Resources.NotFoundException e) {
            // Crashlytics.logException(e);
            Toast.makeText(getActivity(), R.string.dl_from_play, Toast.LENGTH_LONG).show();
            Objects.requireNonNull(getActivity()).finish();
            return rootView;
        }
        rootView.findViewById(R.id.add_search).setVisibility(GONE);

        activity = getActivity();

        // There are 2 direct children of the RelativeLayout with IDs 0,1
        // Only add new views with IDs >=2
        fragListViewId = MIN_DYNAMIC_VIEW_ID;

        mySpaceEditText = rootView.findViewById(R.id.my_space_editText);
        searchEditText = rootView.findViewById(R.id.search_edit_text_mySpaceFragment);
        editLayout = rootView.findViewById(R.id.edit_mySpaceFragment_layout);
        fab = rootView.findViewById(R.id.add_search);
        Timber.i("FAB id = %s", fab.getId());

        setAdapter(rootView);
        setButtons(rootView);

        // reset whenever query is edited
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0) searchIndex = 0;
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        // search when done is pressed
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search();
                return true;
            }
            return false;
        });

        return rootView;
    }

    private ArrayList<Item> setList() {
        return new ArrayList<>(Arrays.asList(
                new Item(activity.getString(R.string.majors), WriteFile.class),
                new Item(activity.getString(R.string.ben), WriteFile.class),
                new Item(activity.getString(R.string.wardrobes), WriteFile.class),
                new Item(activity.getString(R.string.lists), WriteFile.class),
                new Item(activity.getString(R.string.words), WriteFile.class)));
        //TODO:
        //list.add(new Item(getString(R.string.equations), WriteEquations.class));
        //list.add(new Item(getString(R.string.algos), WriteAlgo.class));
        //list.add(new Item(getString(R.string.derivations), WriteEquations.class));
    }

    public void setAdapter(final View rootView) {
        Timber.v("setAdapter started");
        ArrayList<Item> arrayList = new ArrayList<>();
        if (fragListViewId == 0) throw new NullPointerException("increment fragListViewId");
        if (fragListViewId == MIN_DYNAMIC_VIEW_ID) arrayList = setList();
        else {
            if (dir == null) {
                Toast.makeText(getActivity(), R.string.try_again, Toast.LENGTH_SHORT).show();
                back();
            }
            File[] files = dir.listFiles();
            if (files == null || files.length == 0) return;
            for (File file : files) {
                Timber.d("FileName: %s", file.getName());
                arrayList.add(new Item(file.getName(), WriteFile.class));
            }
        }
        Timber.v("list set");
        MySpaceAdapter adapter = new MySpaceAdapter(getActivity(), arrayList);
        final ListView listView = new ListView(getActivity());
        listView.setDivider(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        listView.setDividerHeight(0);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

        float scale = activity.getResources().getDisplayMetrics().density;
        int dpAsPixels = (int) (16 * scale + 0.5f);
        layoutParams.setMargins(dpAsPixels, dpAsPixels, dpAsPixels, dpAsPixels);

        listView.setLayoutParams(layoutParams);
        //if (listViewId==1) listView.MarginLayoutParams
        listView.setId(fragListViewId);
        final RelativeLayout layout = rootView.findViewById(R.id.my_space_relative_layout);
        layout.addView(listView);
        listView.setAdapter(adapter);
        final ArrayList<Item> finalArrayList = arrayList;
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Timber.d("listView id = %s", listView.getId());
            Item item = finalArrayList.get(position);
            Timber.v("item.mPath = %s", item.mPath);
            if (fragListViewId == MIN_DYNAMIC_VIEW_ID) {                                // show selector
                dir = new File(Helper.APP_FOLDER + getString(R.string.my_space) + File.separator + item.mPath);
                layout.findViewById(fragListViewId).setVisibility(View.GONE);
                fragListViewId++;
                title = item.mName;
                mCallback.tabTitleUpdate(title);
                fab.setVisibility(View.VISIBLE);
                ((FloatingActionButton) fab).setImageDrawable(
                        ContextCompat.getDrawable(activity, R.drawable.ic_action_add));
                fab.setContentDescription(getString(R.string.word_new));
                fab.setOnClickListener(addClickListener);
                rootView.findViewById(R.id.edit_mySpaceFragment_layout).setVisibility(GONE);
                setAdapter(rootView);
                Timber.v("going to id 1, listViewId = %s", fragListViewId);
                //rootView.findViewById(R.id.back_button).setVisibility(View.VISIBLE);
                //rootView.findViewById(R.id.back_button).bringToFront();
            } else {                                                                    // show editor
                Timber.v("listViewId = %s", fragListViewId);
                fileName = Helper.APP_FOLDER + getString(R.string.my_space) + File.separator + title;
                //Intent intent = new Intent(getApplicationContext(), WriteFile.class);
                //intent.putExtra("mHeader", item.mName);
                //intent.putExtra("fileString", item.mItem);
                //intent.putExtra("fileName", fileName);
                File file = new File(fileName);
                boolean isDirectoryCreated = file.exists();
                if (!isDirectoryCreated) isDirectoryCreated = file.mkdirs();
                if (isDirectoryCreated) {
                    name = true;
                    try {
                        rootView.findViewById(fragListViewId++).setVisibility(View.GONE);
                    } catch (NullPointerException e) {
                        throw new RuntimeException("Wrong value of fragListViewId = " + fragListViewId);
                    }

                    editLayout.setVisibility(VISIBLE);
                    fab.setContentDescription(getString(R.string.search));
                    fab.setOnClickListener(searchClickListener);
                    ((FloatingActionButton) fab).setImageDrawable(
                            ContextCompat.getDrawable(activity, R.drawable.ic_action_search));

                    writeFile(rootView, fileName, item.mName);
                } else throw new RuntimeException("Directory not created in MySpace");
            }
        });
    }

    public void back() {
        Timber.v("back_button clicked, fragListViewId = %s", fragListViewId);
        // hide keypad
        if (activity.getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
            assert inputMethodManager != null;
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
        // hide search box
        if (searchEditText.getVisibility() == View.VISIBLE) {
            searchEditText.setVisibility(View.GONE);
            return;
        }
        // return to mySpace selector
        if (rootView.findViewById(R.id.edit_mySpaceFragment_layout).getVisibility() == VISIBLE) {
            Timber.v("fileName = %s", fileName);
            if (!save(rootView)) return;    // check if file has name if editor is visible
            editLayout.setVisibility(GONE);
            fab.setContentDescription(getString(R.string.word_new));
            fab.setOnClickListener(addClickListener);
            ((FloatingActionButton) fab).setImageDrawable(
                    ContextCompat.getDrawable(activity, R.drawable.ic_action_add));
        }
        // go back in mySpace
        if (rootView.findViewById(fragListViewId) != null)
            ((RelativeLayout) rootView).removeViewAt(fragListViewId);

        if (rootView.findViewById(--fragListViewId) != null) {
            ((RelativeLayout) rootView).removeViewAt(fragListViewId);
            // findViewById(fragListViewId).setVisibility(View.VISIBLE);
            if (fragListViewId == MIN_DYNAMIC_VIEW_ID) {
                rootView.findViewById(R.id.add_search).setVisibility(View.GONE);
                //rootView.findViewById(R.id.back_button).setVisibility(GONE);
                mCallback.tabTitleUpdate(getString(R.string.my_space));
            }
        }
        setAdapter(rootView);

        if (fragListViewId != MIN_DYNAMIC_VIEW_ID)
            rootView.findViewById(R.id.add_search).setVisibility(VISIBLE);
        if (oldTabTitle == null || fragListViewId == MIN_DYNAMIC_VIEW_ID)
            mCallback.tabTitleUpdate(getString(R.string.my_space));
        else mCallback.tabTitleUpdate(oldTabTitle);
    }

    private void setButtons(final View rootView) {
        //rootView.findViewById(R.id.add_search).setOnClickListener(
        addClickListener = v -> {
            Timber.v("add button clicked");
            name = false;
            fileName = Helper.APP_FOLDER + MySpaceFragment.this.getString(R.string.my_space) + File.separator + title;

            editLayout.setVisibility(VISIBLE);
            fab.setContentDescription(getString(R.string.search));
            fab.setOnClickListener(searchClickListener);
            ((FloatingActionButton) fab).setImageDrawable(
                    ContextCompat.getDrawable(activity, R.drawable.ic_action_search));

            if (rootView.findViewById(fragListViewId) != null)
                rootView.findViewById(fragListViewId++).setVisibility(View.GONE);
            else fragListViewId++;

            MySpaceFragment.this.writeFile(rootView, fileName, title);
        };

        searchClickListener = v -> search();
    }

    private void writeFile(View rootView, String path, String header) {
        oldTabTitle = title;
        mCallback.tabTitleUpdate(header);
        oldName = header;
        mySpaceEditText.setText("");
        ((TextView) rootView.findViewById(R.id.f_name)).setText("");
        if (name) {
            ((EditText) rootView.findViewById(R.id.f_name)).setText(header);
            StringBuilder text = new StringBuilder();
            try {
                BufferedReader br = new BufferedReader(new FileReader(new File(
                        path + File.separator + header + ".txt")));
                String line;

                while ((line = br.readLine()) != null) {
                    text.append(line);
                    text.append('\n');
                }
                br.close();
                mySpaceEditText.setText(text);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), R.string.try_again, Toast.LENGTH_SHORT).show();
                back();
            }
        }
        //intent.getStringExtra()
    }

    public boolean save(View rootView) {
        Timber.v("entered save()");
        String string = mySpaceEditText.getText().toString();
        String fname = ((EditText) rootView.findViewById(R.id.f_name)).getText().toString();
        if (fname.length() == 0) {
            if (!name) {
                ((EditText) rootView.findViewById(R.id.f_name)).setError("please enter a name");
                rootView.findViewById(R.id.f_name).requestFocus();
                //Toast.makeText(getActivity(), "please enter a name", Toast.LENGTH_SHORT).show();
                name = true;
                return false;
            }
            Toast.makeText(getActivity(), R.string.nameless_file, Toast.LENGTH_SHORT).show();
            return true;
        }
        String dirPath = fileName;
        if (fname.length() > 250) {
            if (name) return true;

            Toast.makeText(getActivity(), R.string.shorter_name, Toast.LENGTH_SHORT).show();
            name = true;
            return false;
        }

        if (!fname.equals(oldName)) {
            File from = new File(fileName + File.separator + oldName + ".txt");
            if (from.exists()) {
                File to = new File(fileName + File.separator + fname + ".txt");
                //noinspection ResultOfMethodCallIgnored
                from.renameTo(to);
            }
        }
        if (!Helper.mayAccessStorage(getContext())) {
            if (name) {
                Toast.makeText(getContext(), R.string.storage_permissions,
                        Toast.LENGTH_SHORT).show();
                return true;
            }

            name = true;
            return false;
        }
        if (Helper.externalStorageNotWritable()) {
            Toast.makeText(getActivity(), R.string.check_storage, Toast.LENGTH_SHORT).show();
            if (name) return true;

            name = true;
            return false;
        }

        fname = fileName + File.separator + fname + ".txt";
        if (Helper.makeDirectory(dirPath)) {
            try {
                FileOutputStream outputStream = new FileOutputStream(new File(fname));
                outputStream.write(string.getBytes());
                outputStream.close();

                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(
                        Objects.requireNonNull(getActivity())).edit();
                editor.putLong(fname, System.currentTimeMillis());
                Timber.v(fname + "made at " + System.currentTimeMillis());
                editor.apply();
                ReminderUtils.mySpaceReminder(activity, fname);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), R.string.try_again, Toast.LENGTH_SHORT).show();
            }
        }
        Timber.v("fileName = %s", fileName);
        return true;
    }


    private class Item {
        String mPath, mName;
        Class mClass;

        Item(String itemName, Class class1) {
            mPath = itemName;
            mName = itemName.endsWith(".txt") ? itemName.substring(0, itemName.length() - 4) : itemName;
            mClass = class1;
            Timber.i("Item set!");
        }
    }

    private void search(String stringToSearch) {
        String fullText = mySpaceEditText.getText().toString();
        boolean hasText = fullText.contains(stringToSearch);
        Timber.d("hasText = %s", hasText);
        if (hasText) {
            searchIndex = fullText.indexOf(stringToSearch, searchIndex);
            // -1 : not found. Happens after the last
            if (searchIndex == -1) {
                searchIndex = 0;
                Toast.makeText(activity, R.string.search_from_start, Toast.LENGTH_SHORT).show();
            }

            int lineNumber = mySpaceEditText.getLayout().getLineForOffset(searchIndex);
            int totalLines = mySpaceEditText.getLayout().getLineCount();
            int editTextViewBottom = mySpaceEditText.getBottom();
            ((ScrollView) rootView.findViewById(R.id.my_space_scroll_view))
                    .smoothScrollTo(0, editTextViewBottom * (lineNumber-1) / totalLines);
            searchIndex++;
            return;
        }
        Toast.makeText(activity, R.string.not_found, Toast.LENGTH_SHORT).show();
    }

    public void search() {
        String stringToSearch = searchEditText.getText().toString();
        searchIndex++;
        search(stringToSearch);
        searchEditText.setVisibility(View.VISIBLE);
        searchEditText.requestFocus();
        Timber.v("rootView.findViewById(R.id.search_edit_text_mySpaceFragment) visibility = %s", rootView.findViewById(R.id.search_edit_text_mySpaceFragment));
    }

    private class MySpaceAdapter extends ArrayAdapter<Item> {

        MySpaceAdapter(Activity context, ArrayList<Item> list) {
            super(context, 0, list);
        }

        @NonNull
        @Override
        public View getView(int position, View listItemView, @NonNull ViewGroup parent) {
            if (listItemView == null) listItemView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_file, parent, false);

            ((TextView) listItemView.findViewById(R.id.main_textView)).setText(
                    Objects.requireNonNull(getItem(position)).mName);

            return listItemView;
        }
    }
}