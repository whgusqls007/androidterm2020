package com.example.androidterm2020;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

public class AchieveBeforeFragment extends Fragment {
    PieChart mPieChart;
    EditText Ac;
    EditText Nac;
    TextView achieve;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = (View) inflater.inflate(R.layout.achieve_graph_before, container, false);
        double achievementValue = getArguments().getDouble("achievementValue");

        mPieChart = (PieChart) rootView.findViewById(R.id.piechart);
        Ac = (EditText) rootView.findViewById(R.id.ac);
        Nac = (EditText) rootView.findViewById(R.id.Nac);
        achieve = rootView.findViewById(R.id.achieveBefore);
        achieve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Achieve)getActivity()).updateSchedule();
                ((Achieve)getActivity()).setFragment();
            }
        });


        mPieChart.clearChart();


        mPieChart.addPieSlice(new PieModel("달성", (int)achievementValue, Color.parseColor("#CDA67F")));
        mPieChart.addPieSlice(new PieModel("미달성", 100-(int)achievementValue, Color.parseColor("#00BFFF")));

        // 화면에 보여주는 것.
        Ac.setText(String.format("%.2f", achievementValue) + "%");
        Nac.setText(String.format("%.2f", 100.0 - achievementValue) + "%");

        mPieChart.startAnimation();

        return rootView;
    }





}
