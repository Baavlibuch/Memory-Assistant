package com.memory_athlete.memoryassistant.disciplines;

import android.content.Intent;
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

import com.memory_athlete.memoryassistant.R;
import com.memory_athlete.memoryassistant.main.Recall;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import timber.log.Timber;

import static com.memory_athlete.memoryassistant.R.string.j;


public class Numbers extends DisciplineFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ((EditText) rootView.findViewById(R.id.no_of_values)).setHint(getString(R.string.enter) + " " + getString(R.string.st));
        rootView.findViewById(R.id.negative).setVisibility(View.VISIBLE);
        rootView.findViewById(R.id.decimal).setVisibility(View.VISIBLE);

        Timber.v("Activity Created");
        return rootView;
    }

    @Override
    protected String background() {
        //Text
        StringBuilder stringBuilder = new StringBuilder();
        Random rand = new Random();
        int n1 = 1, n2 = 0;         //n1* for upper limit, n2* for lower limit
        if (((CheckBox) rootView.findViewById(R.id.negative)).isChecked()) {
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
                if (((CheckBox) rootView.findViewById(R.id.negative)).isChecked() && n >= 0 && i > 0)
                    stringBuilder.append("  ");

                //Adding the number to the final string
                stringBuilder.append(n).append(getString(R.string.tab));    //\t is the delimiter for recall

                //Minimum space between 2 numbers
                for (int j = 0; j <= a.get(GROUP_SIZE); j++) {
                    stringBuilder.append(" ");
                }

                //Bigger numbers should have more space between them to look nice
                for (int j = 0; j / 2 <= 2 * a.get(GROUP_SIZE) - Double.toString(n).length() + 1; j++) {
                    stringBuilder.append(" ");
                }
                Timber.v("Entered " + j);
                if (n < 0) stringBuilder.append(" ");   //handling negatives

                if (a.get(RUNNING) == FALSE) break;
            }
        } else {                                        //handling int
            for (int i = 0; i < a.get(GROUP_SIZE); i++) {
                int n;
                //-1 to ensure that the numbers fill the entire range
                n = n1 * rand.nextInt((int) Math.pow(10, a.get(GROUP_SIZE)))
                        - n2 * ((int) Math.pow(10, a.get(GROUP_SIZE)) - 1);

                //Handling extra characters to keep numbers tabulated
                //extra spaces are added when numbers are smaller in length

                //Handling - sign
                if (((CheckBox) rootView.findViewById(R.id.negative)).isChecked() && n >= 0 && i > 0)
                    stringBuilder.append(" ");

                //Adding the value to the final string
                stringBuilder.append(n).append(getString(R.string.tab));            //\t is the delimiter for recall

                //Minimum space between 2 numbers
                for (int j = 0; j <= a.get(GROUP_SIZE) / 2; j++) {
                    stringBuilder.append(" ");
                }

                //Bigger numbers should have more space between them to look nice
                for (int j = 0; j / 2 <= a.get(GROUP_SIZE) - Integer.toString(n).length(); j++) {
                    stringBuilder.append(" ");
                }

                if (n < 0) stringBuilder.append(" ");
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

    @Override
    protected void startCommon() {
        super.startCommon();
        rootView.findViewById(R.id.negative).setVisibility(View.GONE);
        rootView.findViewById(R.id.decimal).setVisibility(View.GONE);
    }

    @Override
    protected void reset() {
        super.reset();
        rootView.findViewById(R.id.decimal).setVisibility(View.VISIBLE);
        rootView.findViewById(R.id.negative).setVisibility(View.VISIBLE);
    }

    @Override
    protected boolean save() {
        if (((RadioButton) rootView.findViewById(R.id.standard_radio)).isChecked()
                || ((Spinner) rootView.findViewById(R.id.group)).getSelectedItemPosition() < 2) {
            //Case with single digits

            String string = ((TextView) rootView.findViewById(R.id.random_values)).getText().toString();
            if (string.equals("")) return false;

            //Directory is Digits, not Numbers
            String fname = getActivity().getFilesDir().getAbsolutePath() + File.separator
                    + getString(R.string.practice) + File.separator + "Digits" + File.separator
                    + ((new SimpleDateFormat("yy-MM-dd_HH:mm")).format(new Date())) + ".txt";
            String dirPath = getActivity().getFilesDir().getAbsolutePath()  + File.separator
                    + getString(R.string.practice) + File.separator + "Digits";
            File pDir = new File(dirPath);
            boolean isDirectoryCreated = pDir.exists();
            if (!isDirectoryCreated) {
                isDirectoryCreated = pDir.mkdir();
            }
            if (isDirectoryCreated) {
                try {
                    FileOutputStream outputStream = new FileOutputStream(new File(fname));
                    outputStream.write(string.getBytes());

                    outputStream.close();
                    Toast.makeText(getActivity(), "Saved", Toast.LENGTH_SHORT).show();
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Try again", Toast.LENGTH_SHORT).show();
                }
            } else throw new RuntimeException("Couldn't create the directory of the discipline");
            return false;
        }
        //Case with more digits than 1 or Custom
        return super.save();
    }

    @Override
    protected void recall() {
        if (((RadioButton) rootView.findViewById(R.id.standard_radio)).isChecked()
                || ((Spinner) rootView.findViewById(R.id.group)).getSelectedItemPosition() < 2) {
            //Recall Digits
            Intent intent = new Intent(getActivity(), Recall.class);
            intent.putExtra("file exists", save());
            intent.putExtra("discipline", "Digits");
            Timber.v("recalling " + "Digits");
            startActivity(intent);
        } else super.recall();  //Recall Numbers
    }
}