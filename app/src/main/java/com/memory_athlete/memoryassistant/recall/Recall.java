package com.memory_athlete.memoryassistant.recall;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.memory_athlete.memoryassistant.BuildConfig;
import com.memory_athlete.memoryassistant.R;
import com.memory_athlete.memoryassistant.data.MakeList;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import timber.log.Timber;

import static android.text.InputType.TYPE_CLASS_NUMBER;
import static android.text.InputType.TYPE_CLASS_TEXT;
import static com.memory_athlete.memoryassistant.R.layout.activity_recall;
import static com.memory_athlete.memoryassistant.R.string.binary;
import static com.memory_athlete.memoryassistant.R.string.c;
import static com.memory_athlete.memoryassistant.R.string.cards;
import static com.memory_athlete.memoryassistant.R.string.k;
import static com.memory_athlete.memoryassistant.R.string.numbers;
import static com.memory_athlete.memoryassistant.data.MakeList.makeCardString;
import static java.lang.Integer.parseInt;
import static java.lang.Math.abs;


public class Recall extends AppCompatActivity {

    private static final String LOG_TAG = "\tRecall: ";
    private ArrayList<String> answers = new ArrayList<>();
    private ArrayList<String> responses = new ArrayList<>();
    ArrayList<String> categories;
    private Spinner mSpinner=null;
    private String mDiscipline=null;
    private int selectedSuit = 0;
    private int[] cardImageIds;

    //int mResponsePosition = 0;
    //static byte submitDoubt = 0;
    private int responseFormat = 0;
    private byte compareFormat = 0;
    private int mSuitBackground;

    int correct = 0, wrong = 0, missed = 0;
    private StringBuilder mTextAnswer = null, mTextResponse = null;
    private String whitespace;
    //protected CompareAsyncTask task = new CompareAsyncTask(); //use to cancel the async task, don't remember how

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        theme();
        setContentView(activity_recall);
        setTitle(getString(R.string.recall));
        Timber.v("theme() complete");
        makeSpinner1(intent);

