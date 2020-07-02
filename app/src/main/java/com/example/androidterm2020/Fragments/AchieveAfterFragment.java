package com.example.androidterm2020.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.androidterm2020.R;
import com.example.androidterm2020.RoomDB.Schedule;
import com.example.androidterm2020.RoomDB.ScheduleViewModel;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

public class AchieveAfterFragment extends Fragment {
    PieChart mPieChart;
    EditText Ac;
    EditText Nac;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = (View) inflater.inflate(R.layout.achieve_graph_after, container, false);
        double achievementValue = getArguments().getDouble("achievementValue");

        mPieChart = (PieChart) rootView.findViewById(R.id.piechart);
        Ac = (EditText) rootView.findViewById(R.id.ac);
        Nac = (EditText) rootView.findViewById(R.id.Nac);

        mPieChart.clearChart();


        mPieChart.addPieSlice(new PieModel("달성", (int)achievementValue, Color.parseColor("#00BFFF")));
        mPieChart.addPieSlice(new PieModel("미달성", 100-(int)achievementValue, Color.parseColor("#CDA67F")));

        // 화면에 보여주는 것.
        Ac.setText(String.format("%.2f", achievementValue) + "%");
        Nac.setText(String.format("%.2f", 100.0 - achievementValue) + "%");

        mPieChart.startAnimation();

        return rootView;
    }
}
