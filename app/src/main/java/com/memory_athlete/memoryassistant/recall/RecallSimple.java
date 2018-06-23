package com.memory_athlete.memoryassistant.recall;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.memory_athlete.memoryassistant.R;
import com.memory_athlete.memoryassistant.data.Helper;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import timber.log.Timber;

import static android.text.InputType.TYPE_CLASS_NUMBER;
import static android.text.InputType.TYPE_CLASS_TEXT;
import static java.lang.Math.abs;
import static java.lang.Math.pow;

public class RecallSimple extends AppCompatActivity {

    protected ArrayList<String> answers = new ArrayList<>();
    protected ArrayList<String> responses = new ArrayList<>();
    protected int selectedSuit = 0;
    protected int[] cardImageIds;

    //int mResponsePosition = 0;
    //static byte submitDoubt = 0;
    protected final int SIMPLE_COMPARE_FORMAT = 0, WORD_COMPARE_FORMAT = 1, CARD_COMPARE_FORMAT = 2;
    protected final int SIMPLE_RESPONSE_FORMAT = 0, WORD_RESPONSE_FORMAT = 1,
            CARD_RESPONSE_FORMAT = 2, CHARACTER_RESPONSE_FORMAT = 3, DATE_RESPONSE_FORMAT = 4;
    protected int responseFormat = SIMPLE_RESPONSE_FORMAT;
    protected byte compareFormat = SIMPLE_COMPARE_FORMAT;
    protected int mSuitBackground;

    protected int correct = 0, wrong = 0, missed = 0, extra = 0, spelling;
    protected StringBuilder mTextAnswer = null, mTextResponse = null;
    protected String whitespace;
    //protected CompareAsyncTask task = new CompareAsyncTask(); //use to cancel the async task, don't remember how

    protected String mDiscipline = null;
    protected String mFilePath;

