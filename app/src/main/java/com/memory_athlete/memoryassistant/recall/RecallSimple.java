package com.memory_athlete.memoryassistant.recall;

import static android.text.InputType.TYPE_CLASS_NUMBER;
import static android.text.InputType.TYPE_CLASS_TEXT;
import static java.lang.Math.abs;
import static java.lang.Math.pow;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.memory_athlete.memoryassistant.Helper;
import com.memory_athlete.memoryassistant.language.LocaleHelper;
import com.memory_athlete.memoryassistant.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

import timber.log.Timber;

public class RecallSimple extends AppCompatActivity {
    protected boolean givenUp = false;
    protected boolean resetSWFlag = true;

    protected ArrayList<String> responses = new ArrayList<>();
    protected ArrayList<String> answers = new ArrayList<>();
    protected int selectedSuit = 0;
    protected int[] cardImageIds;

    protected int mSuitBackground;
    protected CompareFormat compareFormat = CompareFormat.SIMPLE_COMPARE_FORMAT;
    protected ResponseFormat responseFormat = ResponseFormat.SIMPLE_RESPONSE_FORMAT;

    protected int correctCount = 0;                     // 1 point
    protected int wrongCount = 0;                       // 10 points penalized
    protected int missedCount = 0;                      // 5 points penalized
    protected int extraCount = 0;                       // 2 points penalized
    protected int spellingCount = 0;                    // 1 point penalized. not displayed

    protected StringBuilder mTextAnswer = null;
    protected StringBuilder mTextResponse = null;

    protected String whitespace;
    //protected CompareAsyncTask task;                  //use to cancel the async task if it is instantiated

    protected String mFilePath;
    protected String mDiscipline = null;

    protected ListView complexListView;
    protected GridView gridView;

    protected SharedPreferences sharedPreferences;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    public void onBackPressed() {
        if (findViewById(R.id.result).getVisibility() == View.VISIBLE || givenUp)
            reset();                  // answers are visible, go back to responses
        else super.onBackPressed();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = Objects.requireNonNull(PreferenceManager.getDefaultSharedPreferences(this));
        Helper.theme(this, this);
        setContentView(R.layout.activity_recall);
        Intent intent = getIntent();
        mFilePath = intent.getStringExtra("file");
        mDiscipline = intent.getStringExtra("discipline");
        Toast.makeText(RecallSimple.this,mDiscipline, Toast.LENGTH_SHORT).show();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Timber.i("file Path = %s", mFilePath);
        Timber.i("discipline = %s", mDiscipline);
        Timber.i("file exists = %s", intent.getBooleanExtra("file exists", false));

        setTitle(mDiscipline);

        gridView = findViewById(R.id.cards_responses);
        complexListView = findViewById(R.id.response_list_view);

        findViewById(R.id.result).setVisibility(View.GONE);

        if (intent.getBooleanExtra("file exists", false)) {

            int EXTERNAL_STORAGE_PERMISSION_CODE = 23;
                ActivityCompat.requestPermissions(RecallSimple.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        EXTERNAL_STORAGE_PERMISSION_CODE);

                File folder = getFilesDir();
                File dir = new File(folder + File.separator
                        + getString(R.string.practice) + File.separator + mDiscipline);

//            File dir = new File(Helper.APP_FOLDER + File.separator
//                    + getString(R.string.practice) + File.separator + mDiscipline);
            File[] files = dir.listFiles();
            if (files == null) {
                finish();
                return;
            }
            mFilePath = files[files.length - 1].getAbsolutePath();
            Timber.v("filePath = %s", mFilePath);


        }

        setResponseLayout(true);
        Timber.v("activity created");
    }

