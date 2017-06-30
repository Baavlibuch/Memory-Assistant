package com.maniksejwal.memoryathletes.main.OpenFile;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.maniksejwal.memoryathletes.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import static android.text.InputType.TYPE_CLASS_NUMBER;
import static android.text.InputType.TYPE_CLASS_TEXT;
import static com.maniksejwal.memoryathletes.R.layout.activity_open_file;
import static com.maniksejwal.memoryathletes.R.string.a;
import static com.maniksejwal.memoryathletes.R.string.b;
import static com.maniksejwal.memoryathletes.R.string.c;
import static com.maniksejwal.memoryathletes.R.string.e;
import static com.maniksejwal.memoryathletes.R.string.i;
import static com.maniksejwal.memoryathletes.R.string.j;
import static com.maniksejwal.memoryathletes.R.string.k;
import static java.lang.Math.abs;


public class Recall extends AppCompatActivity {

    private static final String TAG = "Position : – –";
    private ArrayList<String> answers = new ArrayList<>();
    private ArrayList<String> responses = new ArrayList<>();
    //int mResponsePosition = 0;
    int correct = 0, wrong = 0, missed = 0;
    //static byte submitDoubt = 0;
    private byte hasWords = 0;
    private String mTextResponse = "";
    private String mTextAnswer = "";
    private Spinner mSpinner;
    private String mItem;
    private int mFormat = 0;
    private StringBuilder mta, mtr;
    //protected CompareAsyncTask task = new CompareAsyncTask(); //use if want to cancel the async task, don't remember how


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_open_file);
        setLayout();
        makeSpinner1();

        Log.v(TAG, "activity created");
    }

    void setLayout() {
        findViewById(R.id.text_answer_layout).setVisibility(View.GONE);
        findViewById(R.id.image_answer_layout).setVisibility(View.GONE);
        findViewById(R.id.result).setVisibility(View.GONE);
        findViewById(R.id.response_layout).setVisibility(View.GONE);
        findViewById(R.id.reset).setVisibility(View.GONE);

        Log.v(TAG, "layout set");
    }

    void makeSpinner1() {
        ArrayList<String> categories = new ArrayList<>();
        categories.add(getString(R.string.cd));
        categories.add(getString(R.string.f));
        categories.add(getString(e));
        categories.add(getString(k));
        categories.add(getString(R.string.d));
        categories.add(getString(b));
        categories.add(getString(R.string.g));
        categories.add(getString(c));
        categories.add(getString(j));
        categories.add(getString(R.string.h));
        categories.add(getString(i));
        categories.add(getString(a));

        final Spinner spinner = (Spinner) findViewById(R.id.discipline_spinner);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>
                (this, android.R.layout.simple_spinner_item, categories);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                makeSpinner2();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        Log.v(TAG, "spinner 1 set");
    }

    void spinnerReset() {
        //if (!answers.isEmpty()) answers.clear();
        //if (!responses.isEmpty()) responses.clear();
        //mResponsePosition = 0;
        correct = 0;
        wrong = 0;
        missed = 0;

    }

    void makeSpinner2() {
        spinnerReset();
        final String item = ((Spinner) findViewById(R.id.discipline_spinner)).getSelectedItem().toString();
        Log.v(TAG, "item : = = " + item);
        if (item == getString(R.string.cd)) return;

        final Spinner spinner = (Spinner) findViewById(R.id.chose_file);
        File dir = new File(getFilesDir().getAbsolutePath() + File.separator + item);
        ArrayList<String> fileList = new ArrayList<>();
        fileList.add(getString(R.string.cf));
        File[] files = dir.listFiles();
        if (files == null) {
            Toast.makeText(getApplicationContext(), "No saved entries found", Toast.LENGTH_SHORT).show();
            spinner.setVisibility(View.GONE);
            return;
        }
        spinner.setVisibility(View.VISIBLE);
        for (File file : files) {
            Log.d("Files", "FileName:" + file.getName());
            fileList.add(file.getName());
        }
        spinner.setAdapter(null);
        ArrayAdapter<String> fileAdapter = new ArrayAdapter<>
                (this, android.R.layout.simple_spinner_item, fileList);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spinner.getSelectedItem().toString() == getString(R.string.cf)) return;

                //getAnswers(spinner, item);
                mSpinner = spinner;
                mItem = item;
                EditText editText = (EditText) findViewById(R.id.response_input);
                switch (((Spinner) findViewById(R.id.discipline_spinner)).getSelectedItem().toString()) {
                    case "Numbers":
                    case "Binary Digits":
                        mFormat = 0;
                        editText.setRawInputType(TYPE_CLASS_NUMBER);
                        break;
                    case "Letters":
                    case "Words":
                    case "Names":
                    case "Places":
                        mFormat = 1;
                        editText.setRawInputType(TYPE_CLASS_TEXT);
                        break;
                    case "Cards":
                        mFormat = 2;
                        //TODO:Input for cards
                        break;
                }
                setResponseLayout();
                //findViewById(R.id.text_answer_layout).setVisibility(View.VISIBLE);

                /*
                TextView textView = (TextView) findViewById(R.id.responses_text);
                textView.setVisibility(View.VISIBLE);
                String text = "";
                for(String item : answers) text+=item + " \t";
                textView.setText(text);*/

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });
        fileAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(fileAdapter);

        Log.v(TAG, "spinner 2 set");
    }


    void getAnswers(Spinner spinner, String item) {
        try {
            String string;

            Scanner scanner = new Scanner(new File(getFilesDir().getAbsolutePath() +
                    File.separator + item + File.separator +
                    spinner.getSelectedItem().toString())).useDelimiter(" \t|\t  \t|\n|\n\n");

            while (scanner.hasNext()) {
                string = scanner.next();
                answers.add(string);
            }
            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.v(TAG, "getAnswers() complete");
    }

    String justAnswers() {
        Scanner scanner = null;
        String whitespace;
        switch (mFormat) {
            case 0:
                whitespace = " " + getString(R.string.tab);
                break;
            default:
                whitespace = " \n";
        }
        try {
            StringBuilder sb = new StringBuilder();
            scanner = new Scanner(new File(getFilesDir().getAbsolutePath() +
                    File.separator + mItem + File.separator +
                    mSpinner.getSelectedItem().toString())).useDelimiter(" \t|\t  \t|\n|\n\n");

            while (scanner.hasNext()) {
                sb.append(scanner.next()).append(whitespace);
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (scanner != null)
                scanner.close();
        }
        return null;
    }

    void setResponseLayout() {
        findViewById(R.id.response_layout).setVisibility(View.VISIBLE);
        final EditText editText = (EditText) findViewById(R.id.response_input);

    /*    findViewById(R.id.previous).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mResponsePosition > 0) {
                    editText.setHint("Enter value no. " + (--mResponsePosition + 1));
                } else {
                    Toast.makeText(Recall.this, "Not possible", Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setHint("Enter value no. " + (++mResponsePosition + 1));
                if (mResponsePosition >= responses.size()) responses.add(" ");
            }
        });
*/
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Log.v(TAG, "onEditorAction started");
                getResponse();
                return true;
            }
        });


        findViewById(R.id.check).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                (new CompareAsyncTask()).execute();
            }
        });

        findViewById(R.id.just_answers).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                (new JustAnswersAsyncTask()).execute();
            }
        });

        Log.v(TAG, "responseLayout set");
    }

    void getResponse() {
        EditText editText = (EditText) findViewById(R.id.response_input);
        String values = editText.getText().toString(), value = "";
        //while (responses.size() <= mResponsePosition) responses.add(" ");
        char delimiter = (mFormat == 0 ? ' ' : '\n');
        for (int i = 0; i<values.length(); i++){
            if(!(values.charAt(i)==delimiter)) {
                value += values.charAt(i);
            }
            if(i+1==values.length()) {
                responses.add(value);
                continue;
            }
                if ((values.charAt(i) == delimiter && values.charAt(i - 1) != delimiter)) {
                    responses.add(value);
                    value = "";
                    continue;
                }
            if((values.charAt(i)==delimiter && values.charAt(i+1)==delimiter)){
                responses.add(" ");
                value = "";
            }
        }
                //responses.set(mResponsePosition, value);
        String text = ((TextView) findViewById(R.id.responses_text)).getText() + " " + getString(R.string.tab) + value;
        ((TextView) findViewById(R.id.responses_text)).setText(text);
        Log.v(TAG, "onEditorAction complete");
        editText.setText("");
        //editText.setHint("Enter value no. " + (++mResponsePosition + 1));
    }

    void hideResponseLayout() {
        findViewById(R.id.response_layout).setVisibility(View.GONE);
        //findViewById(R.id.check).setVisibility(View.GONE);
        findViewById(R.id.discipline_spinner).setVisibility(View.GONE);
        findViewById(R.id.chose_file).setVisibility(View.GONE);
        findViewById(R.id.reset).setVisibility(View.VISIBLE);
        findViewById(R.id.text_answer_layout).setVisibility(View.VISIBLE);
        findViewById(R.id.progress_bar_recall).setVisibility(View.GONE);

    }


    void compare(boolean words) {
        mTextAnswer = "";
        mTextResponse = "";
        mta = mtr = new StringBuilder();
        for (int i = 0, j = 0; i < responses.size() && j < answers.size(); i++, j++) {
            Log.v(TAG, "Entered loop - response, answer no. ––" + responses.get(i) + " " + answers.get(j));
            if (isLeft(i, j)) continue;
            if (isCorrect(i, j)) continue;
            if (words) {
                if (isSpelling(i, j)) {
                    mta.append("<font color=#FFFF00>").append(answers.get(j)).append("</font>").append(" ").append(getString(R.string.tab));
                    mtr.append("<font color=#FFFF00>").append(responses.get(i)).append("</font>").append(" ").append(getString(R.string.tab));
                    correct++;
                    continue;
                }
            }
            if(isMissOrWrong(i, j)) i--;
        }
        Log.v(TAG, "submit complete");
    }

    boolean isLeft(int i, int j) {
        if (responses.get(i).equals(" ")) {
            missed++;
            mta.append(answers.get(j)).append(" ").append(getString(R.string.tab));
            mtr.append("<font color=#FF9500>").append(answers.get(j)).append("</font>").append(" ").append(getString(R.string.tab));
            return true;
        } else return false;
    }

    boolean isCorrect(int i, int j) {
        if (responses.get(i).equalsIgnoreCase(answers.get(j))) {
            correct++;
            mta.append(answers.get(j)).append(" ").append(getString(R.string.tab));
            mtr.append(responses.get(i)).append(" ").append(getString(R.string.tab));
            return true;
        } else return false;
    }

    boolean isMissOrWrong(int i, int j) {
        boolean miss = false;
        for (int l = 1; l <= (answers.size() - responses.size()) / 10; l++) {
            int match = 0, k=l;
            for (; k <= 10 && i + k < responses.size() && j + k < answers.size(); k++) {
                //if (i + k < responses.size() && j + k < answers.size()) {
                    if (!responses.get(i + k).equals(" ")) {
                        if (responses.get(i + k).equalsIgnoreCase(answers.get(j + k))) {
                            match++;
                        }
                    }
                //}
            }

            if (((float) match/k) > 0.5) {
                wrong++;
                mta.append("<font color=#FF0000>").append(answers.get(j)).append("</font>").append(" ").append(getString(R.string.tab));
                mtr.append("<font color=#FF0000>").append(responses.get(i)).append("</font>").append(" ").append(getString(R.string.tab));
                miss = false;
                break;
            } else if (((float) match / k) <= 0.5) {
                missed++;
                miss = true;
                break;
            }
        }
        if (miss) {
            mta.append(answers.get(j)).append(" ").append(getString(R.string.tab));
            mtr.append("<font color=#FF9500>").append(answers.get(j)).append("</font>").append(" ").append(getString(R.string.tab));
            //mTextResponse += /*"<font color=#FF0000>" +*/ responses.get(i) /*+ "</font>"*/ + " " + getString(R.string.tab);
        }
        return miss;
    }

    boolean isSpelling(int i, int j) {
        if ((float) abs((responses.get(i).length() - answers.get(j).length()) / answers.get(j).length()) > 0.3)
            return false;
        else {
            int count = 0;
            for (int a = 0; a < responses.get(i).length() && a < answers.get(j).length(); a++) {
                if (responses.get(i).charAt(a) == answers.get(j).charAt(a)) {
                    count++;
                    continue;
                }
                for (int b = -2; b < 2; b++) {
                    if (a + b > 0 && a + b < responses.get(i).length() && a + b < answers.get(j).length()) {
                        if (responses.get(i).charAt(a) == answers.get(j).charAt(a + b)) {
                            count++;
                            break;
                        }
                    }
                }
            }
            return ((float) count / answers.get(j).length()) > 0.7;
        }
    }

    void compareCards() {} //TODO: Its empty


    private void reset() {
        setLayout();
        findViewById(R.id.discipline_spinner).setVisibility(View.VISIBLE);
        findViewById(R.id.chose_file).setVisibility(View.VISIBLE);
        findViewById(R.id.response_layout).setVisibility(View.VISIBLE);
        answers.clear();
        responses.clear();
        mTextAnswer = "";
        mTextResponse = "";
        ((TextView) findViewById(R.id.responses_text)).setText("");
        ((TextView) findViewById(R.id.answers_text)).setText("");
        ((Chronometer) findViewById(R.id.time_elapsed_value)).stop();
        correct = 0; wrong = 0; missed = 0;

        Log.v(TAG, "Recall Reset Complete");
    }

    public void reset(View view){reset();}

    public void startSW(View view){
        ((Chronometer) findViewById(R.id.time_elapsed_value)).start();
        findViewById(R.id.time_elapsed_value).setVisibility(View.VISIBLE);
        findViewById(R.id.result).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.time_elapsed_header)).setText(R.string.time_elapsed);
    }

    public void startTimer(View view){} //TODO: USe this!

    private class CompareAsyncTask extends AsyncTask<ArrayList<Integer>, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            findViewById(R.id.progress_bar_recall).setVisibility(View.VISIBLE);
            getResponse();
            switch (((Spinner) findViewById(R.id.discipline_spinner)).getSelectedItem().toString()) {
                case "Numbers":
                case "Letters":
                case "Binary Digits":
                    hasWords=0;
                    //compare(false);
                    break;
                case "Words":
                case "Names":
                case "Places":
                    hasWords=1;
                    //compare(true);
                    break;
                case "Cards":
                    hasWords=2;
                    //compareCards();
                    break;
            }
        }

        @SafeVarargs
        @Override
        protected final Void doInBackground(ArrayList<Integer>... a) {
            getAnswers(mSpinner, mItem);
            if (hasWords==0) compare(false);
            else if (hasWords==1) compare(true);
            else compareCards();
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            hideResponseLayout();
            findViewById(R.id.responses_text).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.responses_text)).setText(Html.fromHtml(mTextResponse),
                    TextView.BufferType.SPANNABLE);
            findViewById(R.id.answers_text).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.answers_text)).setText(Html.fromHtml(mTextAnswer),
                    TextView.BufferType.SPANNABLE);
            findViewById(R.id.result).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.no_of_correct)).setText(Integer.toString(correct));
            ((TextView) findViewById(R.id.no_of_wrong)).setText(Integer.toString(wrong));
            ((TextView) findViewById(R.id.value_of_score)).setText(Integer.toString(correct - 10*(wrong+missed)));
            InputMethodManager imm = (InputMethodManager)getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }
    }

    private class JustAnswersAsyncTask extends AsyncTask<Void, String, String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            findViewById(R.id.progress_bar_recall).setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {
            return justAnswers();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s==null){
                Toast.makeText(getApplicationContext(), "Error reading the answers", Toast.LENGTH_SHORT).show();
                reset();
                return;
            }
            findViewById(R.id.responses_text).setVisibility(View.GONE);
            hideResponseLayout();
            TextView textAnswers = (TextView) findViewById(R.id.answers_text);
            textAnswers.setText(s);
            textAnswers.setVisibility(View.VISIBLE);
        }
    }
}

//TODO: key

//orange:FFA500
// textView.setText(Html.fromHtml(styledText), TextView.BufferType.SPANNABLE);