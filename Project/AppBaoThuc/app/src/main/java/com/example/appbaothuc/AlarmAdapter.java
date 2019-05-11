package com.example.appbaothuc;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.appbaothuc.alarmsetting.SettingAlarmFragment;
import com.example.appbaothuc.models.Alarm;

import java.util.Collections;
import java.util.List;

import static com.example.appbaothuc.challenge.ChallengeActivity.ChallengeType.DEFAULT;
import static com.example.appbaothuc.challenge.ChallengeActivity.ChallengeType.MATH;
import static com.example.appbaothuc.challenge.ChallengeActivity.ChallengeType.SHAKE;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder> {
    private Context context;
    private UpcomingAlarmFragment upcomingAlarmFragment;
    private List<Alarm> listAlarm;

    private SparseIntArray mapConstraintLayoutAlarm;
    private SparseIntArray mapButtonAlarm;

    private SettingAlarmFragment settingAlarmFragment;
    private FragmentManager fragmentManager;
    public AlarmAdapter(Context context, UpcomingAlarmFragment upcomingAlarmFragment, List<Alarm> listAlarm) {
        this.context = context;
        this.upcomingAlarmFragment = upcomingAlarmFragment;
        this.listAlarm = listAlarm;

        this.mapConstraintLayoutAlarm = new SparseIntArray();
        this.mapButtonAlarm = new SparseIntArray();

        this.settingAlarmFragment = new SettingAlarmFragment();
        this.fragmentManager = ((FragmentActivity)context).getSupportFragmentManager();
    }

    @Override
    public int getItemCount() {
        return this.listAlarm.size();
    }

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View alarmView = layoutInflater.inflate(R.layout.item_alarm, viewGroup, false);
        return new AlarmViewHolder(alarmView);
    }

    @Override
    public void onBindViewHolder(@NonNull final AlarmViewHolder alarmViewHolder, int i) {
        mapConstraintLayoutAlarm.put(alarmViewHolder.constraintLayoutParent.getId(), i);
        mapButtonAlarm.put(alarmViewHolder.buttonAlarmType.getId(), i);
        final Alarm alarm = listAlarm.get(i);
        ConstraintLayout constraintLayoutParent = alarmViewHolder.constraintLayoutParent;
        final CheckBox checkBoxEnable = alarmViewHolder.checkBoxEnable;
        TextView textViewHour = alarmViewHolder.textViewHour;
        TextView textViewMinute = alarmViewHolder.textViewMinute;
        TextView textViewDescribeRepeatDay = alarmViewHolder.textViewDescribeRepeatDay;
        ImageView buttonAlarmType = alarmViewHolder.buttonAlarmType;

        checkBoxEnable.setChecked(alarm.isEnable());
        textViewHour.setText(String.valueOf(alarm.getHour()));
        if (alarm.getMinute() < 10) {
            textViewMinute.setText("0" + alarm.getMinute());
        }
        else {
            textViewMinute.setText(String.valueOf(alarm.getMinute()));
        }
        textViewDescribeRepeatDay.setText(alarm.getDescribeRepeatDay());

        checkBoxEnable.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                alarm.setEnable(checkBoxEnable.isChecked());
                upcomingAlarmFragment.updateAlarmEnable(alarm);
                listAlarm.set(alarmViewHolder.getAdapterPosition(), alarm);
                Collections.sort(listAlarm);
                AlarmAdapter.this.notifyDataSetChanged();
            }
        });
        constraintLayoutParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Alarm checkedAlarm = listAlarm.get(mapConstraintLayoutAlarm.get(v.getId()));
                MainActivity.validateAlarmRingtoneUrl(context, checkedAlarm);
                settingAlarmFragment.configure(upcomingAlarmFragment, checkedAlarm);
                fragmentManager.beginTransaction().add(R.id.full_screen_fragment_container, settingAlarmFragment).addToBackStack(null).commit();
            }
        });
        buttonAlarmType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Alarm checkedAlarm = listAlarm.get(mapConstraintLayoutAlarm.get(v.getId()));
                MainActivity.validateAlarmRingtoneUrl(context, checkedAlarm);
                settingAlarmFragment.configure(upcomingAlarmFragment, checkedAlarm);
                fragmentManager.beginTransaction().add(R.id.full_screen_fragment_container, settingAlarmFragment).addToBackStack(null).commit();
            }
        });
        switch(alarm.getChallengeType()){
            case DEFAULT:
                buttonAlarmType.setImageDrawable(context.getDrawable(R.drawable.ic_alarm));
                break;
            case MATH:
                buttonAlarmType.setImageDrawable(context.getDrawable(R.drawable.ic_math_36));
                break;
            case SHAKE:
                buttonAlarmType.setImageDrawable(context.getDrawable(R.drawable.icons8_shake_phone_60));
                break;
        }
    }

    class AlarmViewHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout constraintLayoutParent;
        private CheckBox checkBoxEnable;
        private TextView textViewHour;
        private TextView textViewMinute;
        private TextView textViewDescribeRepeatDay;
        private ImageButton buttonAlarmType;
        AlarmViewHolder(@NonNull View itemView) {
            super(itemView);
            constraintLayoutParent = itemView.findViewById(R.id.constraint_layout_parent);
            checkBoxEnable = itemView.findViewById(R.id.check_box_enable);
            textViewHour = itemView.findViewById(R.id.text_view_hour);
            textViewMinute = itemView.findViewById(R.id.text_view_minute);
            textViewDescribeRepeatDay = itemView.findViewById(R.id.text_view_describe_repeat_day);
            buttonAlarmType = itemView.findViewById(R.id.button_alarm_type);
            constraintLayoutParent.setId(View.generateViewId());
            buttonAlarmType.setId(View.generateViewId());
        }
    }
}