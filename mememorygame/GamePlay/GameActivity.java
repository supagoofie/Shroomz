package com.mememorygame.snowgoat.mememorygame.GamePlay;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.annimon.stream.function.Function;
import com.mememorygame.snowgoat.mememorygame.AppUtils;
import com.mememorygame.snowgoat.mememorygame.GamePlay.Cards.ShroomzAlienCard;
import com.mememorygame.snowgoat.mememorygame.GamePlay.Cards.BasicCard;
import com.mememorygame.snowgoat.mememorygame.GamePlay.Cards.ShroomzAngelCard;
import com.mememorygame.snowgoat.mememorygame.GamePlay.Cards.ShroomzBlushCard;
import com.mememorygame.snowgoat.mememorygame.GamePlay.Cards.ShroomzCryingCard;
import com.mememorygame.snowgoat.mememorygame.GamePlay.Cards.ShroomzCupcakeCard;
import com.mememorygame.snowgoat.mememorygame.GamePlay.Cards.ShroomzCyborgCard;
import com.mememorygame.snowgoat.mememorygame.GamePlay.Cards.ShroomzDemonCard;
import com.mememorygame.snowgoat.mememorygame.GamePlay.Cards.ShroomzFartCard;
import com.mememorygame.snowgoat.mememorygame.GamePlay.Cards.ShroomzFlamingCard;
import com.mememorygame.snowgoat.mememorygame.GamePlay.Cards.ShroomzFrozenCard;
import com.mememorygame.snowgoat.mememorygame.GamePlay.Cards.ShroomzGhostCard;
import com.mememorygame.snowgoat.mememorygame.GamePlay.Cards.ShroomzGrumpyCard;
import com.mememorygame.snowgoat.mememorygame.GamePlay.Cards.ShroomzHairyCard;
import com.mememorygame.snowgoat.mememorygame.GamePlay.Cards.ShroomzHappyCard;
import com.mememorygame.snowgoat.mememorygame.GamePlay.Cards.ShroomzLobotomyCard;
import com.mememorygame.snowgoat.mememorygame.GamePlay.Cards.ShroomzPensiveCard;

import com.mememorygame.snowgoat.mememorygame.GamePlay.Cards.ShroomzPepeCard;
import com.mememorygame.snowgoat.mememorygame.GamePlay.Cards.ShroomzPsyCard;
import com.mememorygame.snowgoat.mememorygame.GamePlay.Cards.ShroomzPukeCard;
import com.mememorygame.snowgoat.mememorygame.GamePlay.Cards.ShroomzRageCard;
import com.mememorygame.snowgoat.mememorygame.GamePlay.Cards.ShroomzSickCard;
import com.mememorygame.snowgoat.mememorygame.GamePlay.Cards.ShroomzSleepyCard;

import com.mememorygame.snowgoat.mememorygame.R;
import com.mememorygame.snowgoat.mememorygame.TopList.TopListActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;



public class GameActivity extends Activity {

    /*TODO miikan tekemät muutokset ja uudet korttiluokat täytyy integroida*/

    private String LOG = "GameActivity";

    public static Play play;
    private PlayDurationEvaluator durationEvaluator;
    private CardTable cardTable;
    private Combo combo = new Combo();

    private List<ImageView> timeImageViews;
    private List<ImageView> turnCountImageViews;
    private List<Drawable> numberImages;
//TODO 6 & 5 - toimii , 7 & 6 pitäisi saadaa
    public static Drawable backImage;
    private int ROWS = 7, COLUMNS = 6;
    private ImageView minuteSecondSeparator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        AppUtils.startMediaPlayer(1);
        combo.setBoonus(0);
        durationEvaluator = new PlayDurationEvaluator();
        //MediaPlayer.create(this, R.raw.theme).start();
        backImage = getResources().getDrawable(R.drawable.shroomz_deckicon);

        initDigitImageViews();

