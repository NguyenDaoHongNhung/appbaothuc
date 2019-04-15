package com.example.appbaothuc.alarmsetting;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.appbaothuc.Alarm;
import com.example.appbaothuc.AlarmAdapter;
import com.example.appbaothuc.R;
import com.example.appbaothuc.UpcomingAlarmFragment;
import com.example.appbaothuc.interfaces.OnOpenSettingAlarmFragment;
import com.example.appbaothuc.interfaces.SettingAlarmFragmentListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

enum SettingAlarmMode{
    ADD_NEW, EDIT
}
public class SettingAlarmFragment extends Fragment implements LableDialogFragment.LabelDialogListener,
        AgainDialogFragment.AgainDialogListener, RepeatDialogFragment.RepeatDialogListener, View.OnClickListener, OnOpenSettingAlarmFragment {
    private Context context;
    private UpcomingAlarmFragment upcomingAlarmFragment;
    private SettingAlarmFragmentListener listener;

    private SettingAlarmMode settingAlarmMode;
    private Alarm alarm;
    private TimePicker timePicker; // Chọn giờ
    private Button btnPlayMusic, btnCancel, btnDelete, btnOk; //Phát nhạc đang chọn, Hủy thao tác, Xóa báo thức, Hoàn tất
    private LinearLayout linearLayoutLabel, linearLayoutType, linearLayoutRingTone,
            linearLayoutRepeat, linearLayoutAgain;
    private TextView textViewPlus10M, textViewMinus10M, textViewPlus1H,
            textViewMinus1H;
    public TextView textViewTimeLeft /*thời gian còn lại*/, textViewType, textViewRepeat,
            textViewRingTone, textViewAgain, textViewLabel;
    private ImageView imageViewType; //cái hình điện thoại rung
    private SeekBar seekBar; // thanh âm lượng
    private Switch aSwitch; // bật tắt rung

    public static YourRingTone yourRingTone;
    public static String label, outputAgain, outputRepeat;
    public static int hour, minute, snoozeTime, volume;
    public static List<Boolean> listRepeatDay;
    public static boolean vibrate;
    public static int challengeType; // chua co
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting_alarm, container, false);
        setControl(view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }
    private Alarm createDefaultAlarm(){
        List<Boolean> listRepeatDay = Arrays.asList(true, true, true, true, true, false, false);
        return new Alarm(true, R.integer.default_hour,R.integer.default_hour, listRepeatDay);
    }

    void setControl(View view){
        yourRingTone = new YourRingTone(null, null);
        listRepeatDay = alarm.getListRepeatDay();
        btnPlayMusic = view.findViewById(R.id.btnPlayMusic);
        btnCancel = view.findViewById(R.id.btnCancel);
        btnDelete = view.findViewById(R.id.btnDelete);
        btnOk = view.findViewById(R.id.btnOk);
        linearLayoutType = view.findViewById(R.id.linearLayoutType);
        linearLayoutRingTone = view.findViewById(R.id.linearLayoutRingTone);
        linearLayoutRepeat = view.findViewById(R.id.linearLayoutRepeat);
        linearLayoutAgain = view.findViewById(R.id.linearLayoutAgain);
        linearLayoutLabel = view.findViewById(R.id.linearLayoutLabel);

        textViewTimeLeft = view.findViewById(R.id.textViewTimeLeft);
        textViewPlus10M = view.findViewById(R.id.textViewPlus10M);
        textViewMinus10M = view.findViewById(R.id.textViewMinus10M);
        textViewPlus1H = view.findViewById(R.id.textViewPlus1H);
        textViewMinus1H = view.findViewById(R.id.textViewMinus1H);
        textViewType = view.findViewById(R.id.textViewType);
        textViewRepeat = view.findViewById(R.id.textViewRepeat);
        textViewRingTone = view.findViewById(R.id.textViewRingTone);
        textViewAgain = view.findViewById(R.id.textViewAgain);
        textViewLabel = view.findViewById(R.id.textViewLabel);

        imageViewType = view.findViewById(R.id.imageViewType);
        seekBar = view.findViewById(R.id.seekBar);
        aSwitch = view.findViewById(R.id.aSwitch);
        timePicker = view.findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);

        textViewMinus1H.setOnClickListener(this); // event trong hàm onClick()
        textViewMinus10M.setOnClickListener(this);
        textViewPlus1H.setOnClickListener(this);
        textViewPlus10M.setOnClickListener(this);

        btnOk.setOnClickListener(new View.OnClickListener() {
            //@RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                outputAgain = textViewAgain.getText().toString();
                label = textViewLabel.getText().toString();
                if(Build.VERSION.SDK_INT >= 23){
                    hour = timePicker.getHour();
                    minute = timePicker.getMinute();
                }
                else{
                    hour = timePicker.getCurrentHour();
                    minute = timePicker.getCurrentMinute();
                }
                volume = seekBar.getProgress();
                if(aSwitch.isChecked()) vibrate = true;
                else vibrate = false;
                String tst = label + " _ " + outputAgain + " _ "
                        + hour + " _ " + minute;

                Toast.makeText(context, tst, Toast.LENGTH_SHORT).show();





                alarm.setHour(hour);
                alarm.setMinute(minute);
                alarm.setListRepeatDay(listRepeatDay);
                alarm.setRingtoneUrl(yourRingTone.getUriRingTone());
                alarm.setRingtoneName(yourRingTone.getNameRingTone());
                alarm.setLabel(label);
                alarm.setVibrate(vibrate);
                alarm.setSnoozeTime(snoozeTime);
                alarm.setVolume(volume);
                alarm.setChallengeType(challengeType);
                if(settingAlarmMode == SettingAlarmMode.EDIT){
                    listener.onEditAlarm(alarm);
                }
                else if(settingAlarmMode == SettingAlarmMode.ADD_NEW){
                    listener.onAddNewAlarm(alarm);
                }
                getFragmentManager().beginTransaction().remove(SettingAlarmFragment.this).commit();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().remove(SettingAlarmFragment.this).commit();
            }
        });
        linearLayoutType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, TypeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });
        linearLayoutLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLableDialog();
            }
        });

        linearLayoutAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAgainDialog();
            }
        });

        linearLayoutRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRepeatDialog();
            }
        });
        btnPlayMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playMusic();
            }
        });
    }





    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View view) {
        int timeHour = timePicker.getHour();
        int timeMinute = timePicker.getMinute();
        switch (view.getId()){
            case R.id.textViewMinus1H:  // bấm trừ 1 giờ
                if(timeHour == 0) timePicker.setHour(23);
                else  timePicker.setHour(timeHour - 1);
                break;
            case R.id.textViewPlus1H:   // bấm cộng 1 giờ
                if(timeHour == 23) timePicker.setHour(0);
                else  timePicker.setHour(timeHour + 1);
                break;
            case R.id.textViewMinus10M: // Bấm trừ 10 phút
                if(timeMinute - 10 < 0) {   // nếu trừ 10 phút mà ra số âm thì phải trừ giờ xuống 1.
                    if(timeHour == 0) timePicker.setHour(23);
                    else  timePicker.setHour(timeHour - 1);
                    timePicker.setMinute(60 - (10 - timeMinute));   // phút thì còn tầm 5 mươi mấy đó theo công thức
                }
                else timePicker.setMinute(timeMinute - 10);
                break;
            case R.id.textViewPlus10M:
                if(timeMinute + 10 > 59) {
                    if(timeHour == 23) timePicker.setHour(0);
                    else timePicker.setHour(timeHour + 1);
                    timePicker.setMinute(10 + timeMinute - 60);
                }
                else timePicker.setMinute(timeMinute + 10);
                break;
        }
    }
    private void showLableDialog() { // show fragment để Sửa tên báo thức

        LableDialogFragment lableDialogFragment = new LableDialogFragment();
        lableDialogFragment.show(getChildFragmentManager(), "fragment_edit_name");
    }
    @Override
    public void onFinishEditDialog(String inputText) { //get text ở label dialog fragment
        textViewLabel.setText(inputText);
    }


    private void showAgainDialog() { // fragment chọn thời gian báo thức lại
        AgainDialogFragment againDialogFragment = new AgainDialogFragment();
        againDialogFragment.show(getChildFragmentManager(), "fragment_choice");
    }
    @Override
    public void onFinishChoiceDialog(Integer input) {
        snoozeTime = input;
        if(input == 0) textViewAgain.setText("Tắt");
        else textViewAgain.setText(input + " Phút.");
    }

    private void showRepeatDialog() { // fragment chọn các ngày báo thức
        RepeatDialogFragment repeatDialogFragment = new RepeatDialogFragment();
        repeatDialogFragment.setListener(this);
        repeatDialogFragment.show(getChildFragmentManager(), "fragment_repeat");
    }
    @Override
    public void onFinishCheckDialog(ArrayList<Boolean> input) {
        listRepeatDay = new ArrayList<>();
        for(int i = 0; i < 7; i++){
            listRepeatDay.add(i, false);
            listRepeatDay.set(i, input.get(i));
        }
        createStringRepeat(input);
    }

    public void createStringRepeat(ArrayList<Boolean> listDays){
        String repeatString = "";
        int i = 0;
        for(i = 0; i < 7; i++){
            if(listDays.get(i) == false) break;
        }
        if(i == 7) {
            outputRepeat = "Hằng ngày.";
            textViewRepeat.setText(outputRepeat);
            return;
        }
        else if(i == 5){
            outputRepeat = "Các ngày làm việc.";
            textViewRepeat.setText(outputRepeat);
            return;
        }
        i = 0;
        for(i = 0; i < 5; i++){
            if(listDays.get(i) == true) break;
        }
        if(i == 5 && listDays.get(5) == true && listDays.get(6) == true ){
            outputRepeat = "Cuối tuần.";
            textViewRepeat.setText(outputRepeat);
            return;
        }
        else {
            for(int j = 0; j < 7; j++){
                if(listDays.get(j) == true){
                    if(j == 6) repeatString += " CN";
                    else repeatString += " T" + (j+2);
                }
            }
            outputRepeat = repeatString;
            textViewRepeat.setText(outputRepeat);
        }
    }

    static boolean play = false;
    public void getMusic(){
        Map<String, String> list = getNotifications();
        String a = "";
        String b = "";
        for(HashMap.Entry<String, String> h : list.entrySet())
        { b = h.getValue();
            a += h.getKey();
            a+= h.getValue() + "\n";
        }



    }
    public void playMusic(){
        getMusic();


        if(play == true){
            play = false;
            btnPlayMusic.setBackgroundResource(R.drawable.ic_play_arrow_24dp);
        }
        else {
            try {

                Uri defaultRintoneUri = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE);
                Ringtone defaultRingtone = RingtoneManager.getRingtone(context, defaultRintoneUri);
                defaultRingtone.play();
//                Ringtone defaultRingtone = RingtoneManager.getRingtone(getApplicationContext(), Uri.parse("content://media/internal/audio/media/156"));
//                defaultRingtone.play();
            } catch (Exception e) {
                e.printStackTrace();
            }
            play = true;
            btnPlayMusic.setBackgroundResource(R.drawable.ic_pause_black_24dp);
        }
    }


    public Map<String, String> getNotifications() {
        RingtoneManager manager = new RingtoneManager(context);
        manager.setType(RingtoneManager.TYPE_RINGTONE);
        Cursor cursor = manager.getCursor();

        Map<String, String> list = new HashMap<>();
        while (cursor.moveToNext()) {
            String notificationTitle = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX);
            String notificationUri = cursor.getString(RingtoneManager.URI_COLUMN_INDEX) + "/" + cursor.getString(RingtoneManager.ID_COLUMN_INDEX);

            list.put(notificationUri, notificationTitle);
        }
        return list;
    }











    // Override
    @Override
    public void onInitialize(UpcomingAlarmFragment upcomingAlarmFragment, Alarm alarm) {
        this.upcomingAlarmFragment = upcomingAlarmFragment;
        this.listener = upcomingAlarmFragment;
        this.alarm = alarm;
        if(alarm == null){
            this.settingAlarmMode = SettingAlarmMode.ADD_NEW;
            this.alarm = createDefaultAlarm();
        }
        else{
            this.settingAlarmMode = SettingAlarmMode.EDIT;
        }
    }
}