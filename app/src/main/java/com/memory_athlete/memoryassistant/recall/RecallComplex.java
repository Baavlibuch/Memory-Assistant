package com.memory_athlete.memoryassistant.recall;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        (new LoadAnswersAsyncTask()).execute();
        responseFormat = DATE_RESPONSE_FORMAT;
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
        findViewById(R.id.response_layout).setVisibility(View.VISIBLE);
        findViewById(R.id.recall_layout).setVisibility(View.GONE);
        findViewById(R.id.progress_bar_recall).setVisibility(View.GONE);
        findViewById(R.id.button_bar).setVisibility(View.VISIBLE);
        findViewById(R.id.reset).setVisibility(View.GONE);

        complexListView = findViewById(R.id.response_list_view);
        complexListView.setVisibility(View.VISIBLE);
        complexListView.setAdapter(new ResponseAdapter(this, answers));
        Timber.v("setResponseLayout() complete");
    }

    protected void getAnswers() throws FileNotFoundException {
        Timber.v("getAnswersEntered");
        String string;

        Scanner scanner = new Scanner(new File(mFilePath)).useDelimiter("\n\n|\n");

        while (scanner.hasNext()) {
            string = scanner.next();
            answers.add(string);
        }
        Collections.shuffle(answers);
        Timber.v(String.valueOf(answers.size()));
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
        }catch (ClassCastException ignore){}        // ListView is returned instead of EditText, the data has been saved already
    }

    @Override
    protected void compare(boolean words) {
        mTextResponse = new StringBuilder("");
        mTextAnswer = new StringBuilder("");
        whitespace = getString(R.string.tab);

        int i = 0, j = 0;
        for (; i < responses.size() && j < answers.size(); i++, j++) {
            Timber.v("Entered loop " + (i + j) + " - response " + responses.get(i) + ", answer " + answers.get(j));
            if (isCorrect(i, j)) continue;
            if (missed > 8 && missed > correct) break;
            if (isLeft(i, j)) continue;
            // TODO : add spell check
            isWrong(i, j);
        }
        Timber.v("compare() complete");
    }

    @Override
    protected boolean isLeft(int i, int j) {
        String event = answers.get(j).split(" - ")[0];
        if (responses.get(i).equals("")) {
            missed++;
            mTextAnswer.append(event).append(" ").append(whitespace);
            mTextResponse.append("<font color=#FF9500>").append(event).append("</font>").append(" ")
                    .append(whitespace);
            return true;
        } else return false;
    }

    @Override
    protected boolean isCorrect(int i, int j) {
        String event = answers.get(j).split(" - ")[0];
        if (responses.get(i).equalsIgnoreCase(answers.get(j))) {
            correct++;
            mTextAnswer.append(event).append(" ").append(whitespace);
            mTextResponse.append(responses.get(i)).append(" ").append(whitespace);
            return true;
        } else return false;
    }

    @Override
    protected void isWrong(int i, int j) {
        String event = answers.get(j).split(" - ")[0];
        wrong++;
        mTextAnswer.append("<font color=#FF0000>").append(event).append("</font>")
                .append(" ").append(whitespace);
        mTextResponse.append("<font color=#FF0000>").append(responses.get(i))
                .append("</font>").append(" ").append(whitespace);
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
            if (!error) {
                setResponseLayout(false);
                return;
            }
            Toast.makeText(getApplicationContext(), "Please try again", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    class ResponseAdapter extends ArrayAdapter<String> {

        ResponseAdapter(Activity context, ArrayList<String> words) {
            super(context, 0, words);
            for (int i = 0; i < answers.size(); i++) responses.add("" + i);
        }

        @SuppressLint("ResourceType")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_date, parent, false);

            TextView textView = convertView.findViewById(R.id.event);
            textView.setText(getItem(position).split(" - ")[1].trim());

            final EditText editText= convertView.findViewById(R.id.date);

            String tag = (String) editText.getTag();
            if(tag!=null && tag!="")
                responses.set(Integer.parseInt(tag), editText.getText().toString());
            editText.setText(responses.get(position));
            editText.setTag(Integer.toString(position));

            editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus){
                        if(editText==null)return;
                        int position = Integer.parseInt(editText.getTag().toString());
                        responses.set(position, editText.getText().toString());
                    }
                }
            });

            return convertView;
        }
    }
}

//TODO minimise response text
//TODO check display
