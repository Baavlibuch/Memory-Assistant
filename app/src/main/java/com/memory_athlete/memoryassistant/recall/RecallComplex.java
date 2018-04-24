package com.memory_athlete.memoryassistant.recall;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.memory_athlete.memoryassistant.R;
import com.memory_athlete.memoryassistant.main.RecallSimple;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

import timber.log.Timber;

public class RecallComplex extends RecallSimple {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        (new LoadAnswersAsyncTask()).execute();
        responseFormat = 4;
        complexListView = findViewById(R.id.response_list_view);
    }

    @Override
    protected void setResponseLayout() {
        if (mDiscipline.equals(getString(R.string.dates))) {
            findViewById(R.id.response_input).setVisibility(View.GONE);
            findViewById(R.id.card_suit).setVisibility(View.GONE);
            findViewById(R.id.card_numbers).setVisibility(View.GONE);
            gridView.setVisibility(View.GONE);
            findViewById(R.id.response_list_view).setVisibility(View.VISIBLE);
        }

        findViewById(R.id.recall_layout).setVisibility(View.GONE);
        findViewById(R.id.progress_bar_recall).setVisibility(View.GONE);
        findViewById(R.id.response_layout).setVisibility(View.VISIBLE);
        findViewById(R.id.button_bar).setVisibility(View.VISIBLE);
        findViewById(R.id.reset).setVisibility(View.GONE);

        ((ListView) findViewById(R.id.response_list_view)).setAdapter(
                new ResponseAdapter(this, answers));
    }

    protected void getAnswers() {
        Timber.v("getAnswersEntered");
        try {
            String string;

            Scanner scanner = new Scanner(new File(getFilesDir().getAbsolutePath()
                    + File.separator + getString(R.string.practice) + File.separator + mDiscipline + File.separator
                    + mFileName)).useDelimiter("\n|\n\n");

            while (scanner.hasNext()) {
                string = scanner.next();
                answers.add(string);
            }
            Collections.shuffle(answers);
            Timber.v(String.valueOf(answers.size()));
            scanner.close();
        } catch (Exception e) {
            //Toast.makeText(this, "Couldn't read the saved answers", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        Timber.v("getAnswers() complete");
    }

    @Override
    protected String formatAnswers(Scanner scanner, StringBuilder sb, String whitespace) {
        while (scanner.hasNext()) {
            String s = scanner.next();
            s = s.replace("\t", "\n");
            sb.append(s).append(whitespace);
        }
        return sb.toString();
    }

    @Override
    protected void getResponse() {
        for (int i = 0; i < complexListView.getCount(); i++)
            responses.add(((TextView) complexListView.getChildAt(i)).getText().toString());

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
            isWrong(i, j);
        }
    }

    protected boolean isLeft(int i, int j) {
        if (responses.get(i).equals("")) {
            missed++;
            mTextAnswer.append(answers.get(j)).append(" ").append(whitespace);
            mTextResponse.append("<font color=#FF9500>").append(answers.get(j)).append("</font>")
                    .append(" ").append(whitespace);
            return true;
        } else return false;
    }


    class LoadAnswersAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            findViewById(R.id.progress_bar_recall).setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            getAnswers();
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            setResponseLayout();
        }
    }

    class ResponseAdapter extends ArrayAdapter<String> {
        ResponseAdapter(Activity context, ArrayList<String> words) {
            super(context, 0, words);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View listItemView = convertView;
            if (listItemView == null) {
                listItemView = LayoutInflater.from(getContext()).inflate(R.layout.item_date,
                        parent, false);
            }

            TextView textView = listItemView.findViewById(R.id.event);
            textView.setText(getItem(position).split("\t")[1].trim());

            return listItemView;
        }
    }
}

//TODO minimise response text
//TODO check display