    protected GridView gridView;
    protected ListView complexListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recall);
        Intent intent = getIntent();
        mFilePath = intent.getStringExtra("file");
        mDiscipline = intent.getStringExtra(getString(R.string.discipline));

        Helper.theme(this, this);
        setTitle(mDiscipline);

        gridView = findViewById(R.id.cards_responses);
        complexListView = findViewById(R.id.response_list_view);

        findViewById(R.id.result).setVisibility(View.GONE);
        findViewById(R.id.reset).setVisibility(View.GONE);

        if (intent.getBooleanExtra("file exists", false)) {
            File dir = new File(getFilesDir().getAbsolutePath() + File.separator
                    + getString(R.string.practice) + File.separator + mDiscipline);
            ArrayList<String> fileList = new ArrayList<>();
            File[] files = dir.listFiles();
            if (files == null) {
                finish();
                return;
            }
            mFilePath = files[0].getAbsolutePath();
        }

        setResponseLayout(true);
        Timber.v("activity created");
    }

    void simpleResponseLayout() {
        final EditText editText = findViewById(R.id.response_input);

/*        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Log.v(LOG_TAG, "onEditorAction started");
                getResponse();
                return true;
            }
        });*/


        switch (mDiscipline) {
            case "Numbers":
                responseFormat = SIMPLE_RESPONSE_FORMAT;
                compareFormat = SIMPLE_COMPARE_FORMAT;
                editText.setRawInputType(TYPE_CLASS_NUMBER);
                break;
            case "Binary Digits":
            case "Digits":
                responseFormat = CHARACTER_RESPONSE_FORMAT;
                compareFormat = SIMPLE_COMPARE_FORMAT;
                editText.setRawInputType(TYPE_CLASS_NUMBER);
                break;
            case "Letters":
                responseFormat = CHARACTER_RESPONSE_FORMAT;
                compareFormat = SIMPLE_COMPARE_FORMAT;
                editText.setRawInputType(TYPE_CLASS_TEXT);
                editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
                break;
            case "Words":
            case "Names":
            case "Places":
                responseFormat = WORD_RESPONSE_FORMAT;
                compareFormat = WORD_COMPARE_FORMAT;
                editText.setRawInputType(TYPE_CLASS_TEXT);
                editText.setImeOptions(EditorInfo.IME_ACTION_NONE);
                break;
        }
    }

    protected void setResponseLayout(boolean onCreate) {
        simpleResponseLayout();

        findViewById(R.id.response_input).setVisibility(View.VISIBLE);
        findViewById(R.id.card_suit).setVisibility(View.GONE);
        findViewById(R.id.card_numbers).setVisibility(View.GONE);
        gridView.setVisibility(View.GONE);

        findViewById(R.id.recall_layout).setVisibility(View.GONE);
        findViewById(R.id.progress_bar_recall).setVisibility(View.GONE);
        findViewById(R.id.reset).setVisibility(View.GONE);
        findViewById(R.id.response_layout).setVisibility(View.VISIBLE);
        findViewById(R.id.button_bar).setVisibility(View.VISIBLE);
        findViewById(R.id.text_response_scroll_view).setVisibility(View.VISIBLE);

        Timber.v("responseLayout set");
    }

    protected void getAnswers() throws FileNotFoundException {
        Timber.v("getAnswersEntered");

        try {
            String string;

            Scanner scanner = new Scanner(new File(mFilePath)).useDelimiter("\t|\n|\n\n");

            while (scanner.hasNext()) {
                string = scanner.next();
                if (mDiscipline.equals(getString(R.string.numbers)) || mDiscipline.equals(getString(R.string.cards)))
                    answers.add(string.trim());
                    //else if (mDiscipline == getString(e))
                else if (mDiscipline.equalsIgnoreCase(getString(R.string.letters))
                        || mDiscipline.equalsIgnoreCase(getString(R.string.binary))
                        || mDiscipline.equalsIgnoreCase(getString(R.string.digits))) {
                    for (char c : string.toCharArray())
                        if (c != ' ') answers.add("" + c);
                } else answers.add(string);
            }
            Timber.v(String.valueOf(answers.size()));
            scanner.close();
        } catch (Exception e) {
            //Toast.makeText(this, "Couldn't read the saved answers", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        Timber.v("getAnswers() complete");
    }

    protected String giveUp() {
        Timber.v("Give Up! pressed");
        Scanner scanner = null;
        String whitespace;
        switch (responseFormat) {
            case SIMPLE_RESPONSE_FORMAT:
            case CHARACTER_RESPONSE_FORMAT:
                whitespace = " " + getString(R.string.tab);
                break;
            default:
                whitespace = " \n";
        }

        try {
            StringBuilder sb = new StringBuilder("");
            scanner = new Scanner(new File(mFilePath)).useDelimiter("\t|\t   \t|\n|\n\n");
            Timber.v("scanner created");

            return formatAnswers(scanner, sb, whitespace);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (scanner != null)
                scanner.close();
        }

        Timber.v("giveUp() complete, returns null");
        return null;
    }

    protected String formatAnswers(Scanner scanner, StringBuilder sb, String whitespace) {
        while (scanner.hasNext()) sb.append(scanner.next()).append(whitespace);
        Timber.v("giveUp() complete, returns " + sb.toString());
        return sb.toString();
    }

    protected void getResponse() {
        EditText editText = findViewById(R.id.response_input);
        String values = editText.getText().toString(), value = "";

        if (responseFormat == CHARACTER_RESPONSE_FORMAT) {
            for (int i = 0; i < values.length(); i++) {
                if (values.charAt(i) == ' ' || values.charAt(i) == '\n'
                        || values.charAt(i) == R.string.tab) continue;
                responses.add(String.valueOf(values.charAt(i)));
                Timber.v("response = " + String.valueOf(values.charAt(i)));
            }
        } else {
            //while (responses.size() <= mResponsePosition) responses.add(" ");
            char delimiter = (responseFormat == SIMPLE_RESPONSE_FORMAT ? ' ' : '\n');
            for (int i = 0; i < values.length(); i++) {
                if (!(values.charAt(i) == delimiter)) {
                    value += values.charAt(i);
                }
                if (i + 1 == values.length()) {
                    responses.add(value);
                    continue;
                }
                if ((values.charAt(i) == delimiter && values.charAt(i - 1) != delimiter)) {
                    responses.add(value);
                    value = "";
                    continue;
                }
                if ((values.charAt(i) == delimiter && values.charAt(i + 1) == delimiter)) {
                    responses.add(" ");
                    value = "";
                }
            }
        }
        String text = ((TextView) findViewById(R.id.responses_text)).getText() + value + " " + getString(R.string.tab);
        ((TextView) findViewById(R.id.responses_text)).setText(text);
        Timber.v("onEditorAction complete");
        editText.setText("");
    }

    void hideResponseLayout() {
        findViewById(R.id.response_layout).setVisibility(View.GONE);
        findViewById(R.id.button_bar).setVisibility(View.GONE);
        findViewById(R.id.button_bar).setVisibility(View.GONE);
        //findViewById(R.id.check).setVisibility(View.GONE);
        findViewById(R.id.progress_bar_recall).setVisibility(View.GONE);
        findViewById(R.id.reset).setVisibility(View.VISIBLE);
        findViewById(R.id.recall_text_layout).setVisibility(View.VISIBLE);
        findViewById(R.id.recall_layout).setVisibility(View.VISIBLE);
        complexListView.setVisibility(View.GONE);
    }


    protected void compare(boolean words) {
        Timber.v("Comparing answers and responses in backgroundString");
        mTextResponse = new StringBuilder("");
        mTextAnswer = new StringBuilder("");
        whitespace = compareFormat == SIMPLE_COMPARE_FORMAT ? " " + getString(R.string.tab): "<br/>";
        Timber.v("whitespace = " + whitespace + ".");
        int i = 0, j = 0;
        for (; i < responses.size() && j < answers.size(); i++, j++) {
            Timber.v("Entered loop " + (i + j) + " - response " + responses.get(i) + ", answer " + answers.get(j));
            if (isCorrect(i, j)) continue;
            if (missed > 8 && missed > correct) break;
            if (isLeft(i, j)) continue;
            if (words && isSpelling(i, j)) {
                spelling++;
                mTextAnswer.append("<font color=#EEEE00>").append(answers.get(j))
                        .append("</font>").append(" ").append(whitespace);
                mTextResponse.append("<font color=#EEEE00>").append(responses.get(i))
                        .append("</font>").append(" ").append(whitespace);
                correct++;
                continue;
            }

            if (isMiss(i, j)) {
                if (isExtra(i, j)) {
                    j--;
                    continue;
                }
                Timber.v("missed");
                missed++;
                mTextAnswer.append(answers.get(j)).append(" ").append(whitespace);
                mTextResponse.append("<font color=#FF9500>").append(answers.get(j))
                        .append("</font>").append(" ").append(whitespace);
                i--;
                continue;
            }
            isWrong(i, j);
        }
        for (; i < responses.size(); i++)
            mTextResponse.append("<font color=#FF0000>")
                    .append(responses.get(i)).append("</font>").append(" ").append(whitespace);
        for (int k = 0; k < 20 && j < answers.size(); j++, k++) {
            mTextAnswer.append("<font color=#FF9500>")
                    .append(answers.get(j)).append("</font>").append(" ").append(whitespace);
        }
        Timber.v("compare() complete ");
    }

    protected boolean isLeft(int i, int j) {
        if (responses.get(i).equals(" ")) {
            missed++;
            mTextAnswer.append(answers.get(j)).append(" ").append(whitespace);
            mTextResponse.append("<font color=#FF9500>").append(answers.get(j)).append("</font>")
                    .append(" ").append(whitespace);
            return true;
        } else return false;
    }

    protected boolean isCorrect(int i, int j) {
        if (responses.get(i).equalsIgnoreCase(answers.get(j))) {
            correct++;
            mTextAnswer.append(answers.get(j)).append(" ").append(whitespace);
            mTextResponse.append(responses.get(i)).append(" ").append(whitespace);
            return true;
        } else return false;
    }

    protected boolean isMiss(int i, int j) {
        int match = 0, k, checkRange = 10 + missed;

        if (i < 3) k = 1;
        else if (responses.size() - i < 5) k = -1;
        else if (i < 10) k = -(i / 3);
        else k = -4;

        for (; k <= checkRange && i + k < responses.size() && j + k < answers.size(); k++) {
            Timber.v("j = " + j + " k = " + k);
            if (j + k < 0) continue;
            if (responses.get(i + k).equals(" ")) continue;
            if (responses.get(i + k).equalsIgnoreCase(answers.get(j + k))) {
                match++;
            }
        }
        Timber.v(match + "");
        return ((float) match / k) <= 0.5;
    }

    protected boolean isExtra(int i, int j) {
        int match = 0, k, checkRange = 10, l = 0;

        for (; l < 5; l++) {
            //if (i < 3) k = 1;
            //else if (responses.size() - i < 4 || answers.size() - j < 4) k = -1;
            //else if (i < 10) k = -(i / 3);
            //else k = -4;

            for (k = 0; k <= checkRange && i + k < responses.size() && j + k < answers.size(); k++) {
                if (i == -k || j == -k) continue;
                if (!responses.get(i + k).equals(" ")) {
                    if (responses.get(i + k).equalsIgnoreCase(answers.get(j + k - 1))) {
                        match++;
                    }
                }
            }
            Timber.v(match + "");
            if (((float) match / k) > 0.3) {            //Extra
                mTextResponse.append("<font color=#1D6C21>").append(responses.get(i))
                        .append("</font>").append(" ").append(whitespace);
                mTextAnswer.append("<font color=#1D6C21>").append(responses.get(i))
                        .append("</font>").append(" ").append(whitespace);
                extra++;
                return true;
            }
        }
        return false;
    }

    protected void isWrong(int i, int j) {
        wrong++;
        mTextAnswer.append("<font color=#FF0000>").append(answers.get(j)).append("</font>")
                .append(" ").append(whitespace);
        mTextResponse.append("<font color=#FF0000>").append(responses.get(i))
                .append("</font>").append(" ").append(whitespace);
    }

    protected boolean isSpelling(int i, int j) {
        if ((float) abs((responses.get(i).length() - answers.get(j).length())
                / answers.get(j).length()) > 0.3) return false;
        int count = 0;
        for (int a = 0; a < responses.get(i).length() && a < answers.get(j).length(); a++) {
            if (Character.toLowerCase(responses.get(i).charAt(a)) == Character.toLowerCase(answers.get(j).charAt(a))) {
                count++;
                Timber.v("Match for " + answers.get(j).charAt(a) + "  count = " + count);
                continue;
            }
            Timber.d("Not a match");
            for (int b = -2; b < 3; b++) {
                Timber.v("" + b);
                if (a + b > 0 && a + b < responses.get(i).length() && a + b < answers.get(j).length()) {
                    if (responses.get(i).charAt(a) == answers.get(j).charAt(a + b)) {
                        count++;
                        Timber.v("match for " + answers.get(j).charAt(a + b) + "  count = " + count);
                        break;
                    }
                }
            }
        }
        Timber.v("Count = " + count);
        return ((float) count / answers.get(j).length()) > 0.6;

    }


    protected void reset() {
        answers.clear();
        responses.clear();
        mTextAnswer = new StringBuilder("");
        mTextResponse = new StringBuilder("");
        correct = 0;
        wrong = 0;
        missed = 0;

        setResponseLayout(true);

        ((Chronometer) findViewById(R.id.time_elapsed_value)).stop();
        findViewById(R.id.result).setVisibility(View.GONE);
        findViewById(R.id.recall_layout).setVisibility(View.GONE);
        findViewById(R.id.response_layout).setVisibility(View.VISIBLE);
        findViewById(R.id.button_bar).setVisibility(View.VISIBLE);
        findViewById(R.id.reset).setVisibility(View.GONE);

        //findViewById(R.id.response_layout).setVisibility(View.VISIBLE);

        //((TextView) findViewById(R.id.responses_text)).setText("");
        //((TextView) findViewById(R.id.answers_text)).setText("");

        Timber.v("Recall Reset Complete");
    }

    public void reset(View view) {
        reset();
    }

    public void check(View view) {
        View v = this.getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
        (new CompareAsyncTask()).execute();
    }

    public void startSW(View view) {
        ((Chronometer) findViewById(R.id.time_elapsed_value)).start();
        findViewById(R.id.time_elapsed_value).setVisibility(View.VISIBLE);
        findViewById(R.id.result).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.time_elapsed_header)).setText(R.string.time_elapsed);
    }

    public void giveUp(View view) {
        (new JustAnswersAsyncTask()).execute();
    }

    public void startTimer(View view) {
    } //TODO: USe this!

    protected class CompareAsyncTask extends AsyncTask<ArrayList<Integer>, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            gridView.setVisibility(View.GONE);
            getResponse();
            findViewById(R.id.progress_bar_recall).setVisibility(View.VISIBLE);
            //Log.v(LOG_TAG, "answers.size() = " + String.valueOf(answers.size()));
            Timber.v("responses.size() = " + String.valueOf(responses.size()));
        }

        @SafeVarargs
        @Override
        protected final Boolean doInBackground(ArrayList<Integer>... a) {
            try {
                getAnswers();
                if (compareFormat == SIMPLE_COMPARE_FORMAT || compareFormat == CARD_COMPARE_FORMAT)
                    compare(false);
                else if (compareFormat == WORD_COMPARE_FORMAT) compare(true);
                return false;
            } catch (FileNotFoundException e){
                e.printStackTrace();
                return true;
            }
        }

        @Override
        protected void onPostExecute(Boolean error) {
            super.onPostExecute(error);
            if(error){
                Toast.makeText(getApplicationContext(), "Please try again", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            hideResponseLayout();

            ((TextView) findViewById(R.id.responses_text)).setText(Html.fromHtml(mTextResponse.toString()),
                    TextView.BufferType.SPANNABLE);
            findViewById(R.id.answers_text).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.answers_text)).setText(Html.fromHtml(mTextAnswer.toString()),
                    TextView.BufferType.SPANNABLE);

            Timber.v("mTextResponse length = " + String.valueOf(mTextAnswer.toString().length()));
            Timber.v("mTextAnswer length = " + String.valueOf(mTextResponse.toString().length()));
            Timber.v("answer 0 = " + answers.get(0));

            if (correct == answers.size() && !mDiscipline.equals(getString(R.string.binary))) {
                SharedPreferences sharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(getApplicationContext());
                if (pow(2, sharedPreferences.getInt("level", 1) + 2) == correct)
                    sharedPreferences.edit().putInt("level", 1 + sharedPreferences.getInt("level", 1)).apply();
            }

            findViewById(R.id.responses_text).setVisibility(View.VISIBLE);
            findViewById(R.id.result).setVisibility(View.VISIBLE);
            findViewById(R.id.recall_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.responses_text_layout).setVisibility(View.VISIBLE);

            ((TextView) findViewById(R.id.no_of_correct)).setText(String.valueOf(correct));
            ((TextView) findViewById(R.id.no_of_wrong)).setText(String.valueOf(wrong));
            ((TextView) findViewById(R.id.no_of_missed)).setText(String.valueOf(missed));
            ((TextView) findViewById(R.id.no_of_extra)).setText(String.valueOf(extra));
            ((TextView) findViewById(R.id.value_of_score)).setText(String.valueOf(
                    correct - 10 * wrong - 5 * missed - 2 * extra - spelling));

            if (missed > 8 && missed > correct) Toast.makeText(getApplicationContext(),
                    "Very less accuracy", Toast.LENGTH_SHORT).show();
            //((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
            //      .toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }
    }

    private class JustAnswersAsyncTask extends AsyncTask<Void, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            findViewById(R.id.progress_bar_recall).setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {
            return giveUp();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s == null) {
                Toast.makeText(getApplicationContext(), "Sorry...", Toast.LENGTH_SHORT).show();
                reset();
                return;
            }
            hideResponseLayout();
            findViewById(R.id.responses_text_layout).setVisibility(View.GONE);
            gridView.setVisibility(View.GONE);
            TextView textAnswers = findViewById(R.id.answers_text);
            textAnswers.setText(s);
            textAnswers.setVisibility(View.VISIBLE);
        }
    }
}

//orange:FFA500
// textView.setText(Html.fromHtml(styledText), TextView.BufferType.SPANNABLE);
// TODO: shift to fragments
// TODO remove reset
// TODO fix button bar