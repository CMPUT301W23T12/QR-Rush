package com.example.qrrush.view;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.qrrush.R;
import com.example.qrrush.model.FirebaseWrapper;
import com.example.qrrush.model.User;
import com.example.qrrush.model.UserUtil;

import java.util.Map;
import java.util.Optional;

public class SettingsFragment extends DialogFragment {


    View view;

    ImageButton playButton, pauseButton;

    MediaPlayer mediaPlayer;

    SeekBar volumeBar;

    AudioManager audioManager;


    public SettingsFragment (MediaPlayer mediaPlayer){
        this.mediaPlayer = mediaPlayer;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        view = inflater.inflate(R.layout.fragment_settings, container, false);

        playButton = view.findViewById(R.id.play_button);

        pauseButton = view.findViewById(R.id.pause_button);

        volumeBar = view.findViewById(R.id.volume_seekBar);

        audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);


        mediaPlayer = MediaPlayer.create(getActivity().getApplicationContext(), R.raw.backgroundmusic);

        mediaPlayer.start();
        mediaPlayer.setLooping(true);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.setLooping(true);
                mediaPlayer.start();
                }
            });


        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.pause();
            }
        });

        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        int curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        volumeBar.setMax(maxVolume);
        volumeBar.setProgress(curVolume);

        volumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, i, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return view;
    }
}