        findViewById(R.id.result).setVisibility(View.GONE);
        findViewById(R.id.reset).setVisibility(View.GONE);
            Timber.v(LOG_TAG, "activity created");
    }

    @Override
    public void onBackPressed() {
        if(findViewById(R.id.reset).getVisibility()==View.VISIBLE
                || findViewById(R.id.response_layout).getVisibility()==View.VISIBLE) reset();
        else super.onBackPressed();
    }

    protected void theme(){
        String theme = PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.theme), "AppTheme");
        switch (theme) {
            case "Dark":
                setTheme(R.style.dark);
                mSuitBackground=R.color.color_suit_background_dark;
                break;
            case "Night":
                setTheme(R.style.pitch);
                (this.getWindow().getDecorView()).setBackgroundColor(0xff000000);
                mSuitBackground=R.color.color_suit_background_night;
                break;
            default:
                setTheme(R.style.light);
                mSuitBackground=R.color.color_suit_background_light;
        }
    }

    void makeSpinner1(final Intent intent) {
        categories = new ArrayList<>();
        categories.add(getString(R.string.cd));
        categories.add(getString(binary));
        categories.add(getString(cards));
        categories.add(getString(k));
        categories.add(getString(R.string.d));
        categories.add(getString(numbers));
        categories.add(getString(R.string.g));
        categories.add(getString(c));
        //categories.add(getString(j));
        //categories.add(getString(R.string.h));
        //categories.add(getString(i));
        //categories.add(getString(a));

        final Spinner spinner = (Spinner) findViewById(R.id.discipline_spinner);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>
                (this, android.R.layout.simple_spinner_item, categories);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                findViewById(R.id.response_layout).setVisibility(View.GONE);
                findViewById(R.id.cards_responses).setVisibility(View.GONE);
                makeSpinner2(intent);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        spinner.setSelection(0);
        mDiscipline = intent.getStringExtra("discipline");
        if (BuildConfig.DEBUG) Log.v(LOG_TAG, "discipline is " + mDiscipline);
        if (mDiscipline != null && !mDiscipline.equals("")) {
            spinner.setSelection(categories.indexOf(mDiscipline));
        }
        Timber.v("spinner 1 set");
    }

    void spinnerReset() {
        //if (!answers.isEmpty()) answers.clear();
        //if (!responses.isEmpty()) responses.clear();
        //mResponsePosition = 0;
        correct = 0;
        wrong = 0;
        missed = 0;
    }

    void makeSpinner2(Intent intent) {
        spinnerReset();
        final String discipline = ((Spinner) findViewById(R.id.discipline_spinner)).getSelectedItem().toString();
        Timber.v("item : = = " + discipline);
        if (discipline.equals(getString(R.string.cd))) return;

        final Spinner chose_file = (Spinner) findViewById(R.id.chose_file);
        File dir = new File(getFilesDir().getAbsolutePath() + File.separator + discipline);
        ArrayList<String> fileList = new ArrayList<>();
        fileList.add(getString(R.string.cf));
        File[] files = dir.listFiles();
        if (files == null) {
            Toast.makeText(getApplicationContext(), "Nothing saved. Try practicing", Toast.LENGTH_SHORT).show();
            chose_file.setVisibility(View.GONE);
            return;
        }
        chose_file.setVisibility(View.VISIBLE);
        for (int i = files.length - 1; i >= 0; i--) {
            Timber.d("FileName:" + files[i].getName());
            fileList.add(files[i].getName());
        }
        //chose_file.setAdapter(null);
        ArrayAdapter<String> fileAdapter = new ArrayAdapter<>
                (this, android.R.layout.simple_spinner_item, fileList);
        fileAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chose_file.setAdapter(fileAdapter);
        chose_file.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (chose_file.getSelectedItem()!=null &&
                        chose_file.getSelectedItem().toString().equals(getString(R.string.cf))) return;

                //getAnswers(spinner, item);
                mSpinner = chose_file;
                mDiscipline = discipline;
                mTextAnswer = mTextResponse = null;
                answers.clear();
                responses.clear();

                setResponseLayout();
                findViewById(R.id.discipline_spinner).setVisibility(View.GONE);
                findViewById(R.id.chose_file).setVisibility(View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });
        if (intent != null && intent.getStringExtra("discipline")!=null &&
                !intent.getStringExtra("discipline").equals("") &&
                intent.getBooleanExtra("file exists", false)) {
            chose_file.setSelection(1);
        }

        Timber.v("spinner 2 set");
    }


    void updateGridView() {
        GridView gridView = (GridView) findViewById(R.id.cards_responses);
        CardAdapter adapter = new CardAdapter(this, responses);
        gridView.setAdapter(adapter);
    }

    void cardSelected(int card) {
        card = (card == 0 ? 12 : card - 1);
        responses.add(String.valueOf(card + selectedSuit));
        updateGridView();
        //mAdapter.notifyDataSetChanged();
        if (BuildConfig.DEBUG) Log.v(LOG_TAG, "cardSelected complete");
    }

    void cardResponseLayout() {
        cardImageIds = MakeList.makeCards();
        if (BuildConfig.DEBUG) Log.v(LOG_TAG, "setting card layout...");
        findViewById(R.id.response_input).setVisibility(View.GONE);
        findViewById(R.id.cards_responses).setVisibility(View.VISIBLE);
        compareFormat = 2;
        responseFormat = 2;

        LinearLayout suitLayout = (LinearLayout) findViewById(R.id.card_suit);
        if (suitLayout.getChildCount() != 0) return;
        for (int i = 0; i < 4; i++) {
            final ImageView imageView = new ImageView(this);
            imageView.setImageResource(MakeList.makeSuits()[i]);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            float scale = getResources().getDisplayMetrics().density;
            int dpAsPixels = (int) (8 * scale + 0.5f);
            imageView.setPadding(2 * dpAsPixels, dpAsPixels, 2 * dpAsPixels, dpAsPixels);
            imageView.setId(i);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (int j = 0; j < 4; j++) {
                        findViewById(R.id.card_suit).findViewById(j).setBackgroundColor(0);
                    }
                    view.setBackgroundColor(getResources().getColor(mSuitBackground));
                    selectedSuit = view.getId() * 13;
                    if (BuildConfig.DEBUG) Log.v(LOG_TAG, "selectedSuit = " + selectedSuit);
                }
            });
            suitLayout.addView(imageView);
        }
        suitLayout.findViewById(0).setBackgroundColor(getResources().getColor(mSuitBackground));

        LinearLayout numberLayout = (LinearLayout) findViewById(R.id.card_numbers);
        for (int i = 0; i <= 13; i++) {
            TextView textView = new TextView(this);
            textView.setId(i);
            if (i == 0) textView.setText("A");
            else if (i < 10) textView.setText(String.valueOf(i + 1));
            else if (i == 10) textView.setText("J");
            else if (i == 11) textView.setText("Q");
            else if (i == 12) textView.setText("K");
            textView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            float scale = getResources().getDisplayMetrics().density;
            int dpAsPixels = (int) (8 * scale + 0.5f);
            textView.setPadding(dpAsPixels, 0, dpAsPixels, 0);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 36);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cardSelected(view.getId());
                    if (BuildConfig.DEBUG) Log.v(LOG_TAG, "Selected Card = " + view.getId());
                }
            });
            numberLayout.addView(textView);
        }

        Log.v(LOG_TAG, "Card layout set");
    }

    void simpleResponseLayout() {
        final EditText editText = (EditText) findViewById(R.id.response_input);

/*        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Log.v(LOG_TAG, "onEditorAction started");
                getResponse();
                return true;
            }
        });*/


        switch (((Spinner) findViewById(R.id.discipline_spinner)).getSelectedItem().toString()) {
            case "Numbers":
                responseFormat = 0;
                compareFormat = 0;
                editText.setRawInputType(TYPE_CLASS_NUMBER);
                break;
            case "Binary Digits":
                responseFormat = 3;
                compareFormat = 0;
                editText.setRawInputType(TYPE_CLASS_NUMBER);
                break;
            case "Letters":
                compareFormat = 0;
                responseFormat = 3;
                editText.setRawInputType(TYPE_CLASS_TEXT);
                editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
                break;
            case "Words":
            case "Names":
            case "Places":
                compareFormat = 1;
                responseFormat = 1;
                editText.setRawInputType(TYPE_CLASS_TEXT);
                editText.setImeOptions(EditorInfo.IME_ACTION_NONE);
                break;
        }
    }

    void setResponseLayout() {
        Timber.v(((Spinner) findViewById(R.id.discipline_spinner)).getSelectedItem().toString());
        if (((Spinner) findViewById(R.id.discipline_spinner)).getSelectedItem().toString() == getString(R.string.cards)) {
            findViewById(R.id.result).setVisibility(View.GONE);
            findViewById(R.id.cards_responses).setVisibility(View.VISIBLE);

            cardResponseLayout();
        } else {
            findViewById(R.id.card_suit).setVisibility(View.GONE);
            findViewById(R.id.card_numbers).setVisibility(View.GONE);
            findViewById(R.id.cards_responses).setVisibility(View.GONE);

            simpleResponseLayout();
        }
        findViewById(R.id.recall_layout).setVisibility(View.GONE);
        findViewById(R.id.progress_bar_recall).setVisibility(View.GONE);
        findViewById(R.id.response_layout).setVisibility(View.VISIBLE);
        findViewById(R.id.reset).setVisibility(View.GONE);

        Timber.v("responseLayout set");
    }

    void getAnswers(Spinner spinner) {
        if (BuildConfig.DEBUG) Log.v(LOG_TAG, "getAnswersEntered");
        try {
            String string;

            Scanner scanner = new Scanner(new File(getFilesDir().getAbsolutePath() +
                    File.separator + mDiscipline + File.separator +
                    spinner.getSelectedItem().toString())).useDelimiter("\t|\n|\n\n");

            while (scanner.hasNext()) {
                string = scanner.next();
                if (mDiscipline == getString(numbers) || mDiscipline == getString(cards))
                    answers.add(String.valueOf(parseInt(string.trim())));
                    //else if (mDiscipline == getString(e))
                else if (mDiscipline.equalsIgnoreCase(getString(R.string.k)) ||
                        mDiscipline.equalsIgnoreCase(getString(R.string.binary))) {
                    for (char c : string.toCharArray())
                        if (c != ' ') answers.add("" + c);
                } else answers.add(string);
            }
            if (BuildConfig.DEBUG) Log.v(LOG_TAG, String.valueOf(answers.size()));
            scanner.close();
        } catch (Exception e) {
            //Toast.makeText(this, "Couldn't read the saved answers", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        if (BuildConfig.DEBUG) Log.v(LOG_TAG, "getAnswers() complete");
    }

    String giveUp() {
        if (BuildConfig.DEBUG) Log.v(LOG_TAG, "Give Up! pressed");
        Scanner scanner = null;
        String whitespace;
        switch (responseFormat) {
            case 0:
            case 3:
                whitespace = " " + getString(R.string.tab);
                break;
            default:
                whitespace = " \n";
        }
        try {
            StringBuilder sb = new StringBuilder("");
            scanner = new Scanner(new File(getFilesDir().getAbsolutePath() +
                    File.separator + mDiscipline + File.separator +
                    mSpinner.getSelectedItem().toString())).useDelimiter("\t|\t   \t|\n|\n\n");
            if (BuildConfig.DEBUG) Log.v(LOG_TAG, "scanner created");
            if (mDiscipline == getString(R.string.cards)) {
                String[] cards = makeCardString();
                while (scanner.hasNext())
                    sb.append(cards[Integer.parseInt(scanner.next())]).append(whitespace);
            } else while (scanner.hasNext()) sb.append(scanner.next()).append(whitespace);
            if (BuildConfig.DEBUG) Log.v(LOG_TAG, "giveUp() complete, returns " + sb.toString());
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (scanner != null)
                scanner.close();
        }
        if (BuildConfig.DEBUG) Log.v(LOG_TAG, "giveUp() complete, returns null");
        return null;
    }

    void getResponse() {
        if (responseFormat == 2) {
            return;
            //The responses were stored when they were entered
        }
        EditText editText = (EditText) findViewById(R.id.response_input);
        String values = editText.getText().toString(), value = "";

        if (responseFormat == 3) {
            for (int i = 0; i < values.length(); i++) {
                if (values.charAt(i) == ' ' || values.charAt(i) == '\n') continue;
                responses.add(String.valueOf(values.charAt(i)));
            }
        }
        //while (responses.size() <= mResponsePosition) responses.add(" ");
        char delimiter = (responseFormat == 0 ? ' ' : '\n');
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
        String text = ((TextView) findViewById(R.id.responses_text)).getText() + " " + getString(R.string.tab) + value;
        ((TextView) findViewById(R.id.responses_text)).setText(text);
        if (BuildConfig.DEBUG) Log.v(LOG_TAG, "onEditorAction complete");
        editText.setText("");
    }

    void hideResponseLayout() {
        findViewById(R.id.response_layout).setVisibility(View.GONE);
        //findViewById(R.id.check).setVisibility(View.GONE);
        findViewById(R.id.discipline_spinner).setVisibility(View.GONE);
        findViewById(R.id.progress_bar_recall).setVisibility(View.GONE);
        findViewById(R.id.chose_file).setVisibility(View.GONE);
        findViewById(R.id.reset).setVisibility(View.VISIBLE);
        findViewById(R.id.recall_text_layout).setVisibility(View.VISIBLE);
        findViewById(R.id.recall_layout).setVisibility(View.VISIBLE);
    }


    void compare(boolean words) {
        Timber.v("Comparing answers and responses in background");
        mTextResponse = new StringBuilder("");
        mTextAnswer = new StringBuilder("");
        whitespace = compareFormat > 0 ? "<br/>" : getString(R.string.tab) + " &nbsp ";
        Timber.v("whitespace = " + whitespace + ".");

        for (int i = 0, j = 0; i < responses.size() && j < answers.size(); i++, j++) {
            Timber.v("Entered loop - response" + responses.get(i) + "answer – " + answers.get(j));
            if (missed > 8 && missed > correct) {
                for (; i < responses.size(); i++)
                    mTextResponse.append("<font color=#FF0000>")
                            .append(responses.get(i)).append("</font>").append(" ").append(whitespace);
                break;
            }
            if (isLeft(i, j)) continue;
            if (isCorrect(i, j)) continue;
            if (words) {
                if (isSpelling(i, j)) {
                    mTextAnswer.append("<font color=#DDDD00>").append(answers.get(j)).
                            append("</font>").append(" ").append(whitespace);
                    mTextResponse.append("<font color=#DDDD00>").append(responses.get(i)).
                            append("</font>").append(" ").append(whitespace);
                    correct++;
                    continue;
                }
            }
            if (isMissOrWrong(i, j)) i--;
        }
        Log.v(LOG_TAG, "compare() complete ");
    }

    boolean isLeft(int i, int j) {
        if (responses.get(i).equals(" ")) {
            missed++;
            mTextAnswer.append(answers.get(j)).append(" ").append(whitespace);
            mTextResponse.append("<font color=#FF9500>").append(answers.get(j)).append("</font>").append(" ").append(whitespace);
            return true;
        } else return false;
    }

    boolean isCorrect(int i, int j) {
        if (responses.get(i).equalsIgnoreCase(answers.get(j))) {
            correct++;
            mTextAnswer.append(answers.get(j)).append(" ").append(whitespace);
            mTextResponse.append(responses.get(i)).append(" ").append(whitespace);
            return true;
        } else return false;
    }

    boolean isMissOrWrong(int i, int j) {
        boolean miss;

            int match = 0, k;
            k= i>15 ? -10 : 1;
            for (; k <= 10 && i + k < responses.size() && j + k < answers.size(); k++) {
                //if (i + k < responses.size() && j + k < answers.size()) {
                if (!responses.get(i + k).equals(" ")) {
                    if (responses.get(i + k).equalsIgnoreCase(answers.get(j + k))) {
                        match++;
                    }
                }
                //}
            }

            if (((float) match / k) > 0.4) {
                wrong++;
                mTextAnswer.append("<font color=#FF0000>").append(answers.get(j)).append("</font>")
                        .append(" ").append(whitespace);
                mTextResponse.append("<font color=#FF0000>").append(responses.get(i))
                        .append("</font>").append(" ").append(whitespace);
                miss = false;
            } else {
                missed++;
                miss = true;
            }
        if (miss) {
            mTextAnswer.append(answers.get(j)).append(" ").append(whitespace);
            mTextResponse.append("<font color=#FF9500>").append(answers.get(j)).append("</font>")
                    .append(" ").append(whitespace);
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

    void compareCards() {
        String[] cardStrings = makeCardString();
        for (int i = 0; i < responses.size(); i++) {
            responses.set(i, cardStrings[Integer.parseInt(responses.get(i))]);
        }
        for (int i = 0; i < answers.size(); i++) {
            answers.set(i, cardStrings[Integer.parseInt(answers.get(i))]);
        }
        compare(false);
    }


    private void reset() {
        answers.clear();
        responses.clear();
        mTextAnswer = new StringBuilder("");
        mTextResponse = new StringBuilder("");
        correct = 0;
        wrong = 0;
        missed = 0;
        setResponseLayout();
        if (compareFormat == 2) {
            updateGridView();
        } else {
            findViewById(R.id.reset).setVisibility(View.GONE);
        }
        ((Chronometer) findViewById(R.id.time_elapsed_value)).stop();
        findViewById(R.id.result).setVisibility(View.GONE);
        findViewById(R.id.recall_layout).setVisibility(View.GONE);
        findViewById(R.id.response_layout).setVisibility(View.GONE);
        findViewById(R.id.discipline_spinner).setVisibility(View.VISIBLE);
        makeSpinner2(null);
        //findViewById(R.id.response_layout).setVisibility(View.VISIBLE);

        //((TextView) findViewById(R.id.responses_text)).setText("");
        //((TextView) findViewById(R.id.answers_text)).setText("");


        if (BuildConfig.DEBUG) Log.v(LOG_TAG, "Recall Reset Complete");
    }

    public void reset(View view) {
        reset();
    }

    public void check(View view) {
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

    private class CompareAsyncTask extends AsyncTask<ArrayList<Integer>, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            findViewById(R.id.cards_responses).setVisibility(View.GONE);
            getResponse();
            findViewById(R.id.progress_bar_recall).setVisibility(View.VISIBLE);
            //Log.v(LOG_TAG, "answers.size() = " + String.valueOf(answers.size()));
            if (BuildConfig.DEBUG)
                Log.v(LOG_TAG, "responses.size() = " + String.valueOf(responses.size()));
        }

        @SafeVarargs
        @Override
        protected final Void doInBackground(ArrayList<Integer>... a) {
            getAnswers(mSpinner);
            if (compareFormat == 0) compare(false);
            else if (compareFormat == 1) compare(true);
            else compareCards();
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            hideResponseLayout();

            ((TextView) findViewById(R.id.responses_text)).setText(Html.fromHtml(mTextResponse.toString()),
                    TextView.BufferType.SPANNABLE);
            findViewById(R.id.answers_text).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.answers_text)).setText(Html.fromHtml(mTextAnswer.toString()),
                    TextView.BufferType.SPANNABLE);

            if (BuildConfig.DEBUG)
                Log.v(LOG_TAG, "mTextResponse length = " + String.valueOf(mTextAnswer.toString().length()));
            if (BuildConfig.DEBUG)
                Log.v(LOG_TAG, "mTextAnswer length = " + String.valueOf(mTextResponse.toString().length()));

            if (BuildConfig.DEBUG) Log.v(LOG_TAG, "answer 0 = " + answers.get(0));

            if (correct == answers.size() && mDiscipline != getString(binary)) {
                SharedPreferences sharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(getApplicationContext());
                int i = sharedPreferences.getInt("level", 1);
                sharedPreferences.edit().putInt("level", ++i).apply();
            }

            findViewById(R.id.responses_text).setVisibility(View.VISIBLE);
            findViewById(R.id.result).setVisibility(View.VISIBLE);
            findViewById(R.id.recall_layout).setVisibility(View.VISIBLE);

            ((TextView) findViewById(R.id.no_of_correct)).setText(String.valueOf(correct));
            ((TextView) findViewById(R.id.no_of_wrong)).setText(String.valueOf(wrong));
            ((TextView) findViewById(R.id.value_of_score)).setText(String.valueOf(correct - 10 * (wrong + missed)));

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
            findViewById(R.id.cards_responses).setVisibility(View.GONE);
            TextView textAnswers = (TextView) findViewById(R.id.answers_text);
            textAnswers.setText(s);
            textAnswers.setVisibility(View.VISIBLE);

        }
    }

    private class CardAdapter extends ArrayAdapter<String> {

        CardAdapter(Activity context, ArrayList<String> cards) {
            super(context, 0, cards);
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ImageView imageView = (ImageView) convertView;
            if (convertView == null) {
                imageView = new ImageView(getApplicationContext());
                imageView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                imageView.setVisibility(View.VISIBLE);
                //imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.setAdjustViewBounds(true);

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        responses.remove(position);
                        updateGridView();
                    }
                });
                //imageView.setPadding(8, 8, 8, 8);
            }

            Picasso
                    //.setLoggingEnabled(true)
                    .with(getApplicationContext())
                    .load(cardImageIds[parseInt(responses.get(position))])
                    .placeholder(R.drawable.sa)
                    .fit()
                    //.centerInside()                 // or .centerCrop() to avoid a stretched imageÒ
                    .into(imageView);
            //imageView.setImageResource(cardImageIds[parseInt(responses.get(position))]);

            if (BuildConfig.DEBUG) Log.v(LOG_TAG, "getView() complete");

            //((ImageView) listItemView.findViewById(R.id.card_image)).setImageResource(
            //      cardImageIds[parseInt(responses.get(position))]);
            return imageView;//listItemView;
        }
    }
}

//TODO: key
//TODO: receive intent from Disciplines

//orange:FFA500
// textView.setText(Html.fromHtml(styledText), TextView.BufferType.SPANNABLE);