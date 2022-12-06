package com.memory_athlete.memoryassistant.disciplines;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;

import com.memory_athlete.memoryassistant.Helper;
import com.memory_athlete.memoryassistant.R;
import com.memory_athlete.memoryassistant.main.RecallSelector;
import com.memory_athlete.memoryassistant.recall.RecallSimple;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import timber.log.Timber;

public class Numbers extends DisciplineFragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        speechSpeedMultiplier = 1.5f;
        ((EditText) rootView.findViewById(R.id.no_of_values)).setHint(getString(R.string.enter) + " " + getString(R.string.st));
        negativeOrDateCheckBox.setVisibility(View.VISIBLE);
        rootView.findViewById(R.id.decimal).setVisibility(View.VISIBLE);

        Timber.v("Activity Created");
        return rootView;
    }

    @Override
    protected String backgroundString() {
        //Text
        StringBuilder stringBuilder = new StringBuilder();
        Random rand = new Random();
        int n1 = 1, n2 = 0;         //n1* for upper limit, n2* for lower limit
        boolean addZeros = checkPrecedingZeros(),          //To Pad zeros if the setting is enabled
                negative = negativeOrDateCheckBox.isChecked();

        if (negative) {
            n1 = 2;                 //Double the max value of random
            n2 = 1;                 //Subtract from from random to get negative
        }

        //Study the else statement first, its simpler
        if (((CheckBox) rootView.findViewById(R.id.decimal)).isChecked()) {//handling floats numbers
            double n;
            //-1 to ensure that the numbers fill the entire range
            for (int i = 0; i < a.get(NO_OF_VALUES); i++) {
                //Group size is the size of a number
                n = round((rand.nextDouble() * n1 * (Math.pow(10, a.get(GROUP_SIZE)))
                        - n2 * Math.pow(10, a.get(GROUP_SIZE))), a.get(GROUP_SIZE));

                //Handling extra characters to keep numbers tabulated
                //Extra spaces are added when numbers are smaller in length

                //Handling - sign
                if (negative && n >= 0 && i > 0) stringBuilder.append(" ");

                //Adding the number to the final string
                if (addZeros)
                    stringBuilder.append(formatDouble(n)).append(getString(R.string.tab));
                else stringBuilder.append(n).append(getString(R.string.tab));
                //\t is the delimiter for recall

                //Minimum space between 2 numbers
                for (int j = 0; j <= a.get(GROUP_SIZE); j++) {
                    stringBuilder.append(" ");
                }

                //Bigger numbers should have more space between them to look nice
                if (!addZeros) for (int j = 0;
                                    j / 2 <= 2 * a.get(GROUP_SIZE) - Double.toString(n).length() + 1; j++)
                    stringBuilder.append(" ");

                if (n < 0) stringBuilder.append(" ");                   //handling negatives
                else if (addZeros) stringBuilder.append("  ");
                stringBuilder.append(" ");

                if (a.get(RUNNING) == FALSE) break;
            }
        } else {                                        //handling int
            for (int i = 0; i < a.get(NO_OF_VALUES); i++) {
                int n;
                //-1 to ensure that the numbers fill the entire range
                n = n1 * rand.nextInt((int) Math.pow(10, a.get(GROUP_SIZE)))
                        - n2 * ((int) Math.pow(10, a.get(GROUP_SIZE)) - 1);

                //Handling extra characters to keep numbers tabulated
                //extra spaces are added when numbers are smaller in length

                //Handling - sign
                if (negative && n >= 0 && i > 0) stringBuilder.append(" ");

                //Adding the value to the final string
                if (addZeros)
                    stringBuilder.append(formatInt(n)).append(getString(R.string.tab));
                else stringBuilder.append(n).append(getString(R.string.tab));
                //\t is the delimiter for recall

                //Minimum space between 2 numbers
                for (int j = 0; j <= a.get(GROUP_SIZE) / 2; j++) {
                    stringBuilder.append(" ");
                }

                //Bigger numbers should have more space between them to look nice
                if (!addZeros)
                    for (int j = 0; j / 2 <= a.get(GROUP_SIZE) - Integer.toString(n).length(); j++)
                        stringBuilder.append(" ");


                if (n < 0) stringBuilder.append(" ");
                else if (addZeros) stringBuilder.append("  ");
                stringBuilder.append(" ");

                if (a.get(RUNNING) == FALSE) break;
            }
        }
        return stringBuilder.toString();
    }

    public static double round(double d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Double.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_DOWN);
        return bd.doubleValue();
    }

    private boolean checkPrecedingZeros() {
        SharedPreferences s = PreferenceManager.getDefaultSharedPreferences(requireActivity());
        return s.getBoolean(activity.getString(R.string.preceding_zeros), false);
    }

    //pad 0s
    private String formatInt(int n) {
        if (n < 0) return ("-" + String.format("%0" + a.get(GROUP_SIZE) + "d", Math.abs(n)));
        return String.format("%0" + a.get(GROUP_SIZE) + "d", n);
    }

    //pad 0s
    private String formatDouble(double n) {
        if (n < 0) return ("-" + String.format("%0" + (2 * a.get(GROUP_SIZE) + 1) + "."
                + a.get(GROUP_SIZE) + "f", Math.abs(n)));
        return String.format("%0" + (2 * a.get(GROUP_SIZE) + 1) + "." + a.get(GROUP_SIZE) + "f", n);
    }

    @Override
    protected void startCommon() {
        super.startCommon();
        negativeOrDateCheckBox.setVisibility(View.GONE);
        rootView.findViewById(R.id.decimal).setVisibility(View.GONE);
    }

    @Override
    public boolean reset() {
        rootView.findViewById(R.id.decimal).setVisibility(View.VISIBLE);
        negativeOrDateCheckBox.setVisibility(View.VISIBLE);
        return super.reset();
    }

    @Override
    protected boolean save() {
        if (((RadioButton) rootView.findViewById(R.id.standard_radio)).isChecked()
                || ((Spinner) rootView.findViewById(R.id.group)).getSelectedItemPosition() < 2) {
            //Case with single digits

            if (!((CheckBox) rootView.findViewById(R.id.speech_check_box)).isChecked())
                stringToSave = ((TextView) rootView.findViewById(R.id.random_values))
                        .getText().toString();

            if (stringToSave == null || stringToSave.equals("")) return false;
            //Practice Directory

            //From discipline
            //Directory of practice - external storage
            int EXTERNAL_STORAGE_PERMISSION_CODE = 23;
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    EXTERNAL_STORAGE_PERMISSION_CODE);

            File folder = getActivity().getFilesDir();
            String path = folder + File.separator + getString(R.string.practice) + File.separator + activity.getTitle().toString();

            //Directory of practice - internal storage
//        String path = Helper.APP_FOLDER + File.separator
//                + getString(R.string.practice) + File.separator + activity.getTitle().toString();

            Toast.makeText(activity.getApplicationContext(),"discipline is working", Toast.LENGTH_SHORT).show();

            if (Helper.makeDirectory(path, getContext())) {
                path += File.separator + ((new SimpleDateFormat("yy-MM-dd_HH:mm",
                        Locale.getDefault())).format(new Date())) + ".txt";

                //Write the file
                try {
                    FileOutputStream outputStream = new FileOutputStream(new File(path));
                    outputStream.write(stringToSave.getBytes());

                    outputStream.close();
                    Toast.makeText(activity.getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
                    return true;
                } catch (FileNotFoundException e) {
                    Timber.e(e);
                    if (saveErrorCount == 0) {
                        Toast.makeText(activity.getApplicationContext(), R.string.fnf_try_again, Toast.LENGTH_SHORT).show();
                        saveErrorCount++;
                        String[] blocked = {"|", "\\", "?", "*", "<", "\"", ":", ">", "+", "[", "]", "'"};
                        for (CharSequence c : blocked) {
                            String s = path;
                            s = s.replace(c, "");
                            if (!s.equals(path)) {
                                Timber.e("illegal character in path : %s", path);
                                path = s;
                            }
                        }
                        return false;
                    }
                    File f = new File(path);
                    if (!f.exists()) throw new RuntimeException(
                            activity.getTitle().toString() + " directory doesn't exist", e);
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    Timber.e(e);
                    if (saveErrorCount == 0) {
                        Toast.makeText(activity.getApplicationContext(), R.string.io_try_again, Toast.LENGTH_SHORT).show();
                        saveErrorCount++;
                        return false;
                    }
                    File f = new File(path);
                    if (!f.exists() && !f.isDirectory())
                        throw new RuntimeException(path + " doesn't exist", e);
                    throw new RuntimeException(e);
                }
            } else Toast.makeText(getActivity(), R.string.storage_permission_for_recall,
                    Toast.LENGTH_LONG).show();
            return false;



        }
        //Case with more digits than 1 or Custom
        return super.save();
    }

    @Override
    protected void recall() {
        if (((RadioButton) rootView.findViewById(R.id.standard_radio)).isChecked() ||               // standard is equivalent to digits

                (((Spinner) rootView.findViewById(R.id.group)).getSelectedItemPosition() < 2 &&     // single digit
                        !((CheckBox) rootView.findViewById(R.id.decimal)).isChecked() &&            // no decimal
                        !((CheckBox) rootView.findViewById(R.id.negative_or_date)).isChecked())) {  // non negative

            //Recall Digits
            boolean fileExists = save();
            Timber.v("fileExists = %s", fileExists);
            Intent intent;
            if (fileExists) intent = new Intent(getActivity(), RecallSimple.class);
            else intent = new Intent(activity.getApplicationContext(), RecallSelector.class);

//            intent.putExtra("file exists", fileExists);
//            intent.putExtra("discipline", "Digits");

            intent.putExtra("file exists", fileExists);
            intent.putExtra(getString(R.string.discipline), "Digits");

            Timber.i("Numbers/fileExists = %s", fileExists);
            Timber.i("Numbers/discipline = Numbers");

            Timber.v("recalling Numbers");
            startActivity(intent);
        } else super.recall();  //Recall Numbers
    }
}