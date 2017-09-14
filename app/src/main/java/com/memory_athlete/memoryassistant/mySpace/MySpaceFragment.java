package com.memory_athlete.memoryassistant.mySpace;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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

import static android.view.View.GONE;

/**
 * A simple {@link Fragment} subclass.
 */
public class MySpaceFragment extends Fragment {
    int fragListViewId = 0;
    File dir = null;
    String title = "";

    public interface FileSelected {
        void openWriteFile(String mName, String mPath, String fileName);
    }

    FileSelected mCallback = new FileSelected(){
        public void openWriteFile(String mName, String mPath, String fileName) {
            Timber.v("Entered openWriteFile");
            WriteFileFragment mySpaceFragment = new WriteFileFragment();
            Bundle bundle = new Bundle();
            bundle.putString("mHeader", mName);
            bundle.putString("fileString", mPath);
            bundle.putString("path", fileName);
            mySpaceFragment.setArguments(bundle);
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            Timber.v("beginning transaction");
            fragmentManager.beginTransaction().replace(R.id.my_space_fragment, mySpaceFragment).commit();
        }
    };

    public MySpaceFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //if(savedInstanceState != null){}
        View rootView = inflater.inflate(R.layout.activity_my_space, container, false);
        //final ImageView imageView = (ImageView) rootView.findViewById(R.id.body_part_image_view);
        rootView.findViewById(R.id.floatingActionButton).setVisibility(GONE);//.removeViewAt(0);

        if (fragListViewId > 0)
            ((RelativeLayout) rootView.findViewById(R.id.my_space_relative_layout)).removeViewAt(fragListViewId);
        setAdapter(rootView);
        backButton(rootView);
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
                Timber.v("item.mPath = " + item.mPath);
                if (fragListViewId == 0) {
                    dir = new File(getActivity().getFilesDir().getAbsolutePath() + File.separator + item.mPath);
                    layout.findViewById(fragListViewId).setVisibility(GONE);
                    fragListViewId++;
                    getActivity().setTitle(Html.fromHtml(title + item.mName));
                    rootView.findViewById(R.id.floatingActionButton).setVisibility(View.VISIBLE);
                    setAdapter(rootView);
                    //rootView.findViewById(R.id.back_button).setVisibility(View.VISIBLE);
                    Timber.v("going to id 1, listViewId = " + fragListViewId);
                } else {
                    Timber.v("listViewId = " + fragListViewId);
                    String fileName = getActivity().getFilesDir().getAbsolutePath()
                            + File.separator + getActivity().getTitle();
                    Intent intent = new Intent(getActivity(), WriteFile.class);
                    intent.putExtra("mHeader", item.mName);
                    intent.putExtra("fileString", item.mPath);
                    intent.putExtra("path", fileName);
                    File file = new File(fileName);
                    boolean isDirectoryCreated = file.exists();
                    if (!isDirectoryCreated) {
                        isDirectoryCreated = file.mkdir();
                    }
                    if (isDirectoryCreated) {
                        Timber.v("now calling back");
                        mCallback.openWriteFile(item.mName, item.mPath, fileName);
                        //startActivity(intent);
                    } else
                        Toast.makeText(getActivity(), "Try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void backButton(final View rootView){
        rootView.findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fragListViewId==0) {
                    Timber.v("fragListViewId = " + fragListViewId);
                } else {
                    Timber.v("listViewId = " + fragListViewId);
                    RelativeLayout relativeLayout = rootView.findViewById(R.id.my_space_relative_layout);
                    relativeLayout.removeViewAt(fragListViewId);
                    relativeLayout.findViewById(--fragListViewId).setVisibility(View.VISIBLE);
                    getActivity().setTitle(Html.fromHtml(title + getString(R.string.my_space)));
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
