package com.memory_athlete.memoryassistant.recall;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.memory_athlete.memoryassistant.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

import timber.log.Timber;

public class RecallComplex extends RecallSimple {
    ArrayList<RecallObject> recallList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        (new LoadAnswersAsyncTask()).execute();
        responseFormat = DATE_RESPONSE_FORMAT;
        recallList = new ArrayList<>();
    }

    @Override
    protected void setResponseLayout(boolean onCreate) {
        Timber.v("setResponseLayout() entered");
        if (onCreate) {
            findViewById(R.id.progress_bar_recall).setVisibility(View.VISIBLE);
            findViewById(R.id.response_layout).setVisibility(View.GONE);
            return;
        }

        gridView.setVisibility(View.GONE);
        findViewById(R.id.card_suit).setVisibility(View.GONE);
        findViewById(R.id.card_numbers).setVisibility(View.GONE);

        findViewById(R.id.response_input).setVisibility(View.GONE);
        findViewById(R.id.recall_layout).setVisibility(View.GONE);
        findViewById(R.id.progress_bar_recall).setVisibility(View.GONE);
        findViewById(R.id.reset).setVisibility(View.GONE);
        findViewById(R.id.response_layout).setVisibility(View.VISIBLE);
        findViewById(R.id.button_bar).setVisibility(View.VISIBLE);

        findViewById(R.id.complex_recall_list_view).setVisibility(View.GONE);
        complexListView = findViewById(R.id.response_list_view);
        complexListView.setVisibility(View.VISIBLE);
        complexListView.setAdapter(new ResponseAdapter(this, answers));
        Timber.v("answers.size() = " + answers.size());
        Timber.v("setResponseLayout() complete");
    }

    @Override
    protected void getAnswers() throws FileNotFoundException {
        if (answers.size() > 0) {
            Timber.i("getAnswers() is returning early because the answers have already been read");
            return;
        }
        Timber.v("getAnswersEntered");
        String string;

        Scanner scanner = new Scanner(new File(mFilePath)).useDelimiter("\n\n|\n");

        while (scanner.hasNext()) {
            string = scanner.next();
            answers.add(string);
        }
        Collections.shuffle(answers);
        Timber.v("answers.size() = " + String.valueOf(answers.size()));
        scanner.close();

        Timber.v("getAnswers() complete");
    }

    @Override
    protected String formatAnswers(Scanner scanner, StringBuilder sb, String whitespace) {
        while (scanner.hasNext()) {
            String s = scanner.next();
            s = s.replace("\t", "\n");
            sb.append(s).append(whitespace);
        }
        Timber.v("formatAnswers() complete");
        return sb.toString();
    }

    @Override
    protected void getResponse() {
        try {
            EditText editText = (EditText) this.getCurrentFocus();
            responses.set(Integer.parseInt(editText.getTag().toString()), editText.getText().toString());
        } catch (Exception ignore) {
            // ListView is returned instead of EditText, the data has been saved already
            // OR
            // None of the views has the focus so null is returned. Empty list will be compared
        }
    }

    @Override
    protected void compare(boolean words) {
        whitespace = getString(R.string.tab);

        for (int i = 0; i < answers.size(); i++) {
            Timber.v("Entered loop " + i);
            if (isCorrect(i)) continue;
            if (missed > 8 && missed > correct) break;
            if (isLeft(i)) continue;
            // TODO : add spell check
            isWrong(i);
        }
        Timber.v("compare() complete");
    }

    protected boolean isLeft(int i) {
        String event = answers.get(i).split(" - ")[0];
        if (responses.get(i).equals("") || responses.get(i).equals(" ")) {
            missed++;
            recallList.add(new RecallObject(answers.get(i).split(" - ")[1],
                    responses.get(i),"<font color=#FF9500>" +  event + "</font>"));
            return true;
        }
        return false;
    }

    protected boolean isCorrect(int i) {
        String event = answers.get(i).split(" - ")[0];
        if (responses.get(i).equalsIgnoreCase(event)) {
            correct++;
            recallList.add(new RecallObject(answers.get(i).split(" - ")[1], responses.get(i),
                    event));
            return true;
        }
        return false;
    }

    protected void isWrong(int i) {
        String event = answers.get(i).split(" - ")[0];
        wrong++;
        recallList.add(new RecallObject(answers.get(i).split(" - ")[1],
                "<font color=#FF0000>" + responses.get(i) + "</font>",
                "<font color=#FF0000>" + event + "</font>"));
    }

    @Override
    protected void postExecuteCompare() {
        ListView recallListView = findViewById(R.id.complex_recall_list_view);
        RecallAdapter recallAdapter = new RecallAdapter(this, recallList);
        recallListView.setAdapter(recallAdapter);

        recallListView.setVisibility(View.VISIBLE);
        findViewById(R.id.recall_layout).setVisibility(View.GONE);
    }

    @Override
    protected void reset() {
        mTextAnswer = new StringBuilder("");
        mTextResponse = new StringBuilder("");
        correct = 0;
        wrong = 0;
        missed = 0;
        recallList.clear();

        setResponseLayout(false);

        ((Chronometer) findViewById(R.id.time_elapsed_value)).stop();
        findViewById(R.id.result).setVisibility(View.GONE);
        findViewById(R.id.recall_layout).setVisibility(View.GONE);
        findViewById(R.id.reset).setVisibility(View.GONE);
        findViewById(R.id.button_bar).setVisibility(View.VISIBLE);

        Timber.v("Recall Reset Complete");
    }

    class LoadAnswersAsyncTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            findViewById(R.id.progress_bar_recall).setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                getAnswers();
                return false;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return true;
            }
        }

        @Override
        protected void onPostExecute(Boolean error) {
            super.onPostExecute(error);
            if (error) {
                Toast.makeText(getApplicationContext(), "Please try again", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            setResponseLayout(false);
        }
    }

    class ResponseAdapter extends ArrayAdapter<String> {

        ResponseAdapter(Activity context, ArrayList<String> words) {
            super(context, 0, words);
            for (int i = 0; i < answers.size(); i++) responses.add("");
        }

        @SuppressLint("ResourceType")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_date, parent, false);

            TextView textView = convertView.findViewById(R.id.event);
            textView.setText(getItem(position).split(" - ")[1].trim());

            final EditText editText = convertView.findViewById(R.id.date);
            editText.setText(responses.get(position));
            editText.setTag(Integer.toString(position));

            editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_NEXT)
                        ((ListView) (editText.getParent()).getParent()).smoothScrollToPosition(
                                2 + Integer.parseInt(editText.getTag().toString()));
                    return false;
                }
            });

            editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) return;
                    int position = Integer.parseInt(editText.getTag().toString());
                    responses.set(position, editText.getText().toString());
                }
            });

            return convertView;
        }
    }

    class RecallAdapter extends ArrayAdapter<RecallObject>{
        RecallAdapter(Activity context, ArrayList<RecallObject> list) {
            super(context, 0, list);
        }

        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_complex_recall, parent, false);

            TextView eventTextView = convertView.findViewById(R.id.event);
            eventTextView.setVisibility(View.VISIBLE);

            eventTextView.setText(getItem(position).getKey());
            ((TextView) convertView.findViewById(R.id.response_text)).setText(Html.fromHtml(
                    getItem(position).getResponse()), TextView.BufferType.SPANNABLE);
            ((TextView) convertView.findViewById(R.id.answer_text)).setText(Html.fromHtml(
                    getItem(position).getAnswer()), TextView.BufferType.SPANNABLE);

            return convertView;
        }
    }

    private class RecallObject{
        private String key, response, answer;

        RecallObject(String key, String response, String answer){
            this.key = key;
            this.response = response;
            this.answer = answer;
        }

        public String getKey() {
            return key;
        }

        public String getAnswer() {
            return answer;
        }

        public String getResponse() {
            return response;
        }
    }
}

//TODO minimise response text
//TODO check display
