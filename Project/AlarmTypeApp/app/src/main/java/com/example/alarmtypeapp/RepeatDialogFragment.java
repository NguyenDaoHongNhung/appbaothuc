package com.example.alarmtypeapp;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;

import java.util.ArrayList;
import java.util.List;

public class RepeatDialogFragment extends DialogFragment {
    private Button btnOK, btnCancel, btnDays, btnWeekend;
    private CheckBox checkBoxT2, checkBoxT3, checkBoxT4, checkBoxT5, checkBoxT6,
            checkBoxT7, checkBoxCN;
    public static ArrayList<Integer> listDays = new ArrayList<>();

    public RepeatDialogFragment(){
    }

    public interface RepeatDialogListener{
        public void onFinishCheckDialog(ArrayList<Integer> arrayList);
    }

    private RepeatDialogListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View viewDialog = inflater.inflate(R.layout.fragment_repeat_dialog, container);
        btnOK = viewDialog.findViewById(R.id.btnOK);
        btnCancel = viewDialog.findViewById(R.id.btnCancel);
        btnDays = viewDialog.findViewById(R.id.btnDays);
        btnWeekend = viewDialog.findViewById(R.id.btnWeekend);

        checkBoxT2 = viewDialog.findViewById(R.id.checkBoxT2);
        checkBoxT3 = viewDialog.findViewById(R.id.checkBoxT3);
        checkBoxT4 = viewDialog.findViewById(R.id.checkBoxT4);
        checkBoxT5 = viewDialog.findViewById(R.id.checkBoxT5);
        checkBoxT6 = viewDialog.findViewById(R.id.checkBoxT6);
        checkBoxT7 = viewDialog.findViewById(R.id.checkBoxT7);
        checkBoxCN = viewDialog.findViewById(R.id.checkBoxCN);

        getDialog().setTitle("Hello");
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        for(int i = 0; i < 7; i++){
            listDays.add(i, 0);
        }

        checkBoxT2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkBoxT2.isChecked() == true) listDays.set(0, 1);
                else listDays.set(0, 0);
                checkDays();
            }
        });
        checkBoxT3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkBoxT3.isChecked() == true) listDays.set(1, 1);
                else listDays.set(1, 0);
                checkDays();
            }
        });
        checkBoxT4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkBoxT4.isChecked() == true) listDays.set(2, 1);
                else listDays.set(2, 0);
                checkDays();
            }
        });
        checkBoxT5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkBoxT5.isChecked() == true) listDays.set(3, 1);
                else listDays.set(3, 0);
                checkDays();
            }
        });
        checkBoxT6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkBoxT6.isChecked() == true) listDays.set(4, 1);
                else listDays.set(4, 0);
                checkDays();
            }
        });
        checkBoxT7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkBoxT7.isChecked() == true) listDays.set(5, 1);
                else listDays.set(5, 0);
                checkDays();
            }
        });
        checkBoxCN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkBoxCN.isChecked() == true) listDays.set(6, 1);
                else listDays.set(6, 0);
                checkDays();
            }
        });

        btnDays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btnDays.getCurrentTextColor() == Color.WHITE) {
                    for(int i = 0; i < 5; i++){
                        listDays.set(i, 0);
                    }
                    colorButtonOff(btnDays);
                }
                else {
                    for(int i = 0; i < 5; i++){
                        listDays.set(i, 1);
                    }
                    colorButtonOn(btnDays);
                }
                if(checkBoxT2.isChecked()== true && checkBoxT3.isChecked()== true &&
                        checkBoxT4.isChecked()== true && checkBoxT5.isChecked()== true &&
                        checkBoxT6.isChecked()== true)
                {
                    checkBoxT2.setChecked(false);
                    checkBoxT3.setChecked(false);
                    checkBoxT4.setChecked(false);
                    checkBoxT5.setChecked(false);
                    checkBoxT6.setChecked(false);
                }
                else{
                    checkBoxT2.setChecked(true);
                    checkBoxT3.setChecked(true);
                    checkBoxT4.setChecked(true);
                    checkBoxT5.setChecked(true);
                    checkBoxT6.setChecked(true);
                }
            }
        });
        btnWeekend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btnWeekend.getCurrentTextColor() == Color.WHITE) {
                    listDays.set(5, 0);
                    listDays.set(6, 0);
                    colorButtonOff(btnWeekend);
                }
                else {
                    listDays.set(5, 1);
                    listDays.set(6, 1);
                    colorButtonOn(btnWeekend);
                }
                if(checkBoxT7.isChecked()== true && checkBoxCN.isChecked()== true)
                {
                    checkBoxT7.setChecked(false);
                    checkBoxCN.setChecked(false);
                }
                else{
                    checkBoxT7.setChecked(true);
                    checkBoxCN.setChecked(true);
                }
            }
        });
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onFinishCheckDialog(listDays);
                getDialog().dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        return viewDialog;
    }
    private void checkDays(){
        if(checkBoxT2.isChecked()== true && checkBoxT3.isChecked()== true &&
                checkBoxT4.isChecked()== true && checkBoxT5.isChecked()== true &&
                checkBoxT6.isChecked()== true){
            colorButtonOn(btnDays);
        }
        else {
            colorButtonOff(btnDays);
        }
        if(checkBoxT7.isChecked()== true && checkBoxCN.isChecked()== true) {
            colorButtonOn(btnWeekend);
        }
        else {
            colorButtonOff(btnWeekend);
        }
    }
    private void colorButtonOn(Button btnD){
        btnD.setBackgroundColor(getResources().getColor(R.color.colorBlue));
        btnD.setTextColor(Color.WHITE);
    }
    private void colorButtonOff(Button btnD){
        btnD.setBackgroundColor(getResources().getColor(R.color.backgroundlayout));
        btnD.setTextColor(getResources().getColor(R.color.colorBlue));
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.listener = (RepeatDialogListener) context;
    }
}