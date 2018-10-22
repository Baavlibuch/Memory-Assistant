package com.memory_athlete.memoryassistant.disciplines;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.memory_athlete.memoryassistant.R;
import com.memory_athlete.memoryassistant.recall.RecallComplex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

import timber.log.Timber;

public abstract class ComplexDisciplineFragment extends WordDisciplineFragment {
    @Override
    protected RandomAdapter startRandomAdapter(ArrayList list) {
        return new RandomAdapter(activity, list, 18);
    }
}
