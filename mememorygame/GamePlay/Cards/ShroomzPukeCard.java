package com.mememorygame.snowgoat.mememorygame.GamePlay.Cards;

import android.app.Activity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.mememorygame.snowgoat.mememorygame.AppUtils;
import com.mememorygame.snowgoat.mememorygame.R;

import java.util.Collection;

/**
 * Created by M.P on 27.1.2016.
 */


    public class ShroomzPukeCard extends BasicCard {


        public static final int IMAGE_NUMBER = 17;
        private Animation pareFoundAnim;

        public ShroomzPukeCard(Collection<BasicCard> cards, Activity activity) {
            super(cards, activity);
            pareFoundAnim = AnimationUtils.loadAnimation(activity, R.anim.pyor_oik);
        }

        @Override
        public void setPareFound() {
            AppUtils.vibrate(100, 100, 100, 50);
            pareFound=true;
            pare.pareFound=true;
            imageButton.startAnimation(pareFoundAnim);
            pare.imageButton.startAnimation(pareFoundAnim);
        /*Runnable task = () -> {
            //TODO tänne jotain ekstra juttuja ehkä
        };
        scheduledTask = new SingleRunScheduledTask(task, 900, activity);*/
        }



}
