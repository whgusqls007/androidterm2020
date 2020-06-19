package com.example.androidterm2020;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.fragment.app.DialogFragment;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    boolean is_start;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String[] time = getArguments().getString("time").split(":");
        int hour = Integer.parseInt(time[0]);
        int minute = Integer.parseInt(time[1]);
        is_start = getArguments().getBoolean("is_start");

        // 여기서 hour, minute는 초기값.
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, this,
                hour, minute, DateFormat.is24HourFormat(getActivity()));

        TextView title = new TextView(getActivity());
        title.setText("시간을 설정하십시오.");
        title.setTextSize(25);
        title.setGravity(Gravity.CENTER_HORIZONTAL);
        // 더 예쁘게 수정하자. 글자크기, 배경.
        timePickerDialog.setCustomTitle(title);

        return timePickerDialog;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // 시간을 정하면 TextView에 시간을 변경해주는 기능.
        TextView textView = null;

        if(is_start == true) {
            textView = getActivity().findViewById(R.id.editScheduleStrDate);
        }
        else {
            textView = getActivity().findViewById(R.id.editScheduleEndDate);
        }

        String text = textView.getText().toString().split(" ")[0];
        text += " " + (hourOfDay < 10 ? "0" : "" ) + hourOfDay + ":" + (minute < 10 ? "0" : "" ) + minute;
        textView.setText(text);
    }
}
