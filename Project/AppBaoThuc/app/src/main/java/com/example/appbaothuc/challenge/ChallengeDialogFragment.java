package com.example.appbaothuc.challenge;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appbaothuc.Alarm;
import com.example.appbaothuc.MainActivity;
import com.example.appbaothuc.R;
import com.example.appbaothuc.interfaces.ChallengeActivityListener;
import com.example.appbaothuc.interfaces.ChallengeDialogListener;

import java.io.File;

import static android.content.Context.AUDIO_SERVICE;


// TODO: WARNING: This class has some high logical handles
public class ChallengeDialogFragment extends DialogFragment implements ChallengeActivityListener {
    private boolean debugMode = true; // TODO: remove this when release
    private Alarm alarm;
    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;
    private int currentSystemVolume;
    private int fixedVolume;

    private ChallengeActivity.ChallengeType challengeType = ChallengeActivity.ChallengeType.SHAKE; // TODO: Hard-coded
    private boolean graduallyIncreaseVolume = true; //TODO: Hard-coded
    private boolean maxVolume = false; //TODO: Hard-coded
    private int muteTime = 30; //TODO: Hard-coded

    private TextView textViewLabel;
    private TextView textViewRingtoneName;
    private TextView textViewHour;
    private TextView textViewMinute;
    private ImageButton buttonMute;
    private Button buttonOk;
    private Button buttonCancel;

    private FragmentManager fragmentManager;
    private MathChallengeFragment mathChallengeFragment;
    private ShakeChallengeFragment shakeChallengeFragment;
    private ChallengeDialogListener challengeDialogListener;

    // flags for communication with background threads
    private boolean isDismissed = false;
    private boolean isMuting = false;
    private boolean snoozeAgain = false;
    private Thread threadSnooze;

    public ChallengeDialogFragment() {
    }

    public static ChallengeDialogFragment newInstance(Alarm alarm, String title) {
        ChallengeDialogFragment challengeDialogFragment = new ChallengeDialogFragment();
        challengeDialogFragment.setAlarm(alarm);
        challengeDialogFragment.setCancelable(false);
        return challengeDialogFragment;
    }

    private void setAlarm(Alarm alarm) {
        this.alarm = alarm;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundleChallenge = getArguments();
        View view = inflater.inflate(R.layout.fragment_challenge_dialog, container, false);
        mediaPlayer = new MediaPlayer();
        audioManager = (AudioManager) getContext().getSystemService(AUDIO_SERVICE);
        textViewLabel = view.findViewById(R.id.textView_label);
        textViewRingtoneName = view.findViewById(R.id.textView_ringtoneName);
        textViewHour = view.findViewById(R.id.text_view_hour);
        textViewMinute = view.findViewById(R.id.text_view_minute);
        buttonOk = view.findViewById(R.id.button_ok);
        buttonCancel = view.findViewById(R.id.button_cancel);
        buttonMute = view.findViewById(R.id.button_mute);

        textViewLabel.setText(alarm.getLabel());
        textViewHour.setText(String.valueOf(alarm.getHour()));
        if (alarm.getMinute() < 10) {
            textViewMinute.setText("0" + alarm.getMinute());
        } else {
            textViewMinute.setText(String.valueOf(alarm.getMinute()));
        }
        buttonMute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Mute for " + muteTime + " seconds.", Toast.LENGTH_LONG).show();
                SnoozeManager snoozeManager = new SnoozeManager(mediaPlayer, muteTime);
                if (threadSnooze == null || !threadSnooze.isAlive()) {
                    threadSnooze = new Thread(snoozeManager);
                    threadSnooze.start();
                } else {
                    snoozeAgain = true;
                }
            }
        });
        if (debugMode) {
            buttonCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isDismissed = true;
                    mediaPlayer.release();
                    getDialog().dismiss();
                    getActivity().finish();
                    audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentSystemVolume, 0);
                    MainActivity.restartAlarmService(getContext());
                }
            });
        }

        if (maxVolume) {
            currentSystemVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
            fixedVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        } else {

        }


        File file = new File(alarm.getRingtoneUrl());
        if (!file.exists()){
            Resources resources = getResources();
            int resId = R.raw.in_the_busting_square;
            alarm.setRingtoneUrl(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + resources.getResourcePackageName(resId) + '/' + resources.getResourceTypeName(resId) + '/' + resources.getResourceEntryName(resId));
        }
        textViewRingtoneName.setText("Music: " + alarm.getRingtoneName());
        if(bundleChallenge == null){
            mediaPlayer = MediaPlayer.create(getContext(), Uri.parse(alarm.getRingtoneUrl()));

            mediaPlayer.setLooping(true);
            if (!graduallyIncreaseVolume) {
                mediaPlayer.start();
            } else {
                mediaPlayer.setVolume(0, 0);
                mediaPlayer.start();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (float i = 1; i <= 1000; i++) {
                            try {
                                if (isDismissed) {
                                    return;
                                }
                                if (isMuting) {
                                    return;
                                }
                                mediaPlayer.setVolume(i / 1000, i / 1000);
                                Thread.sleep(10);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (IllegalStateException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            }
        }


        fragmentManager = getChildFragmentManager();
        switch (challengeType) {
            case MATH:
                mathChallengeFragment = new MathChallengeFragment();
                challengeDialogListener.onChallengeActivated(mathChallengeFragment);
                mathChallengeFragment.setArguments(bundleChallenge);
                fragmentManager.beginTransaction().add(R.id.challenge_fragment_container, mathChallengeFragment).commit();
                break;
            case SHAKE:
                shakeChallengeFragment = new ShakeChallengeFragment();
                challengeDialogListener.onChallengeActivated(shakeChallengeFragment);
                shakeChallengeFragment.setArguments(bundleChallenge);
                fragmentManager.beginTransaction().add(R.id.challenge_fragment_container, shakeChallengeFragment).commit();
                break;
            default:
                break;
        }




//        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
//            @Override
//            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//                if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_MUTE) {
//                    return true;
//                }
//                return false;
//            }
//        });
        return view;
    }

    private class SnoozeManager implements Runnable {
        private MediaPlayer mediaPlayer;
        private int snoozeTime;
        public SnoozeManager(MediaPlayer mediaPlayer, int snoozeTime) {
            this.mediaPlayer = mediaPlayer;
            this.snoozeTime = snoozeTime;
        }
        public void run() {
            isMuting = true;
            mediaPlayer.setVolume(0, 0);
            try {
                Thread.sleep(this.snoozeTime * 1000);
                isMuting = false;
                for (float i = 1; i <= 1000; i++) {
                    if (isDismissed) {
                        return;
                    }
                    if (snoozeAgain) {
                        snoozeAgain = false;
                        run();
                        return;
                    }
                    mediaPlayer.setVolume(i / 1000, i / 1000);
                    Thread.sleep(10);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onFinishChallenge() {
        isDismissed = true;
        //mediaPlayer.release();
        audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentSystemVolume, 0);
        MainActivity.restartAlarmService(getContext());
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.challengeDialogListener = (ChallengeDialogListener) context;
    }
    @Override
    public Bundle onGetSavedState() {
        return null;
    }
}