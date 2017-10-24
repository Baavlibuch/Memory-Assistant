package com.memory_athlete.memoryassistant.mySpace;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.memory_athlete.memoryassistant.R;

import java.io.File;
import java.util.ArrayList;

import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class MySpaceFragment extends Fragment {
    int fragListViewId = 0;
    File dir = null;
    String title = "";


    public MySpaceFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //if(savedInstanceState != null){}
        View rootView = inflater.inflate(R.layout.activity_my_space, container, false);
        //final ImageView imageView = (ImageView) rootView.findViewById(R.id.body_part_image_view);
        ((RelativeLayout) rootView).removeViewAt(0);

        if (fragListViewId > 0) {
            ((RelativeLayout) rootView.findViewById(R.id.my_space_relative_layout)).removeViewAt(fragListViewId);
        }
        setAdapter(rootView);
        return rootView;
    }

    public void setAdapter(final View rootView) {
        Timber.v("setAdapter started");
        ArrayList<Item> arrayList = new ArrayList<>();
        if (fragListViewId == 0) arrayList = setList();
        else {
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
        listView.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        listView.setId(fragListViewId);
        final RelativeLayout layout = rootView.findViewById(R.id.my_space_relative_layout);
        layout.addView(listView);
        listView.setAdapter(adapter);
        final ArrayList<Item> finalArrayList = arrayList;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                Item item = finalArrayList.get(position);
                Timber.v("item.mItem = " + item.mItem);
                if (fragListViewId == 0) {
                    dir = new File(getActivity().getFilesDir().getAbsolutePath() + File.separator + item.mItem);
                    layout.findViewById(fragListViewId).setVisibility(View.GONE);
                    fragListViewId++;
                    getActivity().setTitle(Html.fromHtml(title + item.mName));
                    rootView.findViewById(R.id.floatingActionButton).setVisibility(View.VISIBLE);
                    setAdapter(rootView);
                    Timber.v("going to id 1, listViewId = " + fragListViewId);
                } else {
                    Timber.v("listViewId = " + fragListViewId);
                    String fileName = getActivity().getFilesDir().getAbsolutePath()
                            + File.separator + getActivity().getTitle();
                    Intent intent = new Intent(getActivity(), WriteFile.class);
                    intent.putExtra("mHeader", item.mName);
                    intent.putExtra("fileString", item.mItem);
                    intent.putExtra("path", fileName);
                    File file = new File(fileName);
                    boolean isDirectoryCreated = file.exists();
                    if (!isDirectoryCreated) {
                        isDirectoryCreated = file.mkdir();
                    }
                    if (isDirectoryCreated) {
                        startActivity(intent);
                    } else
                        Toast.makeText(getActivity(), "Try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    ArrayList<Item> setList() {
        ArrayList<Item> list = new ArrayList<>();
        list.add(new Item(getString(R.string.majors), WriteFile.class));
        list.add(new Item(getString(R.string.ben), WriteFile.class));
        list.add(new Item(getString(R.string.wardrobes), WriteFile.class));
        list.add(new Item(getString(R.string.lists), WriteFile.class));
        list.add(new Item(getString(R.string.words), WriteFile.class));
        //TODO:
        //list.add(new Item(getString(R.string.equations), WriteEquations.class));
        //list.add(new Item(getString(R.string.algos), WriteAlgo.class));
        //list.add(new Item(getString(R.string.derivations), WriteEquations.class));
        return list;
    }

    private class Item {
        String mItem, mName;
        Class mClass;

        Item(String itemName, Class class1) {
            mItem = itemName;
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