        TableLayout tableLayout = (TableLayout) findViewById(R.id.table_layout);
        List<ImageButton> imageButtons = loadTablesViews(tableLayout);
        List<Drawable> images = getCardImages();
        Map<Integer, BasicCard> cards = createCards(images, imageButtons);
        cardTable = new CardTable(cards, this);
    }

    //Alustetaan säije päivittämään ajan näyttö sekunnin välein
    protected void onFirstClick(){
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                List<Integer> digits = durationEvaluator.elapsedTimeToDigits();
                List<ImageView> relevantViews = timeImageViews.subList(0, digits.size());
                Runnable digitUpdate = () -> {
                    if(digits.size()>2 && minuteSecondSeparator.getVisibility()== View.INVISIBLE)
                        minuteSecondSeparator.setVisibility(View.VISIBLE);
                    for(int i = 0; i<digits.size(); i++){
                        ImageView imgView = relevantViews.get(i);
                        if(imgView.getVisibility()==View.INVISIBLE)
                            imgView.setVisibility(View.VISIBLE);
                        imgView.setImageDrawable(numberImages.get(digits.get(i)));
                    }
                };
                GameActivity.this.runOnUiThread(digitUpdate);
            }
        };
        durationEvaluator.onPlayStart();
        timer.scheduleAtFixedRate(timerTask, 0, 1000);
    }


    public void onGameOver(){
        durationEvaluator.onGameOver();

        Log.i("info", "game over");
        play = new Play(cardTable.turnCount,durationEvaluator.getPlayDuration(), 0 /*TODO implement bonus points*/);
        play.setCombo(combo.getBoonus()); //älkää kommentoiko ulos, tämä mahdollistaa bonuspisteiden näkymisen Highscore-taulussa

        new AlertDialog.Builder(this).setTitle("Scores")
                .setMessage("Time: " + (play.gameDuration/1000) +" sec\nTurns: " +cardTable.turnCount+"\nPoints: "+play.getScore()+"\nBonus: "+combo.getBoonus())
                .setCancelable(false)
                .setIcon(R.drawable.shroomz_card_pepe)
                .setPositiveButton("Continue", (d, id) -> {
                    d.dismiss();
                    Intent topListIntent = new Intent(this, TopListActivity.class);
                    topListIntent.putExtra(TopListActivity.PLAY_INTENT_EXTRA, play);
                    startActivity(topListIntent);
                    finish();
                })
                .create().show();
    }

    //kutsuttava CardTable:sta joka kerta kun kaksi korttia on käännetty
    public void setTurnCounter(int count){
        String strCount = String.valueOf(count);
        for(int i = 0; i<3 && i<strCount.length(); i++){
            ImageView imgV = turnCountImageViews.get(i);
            String digitChar = strCount.charAt(i)+"";
            Drawable digitImage = numberImages.get(Integer.valueOf(digitChar));
            if(imgV.getVisibility()==View.INVISIBLE)
                imgV.setVisibility(View.VISIBLE);
            turnCountImageViews.get(i).setImageDrawable(digitImage);
        }
    }

    @Override
    protected void onResume() {
        durationEvaluator.onResume();
        super.onResume();
        AppUtils.startMediaPlayer(1);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppUtils.pauseMediaPlayer();
        durationEvaluator.onPause();
    }

    private List<ImageButton> loadTablesViews(TableLayout tableLayout) {
        List<ImageButton> imageButtons = new ArrayList<>();

        int displayWidthPixels = getResources().getDisplayMetrics().widthPixels;

        int maxTilt = 15;

        int width = displayWidthPixels/COLUMNS-(displayWidthPixels/(COLUMNS*20));


        int height = (int)Math.round(width * 1.2);//(int)Math.round(Math.sqrt(Math.pow(width,2)+Math.pow(catetY,2)))+1;
        //tableLayout.setClipChildren(false);
        //tableLayout.setClipToPadding(false);

        Random random = new Random();
        for (int y = 0; y < ROWS; y++) {
            TableRow tableRow = new TableRow(this);
            TableRow.LayoutParams tableRowLayoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
            tableRow.setLayoutParams(tableRowLayoutParams);
            tableRow.setHorizontalGravity(Gravity.CENTER);
            //tableRow.setClipChildren(false);
            //tableRow.setClipToPadding(false);

            for (int x = 0; x < COLUMNS; x++) {
                ImageButton imageButton = new ImageButton(this, null, R.style.img_btn_5x6_style);
                TableRow.LayoutParams imgBtnLayoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
                imageButton.setLayoutParams(imgBtnLayoutParams);
                //((ViewGroup.MarginLayoutParams) imageButton.getLayoutParams()).setMargins(0, 0, 0, 0);
                imgBtnLayoutParams.width = width;
                imgBtnLayoutParams.height = height;
                imageButton.setImageDrawable(backImage);
                imageButton.setRotation(random.nextInt(maxTilt * 2) - maxTilt);
                //imageButton.setAnimation(animation);
                //scale(imageButton);
                imageButton.setBackgroundColor(Color.TRANSPARENT);
                tableRow.addView(imageButton);
                int id = (y) * COLUMNS + x;
                imageButton.setId(id);
                imageButtons.add(imageButton);
            }
            tableLayout.addView(tableRow);
        }
        return imageButtons;
    }

    private void initDigitImageViews() {
        loadDigitImages();
        timeImageViews = new ArrayList<>();
        timeImageViews.add((ImageView) findViewById(R.id.time_imageview_singleseconds));
        timeImageViews.add((ImageView) findViewById(R.id.time_imageview_tensofseconds));
        timeImageViews.add((ImageView) findViewById(R.id.time_imageview_singleminutes));
        timeImageViews.add((ImageView) findViewById(R.id.time_imageview_tensofminutes));

        minuteSecondSeparator = (ImageView) findViewById(R.id.time_imageview_secondminute_separator);

        ImageView turnCount1 = (ImageView) findViewById(R.id.turncount_imageview1);
        ImageView turnCount2 = (ImageView) findViewById(R.id.turncount_imageview2);
        ImageView turnCount3 = (ImageView) findViewById(R.id.turncount_imageview3);

        turnCountImageViews = Arrays.asList(turnCount1, turnCount2, turnCount3);
    }

    private void loadDigitImages(){
        numberImages = new ArrayList<>();
        numberImages.add(getResources().getDrawable(R.drawable.shroomz_number_nolla));
        numberImages.add(getResources().getDrawable(R.drawable.shroomz_number_yksi));
        numberImages.add(getResources().getDrawable(R.drawable.shroomz_number_kaksi));
        numberImages.add(getResources().getDrawable(R.drawable.shroomz_number_kolme));
        numberImages.add(getResources().getDrawable(R.drawable.shroomz_number_nelja));
        numberImages.add(getResources().getDrawable(R.drawable.shroomz_number_viisi));
        numberImages.add(getResources().getDrawable(R.drawable.shroomz_number_kuusi));
        numberImages.add(getResources().getDrawable(R.drawable.shroomz_number_seiska));
        numberImages.add(getResources().getDrawable(R.drawable.shroomz_number_kasi));
        numberImages.add(getResources().getDrawable(R.drawable.shroomz_number_ysi));
    }

    private List<Drawable> getCardImages() {
        List<Drawable> images = new ArrayList<>();
        images.add(getResources().getDrawable(R.drawable.shroomz_card_alien));  //0
        images.add(getResources().getDrawable(R.drawable.shroomz_card_sleepy));   //1
        images.add(getResources().getDrawable(R.drawable.shroomz_card_blush));   //2
        images.add(getResources().getDrawable(R.drawable.shroomz_card_crying));  //3
        images.add(getResources().getDrawable(R.drawable.shroomz_card_cupcake));   //4
        images.add(getResources().getDrawable(R.drawable.shroomz_card_sick));   //5;
        images.add(getResources().getDrawable(R.drawable.shroomz_card_fart));  //6
        images.add(getResources().getDrawable(R.drawable.shroomz_card_flaming));   //7
        images.add(getResources().getDrawable(R.drawable.shroomz_card_frozen));    //8
        images.add(getResources().getDrawable(R.drawable.shroomz_card_ghost));    //9
        images.add(getResources().getDrawable(R.drawable.shroomz_card_grumpy));    //10
        images.add(getResources().getDrawable(R.drawable.shroomz_card_hairy));    //11
        images.add(getResources().getDrawable(R.drawable.shroomz_card_happy));    //12
        images.add(getResources().getDrawable(R.drawable.shroomz_card_lobotomy));   //13
        images.add(getResources().getDrawable(R.drawable.shroomz_card_pensive));   //14
        //korttien lisäys
        images.add(getResources().getDrawable(R.drawable.shroomz_card_pepe)); //15
        images.add(getResources().getDrawable(R.drawable.shroomz_card_psy)); //16
        images.add(getResources().getDrawable(R.drawable.shroomz_card_puke)); // 17
        images.add(getResources().getDrawable(R.drawable.shroomz_card_rage)); // 18
        images.add(getResources().getDrawable(R.drawable.shroomz_card_cyborg)); // 19
        images.add(getResources().getDrawable(R.drawable.shroomz_card_demon)); // 20
        images.add(getResources().getDrawable(R.drawable.shroomz_card_angel)); // 21

        return images;
    }

    private Map<Integer, BasicCard> createCards(List<Drawable> images, List<ImageButton> imageButtons){
        List<ImageButton> unAssignedImageButtons = new ArrayList<>(imageButtons);

        Map<Integer, BasicCard> cards = new HashMap<>();
        int imageNumber = 0;

        Function<ImageButton, Integer> getRow = iBtn -> iBtn.getId()/COLUMNS;
        Function<ImageButton, Integer> getColumn = iBtn -> iBtn.getId()%COLUMNS;

        BasicCard[] pare = new BasicCard[2];
        BasicCard card;

        pare[0] = new ShroomzAngelCard(cards.values(), this);
        pare[1] = new ShroomzDemonCard(cards.values(), this);
        ImageButton imgButton = unAssignedImageButtons.remove((int) (Math.random() * unAssignedImageButtons.size()));
        pare[0].x = getColumn.apply(imgButton);
        pare[0].y = getRow.apply(imgButton);
        pare[0].setImageButton(imgButton);

        cards.put(imgButton.getId(), pare[0]);
        imgButton = unAssignedImageButtons.remove((int) (Math.random() * unAssignedImageButtons.size()));
        pare[1].x = getColumn.apply(imgButton);
        pare[1].y = getRow.apply(imgButton);
        pare[1].setImageButton(imgButton);
        cards.put(imgButton.getId(), pare[1]);

        pare[0].pare = pare[1];
        pare[1].pare = pare[0];

        pare[0].image = images.get(21);
        pare[1].image = images.get(20);

        while(unAssignedImageButtons.size()>0){

            for(int i = 0; i<2; i++){

                switch (imageNumber){
                    case ShroomzGhostCard.IMAGE_NUMBER:
                        card = new ShroomzGhostCard(cards.values(), this);
                        break;
                    case ShroomzAlienCard.IMAGE_NUMBER:
                        card = new ShroomzAlienCard(cards.values(), this);
                        break;

                    case ShroomzBlushCard.IMAGE_NUMBER:
                        card = new ShroomzBlushCard(cards.values(), this);
                        break;
                    case ShroomzCryingCard.IMAGE_NUMBER:
                        card = new ShroomzCryingCard(cards.values(), this);
                        break;
                    case ShroomzCupcakeCard.IMAGE_NUMBER:
                        card = new ShroomzCupcakeCard(cards.values(), this);
                        break;

                    case ShroomzFartCard.IMAGE_NUMBER:
                        card = new ShroomzFartCard(cards.values(), this);
                        break;
                    case ShroomzFlamingCard.IMAGE_NUMBER:
                        card = new ShroomzFlamingCard(cards.values(), this);
                        break;
                    case ShroomzFrozenCard.IMAGE_NUMBER:
                        card = new ShroomzFrozenCard(cards.values(), this);
                        break;
                    case ShroomzGrumpyCard.IMAGE_NUMBER:
                        card = new ShroomzGrumpyCard(cards.values(), this);
                        break;
                    case ShroomzHairyCard.IMAGE_NUMBER:
                        card = new ShroomzHairyCard(cards.values(), this);
                        break;
                    case ShroomzHappyCard.IMAGE_NUMBER:
                        card = new ShroomzHappyCard(cards.values(), this);
                        break;
                    case ShroomzLobotomyCard.IMAGE_NUMBER:
                        card = new ShroomzLobotomyCard(cards.values(), this);
                        break;
                    case ShroomzPepeCard.IMAGE_NUMBER:
                        card = new ShroomzPepeCard(cards.values(), this);
                        break;
                    case ShroomzPsyCard.IMAGE_NUMBER:
                        card = new ShroomzPsyCard(cards.values(), this);
                        break;
                    case ShroomzPukeCard.IMAGE_NUMBER:
                        card = new ShroomzPukeCard(cards.values(), this);
                        break;
                    case ShroomzRageCard.IMAGE_NUMBER:
                        card = new ShroomzRageCard(cards.values(), this);
                        break;
                    case ShroomzSickCard.IMAGE_NUMBER:
                        card = new ShroomzSickCard(cards.values(), this);
                        break;
                    case ShroomzSleepyCard.IMAGE_NUMBER:
                        card = new ShroomzSleepyCard(cards.values(), this);
                        break;
                    case ShroomzPensiveCard.IMAGE_NUMBER:
                        card = new ShroomzPensiveCard(cards.values(), this);
                        break;
                    case ShroomzCyborgCard.IMAGE_NUMBER:
                        card = new ShroomzCyborgCard(cards.values(), this);
                        break;
                    default:
                        card = new BasicCard(cards.values(), this);
                }
                pare[i] = card;

                imgButton = unAssignedImageButtons.remove((int) (Math.random() * unAssignedImageButtons.size()));
                card.x = getColumn.apply(imgButton);
                card.y = getRow.apply(imgButton);
                card.setImageButton(imgButton);

                cards.put(imgButton.getId(), card);

                if(i>0){
                    pare[0].pare = pare[1];
                    pare[1].pare = pare[0];
                }
                card.image = images.get(imageNumber%images.size());
            }
            imageNumber++;
        }
        return cards;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}