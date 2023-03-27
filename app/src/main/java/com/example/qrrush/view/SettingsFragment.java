package com.example.qrrush.view;

import static androidx.core.content.ContextCompat.getSystemService;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.qrrush.R;

import java.util.Set;

public class SettingsFragment extends DialogFragment {

    MainActivity(mediaPlayer);

    View view;

    ImageButton playButton, pauseButton;

    MediaPlayer mediaPlayer;

    SeekBar volumeBar;

    AudioManager audioManager;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        view = inflater.inflate(R.layout.fragment_settings, container, false);

        playButton = view.findViewById(R.id.play_button);

        pauseButton = view.findViewById(R.id.pause_button);

        volumeBar = view.findViewById(R.id.volume_seekBar);

        audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);


        //mediaPlayer = MediaPlayer.create(getActivity().getApplicationContext(), R.raw.backgroundmusic);

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

        return view;
    }
}
