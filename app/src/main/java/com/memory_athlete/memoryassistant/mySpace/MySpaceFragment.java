package com.memory_athlete.memoryassistant.mySpace;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.memory_athlete.memoryassistant.R;
import com.memory_athlete.memoryassistant.reminders.ReminderUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;

import timber.log.Timber;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MySpaceFragment extends Fragment {
    int fragListViewId = 0;
    File dir = null;
    String title = "", fileName;
    Boolean name;
    View rootView;

    public MySpaceFragment() {}

    public boolean save(){
        if (rootView.findViewById(R.id.f_name).getVisibility()!=VISIBLE) return true;
        Timber.v("Received back from the activity");
        return save(rootView);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //if(savedInstanceState != null){}
        rootView = inflater.inflate(R.layout.fragment_my_space, container, false);
        rootView.findViewById(R.id.add).setVisibility(GONE);//.removeViewAt(0);
        //if (fragListViewId > 0)
        //  ((RelativeLayout) rootView.findViewById(R.id.my_space_relative_layout)).removeViewAt(fragListViewId);

        fragListViewId += 3;                          //There are three other views with ids 0,1,2
        setAdapter(rootView);
        backButton(rootView);
        return rootView;
    }

    ArrayList<Item> setList() {
        ArrayList<Item> list = new ArrayList<>();
        list.add(new Item(getActivity().getString(R.string.majors), WriteFile.class));
        list.add(new Item(getActivity().getString(R.string.ben), WriteFile.class));
        list.add(new Item(getActivity().getString(R.string.wardrobes), WriteFile.class));
        list.add(new Item(getActivity().getString(R.string.lists), WriteFile.class));
        list.add(new Item(getActivity().getString(R.string.words), WriteFile.class));
        //TODO:
        //list.add(new Item(getString(R.string.equations), WriteEquations.class));
        //list.add(new Item(getString(R.string.algos), WriteAlgo.class));
        //list.add(new Item(getString(R.string.derivations), WriteEquations.class));
        return list;
    }

    public void setAdapter(final View rootView) {
        Timber.v("setAdapter started");
        ArrayList<Item> arrayList = new ArrayList<>();
        if (fragListViewId == 0) {
            getActivity().finish();
            Timber.wtf("increment fragListViewId in onCreateView()");
        }
        if (fragListViewId == 3) arrayList = setList();
        else {
            if (dir==null) {
                Toast.makeText(getActivity(), "Please try again", Toast.LENGTH_SHORT).show();
                return;
            }
            File[] files = dir.listFiles();
            if (files == null) {
                return;
            }
            if (files.length == 0) {
                return;
            } else {
                for (File file : files) {
                    Timber.d("FileName: " + file.getName());
                    arrayList.add(new Item(file.getName(), WriteFile.class));
                }
            }
        }
        Timber.v("list set");
        MySpaceAdapter adapter = new MySpaceAdapter(getActivity(), arrayList);
        final ListView listView = new ListView(getActivity());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        if (fragListViewId == 3) {
            float scale = getActivity().getResources().getDisplayMetrics().density;
            int dpAsPixels = (int) (16 * scale + 0.5f);
            layoutParams.setMargins(dpAsPixels, dpAsPixels, dpAsPixels, dpAsPixels);
        }
        listView.setLayoutParams(layoutParams);
        //if (listViewId==1) listView.MarginLayoutParams
        listView.setId(fragListViewId);
        final RelativeLayout layout = rootView.findViewById(R.id.my_space_relative_layout);
        layout.addView(listView);
        listView.setAdapter(adapter);
        final ArrayList<Item> finalArrayList = arrayList;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                Timber.d("listView id = " + listView.getId());
                Item item = finalArrayList.get(position);
                Timber.v("item.mPath = " + item.mPath);
                if (fragListViewId == 3) {
                    dir = new File(getActivity().getFilesDir().getAbsolutePath() + File.separator
                            + getString(R.string.my_space) + File.separator + item.mPath);
                    layout.findViewById(fragListViewId).setVisibility(View.GONE);
                    fragListViewId++;
                    title = item.mName;
                    rootView.findViewById(R.id.add).setVisibility(View.VISIBLE);
                    setAdapter(rootView);
                    Timber.v("going to id 1, listViewId = " + fragListViewId);
                    rootView.findViewById(R.id.back_button).setVisibility(View.VISIBLE);
                    rootView.findViewById(R.id.back_button).bringToFront();
                } else {
                    Timber.v("listViewId = " + fragListViewId);
                    fileName = getActivity().getFilesDir().getAbsolutePath() + File.separator
                            + getString(R.string.my_space) + File.separator + title;
                    //Intent intent = new Intent(getApplicationContext(), WriteFile.class);
                    //intent.putExtra("mHeader", item.mName);
                    //intent.putExtra("fileString", item.mItem);
                    //intent.putExtra("fileName", fileName);
                    File file = new File(fileName);
                    boolean isDirectoryCreated = file.exists();
                    if (!isDirectoryCreated) {
                        isDirectoryCreated = file.mkdir();
                    }
                    if (isDirectoryCreated) {
                        name = true;
                        //rootView.findViewById(R.id.back_button).setVisibility(View.GONE);
                        rootView.findViewById(R.id.add).setVisibility(View.GONE);
                        rootView.findViewById(fragListViewId++).setVisibility(View.GONE);
                        rootView.findViewById(R.id.f_name).setVisibility(View.VISIBLE);
                        rootView.findViewById(R.id.my_space_editText).setVisibility(View.VISIBLE);
                        writeFile(rootView, fileName, item.mName);
                    } else
                        Toast.makeText(getActivity(), "Try again", Toast.LENGTH_SHORT).show();
                    rootView.findViewById(R.id.back_button).bringToFront();
                }
            }
        });
    }

    void backButton(final View rootView) {
        rootView.findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Timber.v("back_button clicked, fragListViewId = " + fragListViewId);
                if (rootView.findViewById(R.id.f_name).getVisibility() == VISIBLE) {
                    Timber.v("fileName = " + fileName);
                    if (!save(rootView)) return;
                    rootView.findViewById(R.id.f_name).setVisibility(GONE);
                    rootView.findViewById(R.id.my_space_editText).setVisibility(GONE);
                }
                if (rootView.findViewById(fragListViewId) != null) {
                    ((RelativeLayout) rootView).removeViewAt(fragListViewId);
                    Timber.v("Removed view at fragListViewId " + fragListViewId);
                }
                if (rootView.findViewById(--fragListViewId) != null) {
                    rootView.findViewById(fragListViewId).setVisibility(View.VISIBLE);
                    if (fragListViewId == 3) {
                        rootView.findViewById(R.id.add).setVisibility(View.GONE);
                        rootView.findViewById(R.id.back_button).setVisibility(GONE);
                    } else rootView.findViewById(R.id.add).setVisibility(VISIBLE);
                }
            }
        });

        rootView.findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timber.v("add button clicked");
                name = false;
                fileName = getActivity().getFilesDir().getAbsolutePath() + File.separator
                        + getString(R.string.my_space) + File.separator + title;
                rootView.findViewById(R.id.add).setVisibility(View.GONE);
                rootView.findViewById(fragListViewId++).setVisibility(View.GONE);
                rootView.findViewById(R.id.f_name).setVisibility(View.VISIBLE);
                rootView.findViewById(R.id.my_space_editText).setVisibility(View.VISIBLE);
                writeFile(rootView, fileName, title);
            }
        });
    }

    void writeFile(View rootView, String path, String header) {
        ((TextView) rootView.findViewById(R.id.my_space_editText)).setText("");
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
                ((EditText) rootView.findViewById(R.id.my_space_editText)).setText(text);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Try again", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        }
        //intent.getStringExtra()
    }

    public boolean save(View rootView) {
        Timber.v("entered save()");
        String string = ((EditText) rootView.findViewById(R.id.my_space_editText)).getText().toString();
        String fname = ((EditText) rootView.findViewById(R.id.f_name)).getText().toString();
        if (fname.length() == 0) {
            if (!name) {
                Toast.makeText(getActivity(), "please enter a name", Toast.LENGTH_SHORT).show();
                name = true;
                return false;
            }
            Toast.makeText(getActivity(), "Didn't save nameless file", Toast.LENGTH_SHORT).show();
            return true;
        }
        String dirPath = fileName;
        if (fname.length() > 250) {
            if (!name) {
                Toast.makeText(getActivity(), "Try again with a shorter name", Toast.LENGTH_SHORT).show();
                name = true;
                return false;
            } else return true;
        }

        fname = fileName + File.separator + fname + ".txt";
        Timber.v("fname = " + fname);
        File pDir = new File(dirPath);
        boolean isDirectoryCreated = pDir.exists();
        if (!isDirectoryCreated) {
            isDirectoryCreated = pDir.mkdir();
        }
        if (isDirectoryCreated) {
            try {
                FileOutputStream outputStream = new FileOutputStream(new File(fname));
                outputStream.write(string.getBytes());
                outputStream.close();

                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                editor.putLong(fname, System.currentTimeMillis());
                Timber.v(fname + "made at " + System.currentTimeMillis());
                editor.apply();
                ReminderUtils.mySpaceReminder(getActivity(), fname);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), R.string.try_again, Toast.LENGTH_SHORT).show();
            }
        } else Toast.makeText(getActivity(),
                R.string.try_again, Toast.LENGTH_SHORT).show();
        Timber.v("fileName = " + fileName);
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

    private class MySpaceAdapter extends ArrayAdapter<Item> {

        MySpaceAdapter(Activity context, ArrayList<Item> list) {
            super(context, 0, list);
        }

        @Override
        public View getView(int position, View listItemView, ViewGroup parent) {
            if (listItemView == null) {
                listItemView = LayoutInflater.from(getContext()).inflate(R.layout.main_item, null, true);
            }

            TextView textView = listItemView.findViewById(R.id.main_textView);
            textView.setText(getItem(position).mName);

            return listItemView;
        }
    }
}

