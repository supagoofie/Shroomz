package com.mememorygame.snowgoat.mememorygame.GamePlay.Cards;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.widget.ImageButton;

import com.mememorygame.snowgoat.mememorygame.AppUtils;
import com.mememorygame.snowgoat.mememorygame.GamePlay.GameActivity;
import com.mememorygame.snowgoat.mememorygame.GamePlay.SingleRunScheduledTask;
import com.mememorygame.snowgoat.mememorygame.R;

import java.util.Collection;
import java.util.Observable;

import javax.xml.transform.sax.SAXSource;

/**
 * Created by WinNabuska on 11.11.2015.
 */
public class BasicCard extends Observable {

    protected ImageButton imageButton;
    public volatile SingleRunScheduledTask scheduledTask;
    public BasicCard pare;
    public boolean pareFound;
    public Drawable image;
    public Collection<BasicCard> basicCards;
    public int x;
    public int y;
    Activity activity;
    public boolean shufflable = true;
    private boolean demonState= false;

    public BasicCard(Collection<BasicCard> cards, Activity activity/* Handler cardFlipHandler*/) {
        //this.cardFlipHandler = cardFlipHandler;
        this.activity = activity;
        //this.animation = AnimationUtils.loadAnimation(activity, android.R.anim.slide_out_right);
        this.basicCards = cards;
        scheduledTask = new SingleRunScheduledTask(() -> {}, 0, activity); //empty task with zero delay
    }

    public ImageButton getImageButton() {
        return imageButton;
    }

    public void setImageButton(ImageButton imageButton) {
        this.imageButton = imageButton;
    }

    public void changeImageButtonID(int id) {
        imageButton.setId(id);
    }

    public boolean imageSideUpp() {

        return imageButton.getDrawable() == image;
    }

    public boolean getDemonState () {
        return demonState;
    }

    public void setDemonState(boolean state) {
        demonState = state;
    }

    public void hideImage() {
        imageButton.setImageDrawable(GameActivity.backImage);
    }

    public void showImage() {
        imageButton.setImageDrawable(image);
        System.out.println(getClass().toString());
        System.out.println(getImageButton().getId());
        //imageButton.startAnimation(animation);
        //pare.imageButton.startAnimation(animation);
    }

    public void setPareFound() {
        AppUtils.vibrate(100, 100, 100, 50);
        shufflable = false;
        pare.shufflable=false;
        pareFound=true;
        pare.pareFound=true;
        Runnable task = () -> {
            imageButton.setAlpha(0.1f);
            pare.imageButton.setAlpha(0.1f);

        };
        scheduledTask = new SingleRunScheduledTask(task, 900,activity);
    }

    public void hideImageSideAfterDelay(){
        Runnable task = () -> {
            imageButton.setImageDrawable(GameActivity.backImage);
        };
        scheduledTask = new SingleRunScheduledTask(task, 900, activity);
    }
    public boolean adjacentTo(BasicCard other) {
        if(imageButton.getId()-other.getImageButton().getId()==1 || imageButton.getId()-other.getImageButton().getId()==-1 ) {
            if(imageButton.getId()%6 ==0 && imageButton.getId()-other.getImageButton().getId()==-1) {
                return false;
            }
            if(imageButton.getId()%6 ==1 && imageButton.getId()-other.getImageButton().getId()==1) {
                return false;
            }
            return true;
        }
        else if(imageButton.getId()-6 == other.getImageButton().getId() ||imageButton.getId()+6 == other.getImageButton().getId()) {
            return true;
        }

        else {return false;}
        /*int xDif = Math.abs(imageButton.getId() - other.x);
        int yDif = Math.abs(y - other.y);
        boolean adjacent = xDif == 1 && yDif == 0 || yDif == 1 && xDif == 0;
        return adjacent;*/
    }

}

