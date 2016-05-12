package com.mememorygame.snowgoat.mememorygame;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class HowToPlayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to_play);
        AppUtils.startMediaPlayer(2);
    }

    public void onReturnButtonClick(View view) {
        AppUtils.vibrate(100, 50, 100);
        if(MainMenuActivity.bSound) {
            MainMenuActivity.mp = MediaPlayer.create(getApplicationContext(), R.raw.button_sound);
            MainMenuActivity.mp.start();
        }
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppUtils.startMediaPlayer(2);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppUtils.pauseMediaPlayer();
    }
}
