package com.mememorygame.snowgoat.mememorygame.GamePlay.Cards;

import android.app.Activity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.mememorygame.snowgoat.mememorygame.AppUtils;
import com.mememorygame.snowgoat.mememorygame.R;

import java.util.Collection;

/**
 * Created by lammi on 22.12.2015.
 */
public class ShroomzAngelCard extends BasicCard {
    private Animation pareFoundAnim;
    public static final int IMAGE_NUMBER = 20;
    private boolean demonState = false;

    public ShroomzAngelCard(Collection<BasicCard> cards, Activity activity) {
        super(cards, activity);
        pareFoundAnim = AnimationUtils.loadAnimation(activity, R.anim.kaatuu_taakse);
    }

    @Override
    public void setPareFound() {

        AppUtils.vibrate(100, 100, 100, 50);
        pareFound=true;
        pare.pareFound=true;
        imageButton.startAnimation(pareFoundAnim);
        pare.imageButton.startAnimation(pareFoundAnim);

    }

    @Override
    public void showImage(){
        setDemonState(false);
        System.out.println(getClass().toString());
        imageButton.setImageDrawable(image);


    }




    }





