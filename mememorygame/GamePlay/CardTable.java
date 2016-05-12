package com.mememorygame.snowgoat.mememorygame.GamePlay;

    import android.content.Context;
    import android.util.Log;
    import android.view.View;
    import android.widget.ImageButton;
    import android.widget.ImageView;
    import android.widget.Toast;

    import com.annimon.stream.Collector;
    import com.annimon.stream.Collectors;
    import com.annimon.stream.Stream;
    import com.mememorygame.snowgoat.mememorygame.AppUtils;
    import com.mememorygame.snowgoat.mememorygame.GamePlay.Cards.BasicCard;
    import com.mememorygame.snowgoat.mememorygame.GamePlay.Cards.ShroomzAlienCard;
    import com.mememorygame.snowgoat.mememorygame.GamePlay.Cards.ShroomzAngelCard;
    import com.mememorygame.snowgoat.mememorygame.GamePlay.Cards.ShroomzDemonCard;
    import com.mememorygame.snowgoat.mememorygame.GamePlay.Cards.ShroomzFartCard;
    import com.mememorygame.snowgoat.mememorygame.GamePlay.Cards.ShroomzGhostCard;
    import com.mememorygame.snowgoat.mememorygame.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.xml.transform.sax.SAXSource;

/**
 * Created by joona on 04/12/2015.
 */
public class CardTable {
    public int turnCount;
    private Map<Integer, BasicCard> cards;
    private GameActivity activity;
    private boolean started = false;
    private boolean demonState = false;
    private Random random = new Random();
    private int comboCount = 0;


    Combo combo = new Combo();


    public CardTable(Map<Integer, BasicCard> cards, GameActivity activity) {
        this.activity = activity;
        this.cards = cards;
        Stream.of(cards.values()).forEach(c -> c.getImageButton().setOnClickListener(v -> onClick(v.getId())));
    }


    public int getDeckSize() {
        return cards.size();

    }

    public BasicCard getRandomCard() {
        int index = random.nextInt(cards.size());
        BasicCard korddi = cards.get(index);
        System.out.println(korddi);
        return korddi;
    }

    //Is called everytime a visible imagebutton is clicked
    private void onClick(int id) {
        if (!started) {
            started = true;
            activity.onFirstClick();
        }
        BasicCard card = cards.get(id);
        //Jos kortteja on asetettu sulkeutumaan tietyn aika viiveen päästä, niin kiirehditään suoritusta ja suoritetaan tehtävä välittömästi
        Stream.of(cards.values()).forEach(c -> c.scheduledTask.flush());

        //Opening a visible card is not possible (NOTE it might have just been closed while flushing any scheduledTask:s)
        if (!card.imageSideUpp()) {
            AppUtils.vibrate(50, 50, 50);

            card.showImage();

            List<BasicCard> visibleCards = Stream.of(cards.values()).filter(c -> !c.pareFound && c.imageSideUpp()).collect(Collectors.toList());

            if (visibleCards.size() == 2) {
                //katotaan demoni
                if (Stream.of(visibleCards).anyMatch(c -> c.getClass() == ShroomzDemonCard.class) && !Stream.of(visibleCards).anyMatch(c -> c.getClass() == ShroomzAngelCard.class)) {



                    returnRandomPairBack();

                    shuffleCards();



                }

                 else if (Stream.of(visibleCards).anyMatch(c -> card.pare == c)) {
                    //kummituksen toiminta
                    if(card.getClass() == ShroomzGhostCard.class) {
                        BasicCard locationcard1 = visibleCards.get(0);
                        BasicCard locationcard2= visibleCards.get(1);


                        List < BasicCard> adjacent1 = Stream.of(cards.values()).filter(c -> locationcard1.adjacentTo(c) && !c.pareFound).collect(Collectors.toList());
                        List < BasicCard> adjacent2 = Stream.of(cards.values()).filter(c -> locationcard2.adjacentTo(c) && !c.pareFound).collect(Collectors.toList());
                        Stream.of(adjacent1).forEach(c -> activity.runOnUiThread(() -> c.showImage()));
                        Stream.of(adjacent2).forEach(c -> activity.runOnUiThread(() -> c.showImage()));
                        Stream.of(adjacent1).forEach(c->c.hideImageSideAfterDelay());
                        Stream.of(adjacent2).forEach(c->c.hideImageSideAfterDelay());

                    }
                    card.setPareFound();
                    card.getImageButton().setAlpha(0.1f);
                    card.pare.getImageButton().setAlpha(0.1f);
                    for (BasicCard iCard : cards.values()) {
                        if (!iCard.pareFound) {
                            iCard.getImageButton().setAlpha(1f);
                        }
                    }
                    if (card.getClass() == ShroomzFartCard.class) {
                        shuffleCards();


                    }
                    comboCount++;
//                    if(comboCount>1)
                    testingCombo(comboCount);

                }


                else {//shuffleCards();
                    comboCount = 0;
                    //Stream.of(visibleCards).forEach(c -> c.hideImageSideAfterDelay());
                    if (Stream.of(cards.values()).allMatch(c -> c.pareFound)){
                        System.out.println("KAIKKI PARIS FOUND 3 ");
                    }
                    for (BasicCard c : visibleCards) {
                        c.hideImageSideAfterDelay();

                    }

                }

                activity.setTurnCounter(++turnCount);
            } else if (visibleCards.size() > 2)
                Log.i("viduiks", "EI PITÄIS TAPAHTUA");

            if (Stream.of(cards.values()).allMatch(c -> c.pareFound))
                activity.onGameOver();
        }
    }