    void simpleResponseLayout() {
        final EditText editText = findViewById(R.id.response_input);

        if (mDiscipline.equals(getString(R.string.numbers))) {
            responseFormat = ResponseFormat.SIMPLE_RESPONSE_FORMAT;         // space separated
            compareFormat = CompareFormat.SIMPLE_COMPARE_FORMAT;
            editText.setRawInputType(TYPE_CLASS_NUMBER);
        } else if (mDiscipline.equals(getString(R.string.binary)) || mDiscipline.equals(getString(R.string.digits))) {
            responseFormat = ResponseFormat.CHARACTER_RESPONSE_FORMAT;      // no delimiter
            compareFormat = CompareFormat.SIMPLE_COMPARE_FORMAT;
            editText.setRawInputType(TYPE_CLASS_NUMBER);
        } else if (mDiscipline.equals(getString(R.string.letters))) {
            responseFormat = ResponseFormat.CHARACTER_RESPONSE_FORMAT;      // no delimiter
            compareFormat = CompareFormat.SIMPLE_COMPARE_FORMAT;
            editText.setRawInputType(TYPE_CLASS_TEXT);
            editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        } else if (mDiscipline.equals(getString(R.string.words)) || mDiscipline.equals(getString(R.string.names)) || mDiscipline.equals(getString(R.string.places_capital))) {
            responseFormat = ResponseFormat.WORD_RESPONSE_FORMAT;           // newline separated
            compareFormat = CompareFormat.WORD_COMPARE_FORMAT;
            editText.setRawInputType(TYPE_CLASS_TEXT);
            editText.setImeOptions(EditorInfo.IME_ACTION_NONE);
        } else {
            throw new RuntimeException("Bad discipline : " + mDiscipline);
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
        findViewById(R.id.response_layout).setVisibility(View.VISIBLE);
        findViewById(R.id.button_bar).setVisibility(View.VISIBLE);
        findViewById(R.id.text_response_scroll_view).setVisibility(View.VISIBLE);

        Timber.v("responseLayout set");
    }

    protected void getAnswers() throws FileNotFoundException {
        Timber.v("getAnswersEntered");
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

        Timber.v("getAnswers() complete");
    }

    protected String giveUp() {
        Timber.v("Give Up! pressed");
        givenUp = true;
        String whitespace;
        switch (responseFormat) {
            case SIMPLE_RESPONSE_FORMAT:
            case CHARACTER_RESPONSE_FORMAT:
                whitespace = " " + getString(R.string.tab);
                break;
            default:
                whitespace = " \n";
        }

        StringBuilder sb = new StringBuilder();
        try (Scanner scanner = new Scanner(new File(mFilePath)).useDelimiter("\t|\t {3}\t|\n|\n\n")) {
            return formatAnswers(scanner, sb, whitespace);
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    protected String formatAnswers(Scanner scanner, StringBuilder sb, String whitespace) {
        while (scanner.hasNext()) sb.append(scanner.next()).append(whitespace);
        Timber.v("giveUp() complete, returns %s", sb.toString());
        return sb.toString();
    }

    protected void getResponse() {
        EditText editText = findViewById(R.id.response_input);
        String values = editText.getText().toString();
        String value = "";
        responses.clear();

        if (responseFormat == ResponseFormat.CHARACTER_RESPONSE_FORMAT) {
            // just a simple list of characters. Convert it into an ArrayList
            for (int i = 0; i < values.length(); i++) {
                // ignore whitespace
                if (values.charAt(i) == ' ' || values.charAt(i) == '\n'
                        || values.charAt(i) == getString(R.string.tab).charAt(0)) continue;
                responses.add(String.valueOf(values.charAt(i)));
                Timber.v("response = %s", String.valueOf(values.charAt(i)));
            }
        } else {
            // words or sentences
            // simple response format - ' ', '\n' delimited
            // word response format  - '\n' delimited
            char delimiter = (responseFormat == ResponseFormat.SIMPLE_RESPONSE_FORMAT ? ' ' : '\n');

            for (int i = 0; i < values.length(); i++) {

                boolean encounteredDelimiter = values.charAt(i) == delimiter || values.charAt(i) == '\n';
                // append character to the value if it is not equal to the delimiter
                if (!encounteredDelimiter) value += values.charAt(i);

                // search for value's end (delimiter) and move to next
                // Only if didn't encounter 2 delimiters back to back
                // Not possible when index is 0 because i-1 == -1
                if (i != 0 && (encounteredDelimiter && (values.charAt(i - 1) != delimiter || values.charAt(i - 1) == '\n'))) {
                    responses.add(value);
                    value = "";                             // reset for new value
                    continue;
                }

                // check if the end has been reached. If yes, add to responses list and end loop
                if (i + 1 == values.length()) responses.add(value);

                // else multiple delimiters encountered. ignore and move on
            }
        }
    }

    void hideResponseLayout() {
        findViewById(R.id.response_layout).setVisibility(View.GONE);
        findViewById(R.id.button_bar).setVisibility(View.GONE);
        findViewById(R.id.button_bar).setVisibility(View.GONE);
        //findViewById(R.id.check).setVisibility(View.GONE);
        findViewById(R.id.progress_bar_recall).setVisibility(View.GONE);
        findViewById(R.id.recall_text_layout).setVisibility(View.VISIBLE);
        findViewById(R.id.recall_layout).setVisibility(View.VISIBLE);
        complexListView.setVisibility(View.GONE);
    }


    protected void compare(boolean words) {
        Timber.v("Comparing answers and responses in backgroundString");
        if (mDiscipline.equals(getString(R.string.letters)) && Integer.parseInt(Objects.requireNonNull(
                sharedPreferences.getString(getString(R.string.letter_case), "1"))) == Helper.MIXED_CASE) {
            compareMixed();
            return;
        }
        mTextResponse = new StringBuilder();
        mTextAnswer = new StringBuilder();
        whitespace = compareFormat == CompareFormat.SIMPLE_COMPARE_FORMAT ? " " + getString(R.string.tab) : "<br/>";
        Timber.v("whitespace = " + whitespace + ".");
        int i = 0, j = 0;
        for (; i < responses.size() && j < answers.size(); i++, j++) {
            Timber.v("Entered loop " + (i + j) + " - response " + responses.get(i) + ", answer " + answers.get(j));
            if (isCorrect(i, j)) continue;
            if (missedCount > 8 && missedCount > correctCount) break;
            if (words && isSpelling(i, j)) {
                spellingCount++;
                mTextAnswer.append("<font color=#EEE000>").append(answers.get(j))
                        .append("</font>").append(" ").append(whitespace);
                mTextResponse.append("<font color=#EEE000>").append(responses.get(i))
                        .append("</font>").append(" ").append(whitespace);
                correctCount++;
                continue;
            }

            if (isMiss(i, j)) {
                if (isExtra(i, j)) {
                    j--;
                    continue;
                }
                Timber.v("missed");
                missedCount++;
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

    protected boolean isCorrect(int i, int j) {
        if (responses.get(i).equalsIgnoreCase(answers.get(j))) {
            correctCount++;
            mTextAnswer.append(answers.get(j)).append(" ").append(whitespace);
            mTextResponse.append(responses.get(i)).append(" ").append(whitespace);
            return true;
        } else return false;
    }

    // check if the value is missing or not
    // somewhat similar to n-grams
    protected boolean isMiss(int i, int j) {
        int match = 0;                                      // count the number of matches
        int k;                                              // position to be compared in the n-gram
        int checkRange = 10;                                // similar to n in n-grams

        // initialise k
        // k != 0 because that would be an obvious mismatch as it is checked in the driver function
        if (i < 3)
            k = 1;                                   // ? it works for some reason. Can't remember why
        else if (responses.size() - i < 5)
            k = -1;          // keep the lookback small near the end because lookahead is small
        else if (i < 10)
            k = -(i / 3);                      // keep the window small and don't look beyond 0
        else k = -4;                                        // preferred look back

        for (; k <= checkRange && i + k < responses.size() && j + k < answers.size(); k++) {
            Timber.v("j = " + j + " k = " + k);
            if (j + k < 0) continue;                        // just for saftey. Might never be true
            if (responses.get(i + k).equalsIgnoreCase(answers.get(j + k))) match++;
        }
        Timber.v("%s", match);
        return ((float) match / k) <= 0.5;                  // too many mismatches -> missed
    }

    protected boolean isExtra(int i, int j) {
        int match = 0;
        int k;
        int checkRange = 10;
        int l = 0;

        for (; l < 5; l++) {
            //if (i < 3) k = 1;
            //else if (responses.size() - i < 4 || answers.size() - j < 4) k = -1;
            //else if (i < 10) k = -(i / 3);
            //else k = -4;

            for (k = 0; k <= checkRange && i + k < responses.size() && j + k < answers.size(); k++) {
                if (i == -k || j == -k) continue;
                if (responses.get(i + k).equalsIgnoreCase(answers.get(j + k - 1))) match++;
            }
            Timber.v("%s", match);
            if (((float) match / k) > 0.3) {            //Extra
                mTextResponse.append("<font color=#1D6C21>").append(responses.get(i))
                        .append("</font>").append(" ").append(whitespace);
                mTextAnswer.append("<font color=#1D6C21>").append(responses.get(i))
                        .append("</font>").append(" ").append(whitespace);
                extraCount++;
                return true;
            }
        }
        return false;
    }

    protected void isWrong(int i, int j) {
        wrongCount++;
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
                Timber.v("%s", b);
                if (a + b > 0 && a + b < responses.get(i).length() && a + b < answers.get(j).length()) {
                    if (responses.get(i).charAt(a) == answers.get(j).charAt(a + b)) {
                        count++;
                        Timber.v("match for " + answers.get(j).charAt(a + b) + "  count = " + count);
                        break;
                    }
                }
            }
        }
        Timber.v("Count = %s", count);
        return ((float) count / answers.get(j).length()) > 0.6;

    }


    // Match the following. Example - dates, names and faces, abstract images
    protected void compareMixed() {
        Timber.v("Comparing answers and responses in compareMixed");
        mTextResponse = new StringBuilder();
        mTextAnswer = new StringBuilder();
        whitespace = compareFormat == CompareFormat.SIMPLE_COMPARE_FORMAT ? " " + getString(R.string.tab) : "<br/>";
        Timber.v("whitespace = " + whitespace + ".");
        int i = 0, j = 0;
        for (; i < responses.size() && j < answers.size(); i++, j++) {
            Timber.v("Entered loop " + (i + j) + " - response " + responses.get(i) + ", answer " + answers.get(j));
            if (isCorrectMixed(i, j)) continue;
            if (missedCount > 8 && missedCount > correctCount) break;
            if (isLeftMixed(i, j)) continue;

            if (isMissMixed(i, j)) {
                if (isExtraMixed(i, j)) {
                    j--;
                    continue;
                }
                Timber.v("missed");
                missedCount++;
                mTextAnswer.append(answers.get(j)).append(" ").append(whitespace);
                mTextResponse.append("<font color=#FF9500>").append(answers.get(j))
                        .append("</font>").append(" ").append(whitespace);
                i--;
                continue;
            }
            isWrongMixed(i, j);
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

    protected boolean isLeftMixed(int i, int j) {
        if (responses.get(i).equals(" ")) {
            missedCount++;
            mTextAnswer.append(answers.get(j)).append(" ").append(whitespace);
            mTextResponse.append("<font color=#FF9500>").append(answers.get(j)).append("</font>")
                    .append(" ").append(whitespace);
            return true;
        } else return false;
    }

    protected boolean isCorrectMixed(int i, int j) {
        if (responses.get(i).equals(answers.get(j))) {
            correctCount++;
            mTextAnswer.append(answers.get(j)).append(" ").append(whitespace);
            mTextResponse.append(responses.get(i)).append(" ").append(whitespace);
            return true;
        } else return false;
    }

    protected boolean isMissMixed(int i, int j) {
        int match = 0, k, checkRange = 10 + missedCount;

        if (i < 3) k = 1;
        else if (responses.size() - i < 5) k = -1;
        else if (i < 10) k = -(i / 3);
        else k = -4;

        for (; k <= checkRange && i + k < responses.size() && j + k < answers.size(); k++) {
            Timber.v("j = " + j + " k = " + k);
            if (j + k < 0) continue;
            if (responses.get(i + k).equals(" ")) continue;
            if (responses.get(i + k).equals(answers.get(j + k))) {
                match++;
            }
        }
        Timber.v("%s", match);
        return ((float) match / k) <= 0.5;
    }

    protected boolean isExtraMixed(int i, int j) {
        int match = 0, k, checkRange = 10, l = 0;

        for (; l < 5; l++) {
            //if (i < 3) k = 1;
            //else if (responses.size() - i < 4 || answers.size() - j < 4) k = -1;
            //else if (i < 10) k = -(i / 3);
            //else k = -4;

            for (k = 0; k <= checkRange && i + k < responses.size() && j + k < answers.size(); k++) {
                if (i == -k || j == -k) continue;
                if (!responses.get(i + k).equals(" ")) {
                    if (responses.get(i + k).equals(answers.get(j + k - 1))) {
                        match++;
                    }
                }
            }
            Timber.v("%s", match);
            if (((float) match / k) > 0.3) {            //Extra
                mTextResponse.append("<font color=#1D6C21>").append(responses.get(i))
                        .append("</font>").append(" ").append(whitespace);
                mTextAnswer.append("<font color=#1D6C21>").append(responses.get(i))
                        .append("</font>").append(" ").append(whitespace);
                extraCount++;
                return true;
            }
        }
        return false;
    }

    protected void isWrongMixed(int i, int j) {
        wrongCount++;
        mTextAnswer.append("<font color=#FF0000>").append(answers.get(j)).append("</font>")
                .append(" ").append(whitespace);
        mTextResponse.append("<font color=#FF0000>").append(responses.get(i))
                .append("</font>").append(" ").append(whitespace);
    }


    protected void reset() {
        wrongCount = 0;
        missedCount = 0;
        correctCount = 0;
        extraCount = 0;
        answers.clear();
        responses.clear();
        mTextAnswer = new StringBuilder();
        mTextResponse = new StringBuilder();

        setResponseLayout(false);
        givenUp = false;
        resetSWFlag = true;

        ((Chronometer) findViewById(R.id.time_elapsed_value)).stop();
        findViewById(R.id.result).setVisibility(View.GONE);
        findViewById(R.id.recall_layout).setVisibility(View.GONE);
        findViewById(R.id.response_layout).setVisibility(View.VISIBLE);
        findViewById(R.id.button_bar).setVisibility(View.VISIBLE);

        Timber.v("Recall Reset Complete");
    }

    public void check(View view) {
        View v = this.getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
        ((Chronometer) findViewById(R.id.time_elapsed_value)).stop();
        //noinspection unchecked            // couldn't find a fix. Help out if you can
        (new CompareAsyncTask()).execute();
    }

    public void startSW(View view) {
        Chronometer chronometer = findViewById(R.id.time_elapsed_value);
        if (resetSWFlag)
            chronometer.setBase(SystemClock.elapsedRealtime());           // start from zero if reset
        chronometer.start();
        findViewById(R.id.time_elapsed_value).setVisibility(View.VISIBLE);
        findViewById(R.id.result).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.time_elapsed_header)).setText(R.string.time_elapsed);
        resetSWFlag = false;
    }

    public void giveUp(View view) {
        ((Chronometer) findViewById(R.id.time_elapsed_value)).stop();
        (new JustAnswersAsyncTask()).execute();
    }

    // TODO: Start a timer

    protected void postExecuteCompare() {
        ((TextView) findViewById(R.id.responses_text)).setText(Html.fromHtml(mTextResponse.toString()),
                TextView.BufferType.SPANNABLE);
        findViewById(R.id.answers_text).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.answers_text)).setText(Html.fromHtml(mTextAnswer.toString()),
                TextView.BufferType.SPANNABLE);
    }

    @SuppressLint("StaticFieldLeak")
    protected class CompareAsyncTask extends AsyncTask<ArrayList<Integer>, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            gridView.setVisibility(View.GONE);
            findViewById(R.id.progress_bar_recall).setVisibility(View.VISIBLE);
            hideResponseLayout();
            Timber.v("responses.size() = %s", String.valueOf(responses.size()));
        }

        @SafeVarargs
        @Override
        protected final Boolean doInBackground(ArrayList<Integer>... a) {
            try {
                getResponse();
                getAnswers();
                if (compareFormat == CompareFormat.SIMPLE_COMPARE_FORMAT
                        || compareFormat == CompareFormat.CARD_COMPARE_FORMAT)
                    compare(false);
                else if (compareFormat == CompareFormat.WORD_COMPARE_FORMAT) compare(true);
                return false;
            } catch (FileNotFoundException e) {
                Timber.e(e);
                return true;
            }
        }

        @Override
        protected void onPostExecute(Boolean fileNotFound) {
            super.onPostExecute(fileNotFound);
            if (fileNotFound) {
                Toast.makeText(getApplicationContext(), "File not found", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            Timber.v("mTextResponse length = %s", String.valueOf(mTextAnswer.toString().length()));
            Timber.v("mTextAnswer length = %s", String.valueOf(mTextResponse.toString().length()));
            Timber.v("answer 0 = %s", answers.get(0));

            // If remembered all, level up
            // Do not level up for binary digits, it is too easy and different
            // Level up only if at max level
            // minimum random count is 2^3 so add 2 to level 1. same for all levels
            int level = sharedPreferences.getInt("level", 1);
            if (correctCount == answers.size() && !mDiscipline.equals(getString(R.string.binary)) &&
                    pow(2, level + 2) == correctCount) {
                sharedPreferences.edit().putInt("level", ++level).apply();

                // report level up
                Bundle bundle = new Bundle();
                bundle.putLong(FirebaseAnalytics.Param.LEVEL, level);
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LEVEL_UP, bundle);
            }

            findViewById(R.id.responses_text).setVisibility(View.VISIBLE);
            findViewById(R.id.result).setVisibility(View.VISIBLE);
            findViewById(R.id.recall_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.responses_text_layout).setVisibility(View.VISIBLE);

            ((TextView) findViewById(R.id.no_of_correct)).setText(String.valueOf(correctCount));
            ((TextView) findViewById(R.id.no_of_wrong)).setText(String.valueOf(wrongCount));
            ((TextView) findViewById(R.id.no_of_missed)).setText(String.valueOf(missedCount));
            ((TextView) findViewById(R.id.no_of_extra)).setText(String.valueOf(extraCount));
            ((TextView) findViewById(R.id.value_of_score)).setText(String.valueOf(
                    correctCount - 10 * wrongCount - 5 * missedCount - 2 * extraCount - spellingCount));

            if (missedCount > 8 && missedCount > correctCount)
                Toast.makeText(getApplicationContext(), "Very less accuracy", Toast.LENGTH_SHORT).show();
            postExecuteCompare();
        }
    }

    @SuppressLint("StaticFieldLeak")
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
                Toast.makeText(getApplicationContext(), "File not found", Toast.LENGTH_SHORT).show();
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

    protected enum ResponseFormat {
        SIMPLE_RESPONSE_FORMAT, WORD_RESPONSE_FORMAT, CARD_RESPONSE_FORMAT,
        CHARACTER_RESPONSE_FORMAT, DATE_RESPONSE_FORMAT
    }

    protected enum CompareFormat {
        SIMPLE_COMPARE_FORMAT, WORD_COMPARE_FORMAT, CARD_COMPARE_FORMAT
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base, "en"));
    }
}

// TODO: shift to fragments