package com.example.qrrush.view;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.qrrush.R;

public class SettingsFragment extends DialogFragment {

    View view;

    Button playButton, pauseButton;

    MediaPlayer mediaPlayer;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        view = inflater.inflate(R.layout.fragment_settings, container, false);

        playButton = view.findViewById(R.id.play_button);

        pauseButton = view.findViewById(R.id.pause_button);

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

        return view;
    }
}
