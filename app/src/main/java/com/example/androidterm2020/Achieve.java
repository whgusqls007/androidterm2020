package com.example.androidterm2020;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

public class Achieve extends AppCompatActivity {
    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.achieve_graph);
        PieChart mPieChart = (PieChart) findViewById(R.id.piechart);
        EditText Ac = (EditText) findViewById(R.id.ac);
        EditText Nac = (EditText) findViewById(R.id.Nac);

        mPieChart.clearChart();

        mPieChart.addPieSlice(new PieModel("달성", 50, Color.parseColor("#CDA67F")));
        mPieChart.addPieSlice(new PieModel("미달성", 50, Color.parseColor("#00BFFF")));
        Ac.setText("달성도 value");
        Nac.setText("미달성도 value");

        mPieChart.startAnimation();
    }
}
