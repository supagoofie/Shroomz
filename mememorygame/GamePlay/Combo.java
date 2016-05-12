package com.mememorygame.snowgoat.mememorygame.GamePlay;

/**
 * Created by Riku on 17.12.2015.
 */


import com.mememorygame.snowgoat.mememorygame.GamePlay.Cards.BasicCard;


public class Combo {
    //    final static int COMBO_2 = 2;
//    final static int COMBO_3 = 3;
//    final static int COMBO_4 = 4;
//    final static int COMBO_5 = 5;
    BasicCard basicCard;
    String test;
    static int boonus;

    public String makeString() {
        return ("Bonus = " + boonus);
    }

    public static void setBoonus(int b) {
        boonus = b;
    }

    public Combo(){
    }
    public void combo2() {
        boonus50();

    }

//
//        AppUtils.vibrate(100, 100, 100, 50);
//        basicCard.pareFound=true;
//        basicCard.pareFound=true;
//
//        Set<BasicCard> adjacent = Stream.of(basicCard.basicCards).filter(c -> this.adjacentTo(c) && !c.pareFound).collect(Collectors.toSet());
//        Stream.of(adjacent).forEach(c -> basicCard.activity.runOnUiThread(() -> c.showImage()));
//        Runnable task = () -> {
//            Stream.of(adjacent).forEach(c -> c.hideImage());
//        };
//        basicCard.scheduledTask = new SingleRunScheduledTask(task, 900, basicCard.activity);


    //Katsoo onko tarkasteltava kortti kohtisuoraa kyseisen oikealla, vasenmalla, yläpuolella tai alapuolella



    public void combo3(){

boonus100();
    }

    public void combo4(){

boonus300();
    }

    public void combo5(){

boonus500();
    }

    private boolean adjacentTo(BasicCard other) {
        int xDif = Math.abs(basicCard.x - other.x);
        int yDif = Math.abs(basicCard.y - other.y);
        boolean adjacent = xDif == 1 && yDif == 0 || yDif == 1 && xDif == 0;
        return adjacent;
    }

    public int getBoonus() {
        return boonus;
    }

    public void boonus50(){
        boonus = boonus+50;
    }
    public void boonus100(){
        boonus = boonus+100;
    }
    public void boonus300(){
        boonus = boonus+300;
    }
    public void boonus500(){
        boonus = boonus+500;
    }

//    public void testingCombo(int comboCount){
//        switch (comboCount){
//            case 1:
//                break;
//            case 2:
//                combo2();
//                break;
//            case 3:
//                combo3();
//                break;
//            case 4:
//            combo4();
//                break;
//            case 5:
//                combo5();
//                break;
//        }
//        if (comboCount == 2) {
//            combo2();
//
//        }
//         if (comboCount == 3) {
//            combo3();
//
//        }
//         if (comboCount == 4) {
//            combo4();
//
//        }
//         if (comboCount == 5) {
//            combo5();
//
//        }

}