    //palauttaa kaksi korttia takaisin peliin
    public void returnRandomPairBack() {
        for(int i=0; i<cards.size(); i++) {
            if(cards.get(i).pareFound) {
                System.out.println( cards.get(i).getClass().toString());
                cards.get(i).pareFound = false;
                cards.get(i).pare.pareFound = false;

                cards.get(i).hideImage();
                cards.get(i).pare.hideImage();

                    cards.get(i).getImageButton().setAlpha(1f);
                    cards.get(i).pare.getImageButton().setAlpha(1f);



                break;
            }
        }

    }

    //ottaa korteista aina 2 korttia ja vaihtaa niiden paikkaa.
    //Ei sekota jo löydettyjä pareja eikä sekoita samaa korttia monta kertaa yhdellä metodikutsulla
    public void shuffleCards() {
        List<BasicCard> shuffler = new ArrayList<>();
        List<Integer> shufflableSlots = new ArrayList<>();
        Map<Integer, BasicCard> tempCards = new HashMap<>();
        ImageButton tempCard;

        for (int i = 0; i < cards.values().size(); i++) {

            if (!cards.get(i).pareFound) {

                shufflableSlots.add(cards.get(i).getImageButton().getId());
            } else if (cards.get(i).pareFound) {

                tempCards.put(cards.get(i).getImageButton().getId(), cards.get(i));
            } else {
                Log.i("Error", "shouldnt be null");
            }
        }

            Collections.shuffle(shufflableSlots);

            for (Integer randomSlot : shufflableSlots) {
                shuffler.add(cards.get(randomSlot));

                if (shuffler.size() == 2) {

                    tempCard = shuffler.get(0).getImageButton();
                    shuffler.get(0).setImageButton(shuffler.get(1).getImageButton());
                    shuffler.get(1).setImageButton(tempCard);
                    shuffler.get(0).pareFound=false;
                    shuffler.get(1).pareFound=false;
                    tempCards.put(shuffler.get(0).getImageButton().getId(), shuffler.get(0));
                    tempCards.put(shuffler.get(1).getImageButton().getId(), shuffler.get(1));
                    shuffler.clear();
                }
            }
        for(BasicCard card : tempCards.values()){
            if(card.image != GameActivity.backImage && !card.pareFound) {

                card.hideImageSideAfterDelay();
            }

        }
            updateTable(tempCards);
        Toast.makeText(activity, "Cards have been shuffled", Toast.LENGTH_SHORT).show();
        }

        // päivittää kortit, käytännössä setteri vaan
        public void updateTable (Map < Integer, BasicCard > cards2){

            cards = cards2;
        }


    public void testingCombo ( int comboCount) {
        switch (comboCount) {
            case 1:
                break;
            case 2:
                combo.combo2();
                break;
            case 3:
                combo.combo3();
                break;
            case 4:
                combo.combo4();
                break;
            case 5:
                combo.combo5();
                break;
        }
    }
        }