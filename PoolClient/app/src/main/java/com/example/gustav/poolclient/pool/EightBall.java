package com.example.gustav.poolclient.pool;

import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class EightBall extends PoolTable{

    private int currentPlayer;
    private Player[] players = new Player[2];
    private boolean setsSet = false;
    public int MAX_SPEED = 25;

    private List<Integer> collisions = new LinkedList<>();

    public EightBall(int xWidth, int yWidth, int timeUnit) {
        super(xWidth, yWidth, -1);

        CollisionListener cl = (firstBall, secondBall) -> {
            if(firstBall == 0){
                collisions.add(secondBall);
            }else if(secondBall == 0){
                collisions.add(firstBall);
            }
        };

        addCollisionListener(cl);

        players[0] = new Player("Player 1", 1);
        players[1] = new Player("Player 2", 2);

        resetBalls();

        if (timeUnit > 0) {
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new timeUnit(), 0, timeUnit);
        }
    }

    /*
    * Shoots the cue ball and switches player turn after all balls have stopped moving
    * */
    public void shoot(double x, double y){
        double scaleFactor = 1;
        int pocketedBefore = pocketed.size();

        if(Math.pow(x,2)+Math.pow(y,2) > Math.pow(MAX_SPEED,2)){ //if combined velocity to high,
            System.out.println("Scale Factor" + scaleFactor);
            scaleFactor = MAX_SPEED/Math.sqrt(Math.pow(x,2)+Math.pow(y,2));
        }

        System.out.println("Scaled: " + x*scaleFactor + " " + y*scaleFactor);

        setCueBallSpeed(x*scaleFactor,y*scaleFactor);
        try {//Wait for the ball to stop moving
            synchronized (this) {
                wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("No balls moving, checking conditions");

        if(!setsSet && !pocketed.isEmpty()){
            setsSet = true;
            Ball b = pocketed.get(0);
            if(b.number < 7){
                players[currentPlayer].setBallSet(1);
                players[(currentPlayer+1)%2].setBallSet(9);
                System.out.println(players[currentPlayer].NAME + " took lower set");
            } else{
                players[currentPlayer].setBallSet(9);
                players[(currentPlayer+1)%2].setBallSet(1);
                System.out.println(players[currentPlayer].NAME + " took upper set");
            }
        }

        //Check for winner when all balls are still
        if(noBallsMoving()){
            checkRules(pocketedBefore);
        }
        //if(earlyEightBall()){
        //    System.out.println(players[currentPlayer] + " lost, early 8 ball!");
        //}



        //switchTurn(); //Switch turn
    }

    private void resetBalls() {
        balls.clear();

        //Add the cue ball
        balls.add(new Ball(0,0.25,15,100,yWidth/2.0));
        balls.get(0).onTable = true;

        //Add the other balls
        int ball = 1;
        int offset;
        for(int i = 0;i<5;i++){
            offset = -i;
            for (int j = 0;j <= i; j++){
                Ball b = new Ball(ball,0.15, 15,0,0);
                b.setPosition((xWidth-200)+i*20,(yWidth/2)+j*15+(offset*10)); //TODO: this formula is not perfect
                b.onTable = true;
                balls.add(b);
                ball++;
                offset++;
            }
        }
    }

    class timeUnit extends TimerTask {
        @Override
        public void run() {
            timeTick();
        }
    }

    @Override
    public void timeTick() {
        super.timeTick(); //Move balls and everything
    }

    /**
    * Method for checking the rules of the game
    * @param pocketedBefore the number of balls that was pocketed before the shot
    * */
    private void checkRules(int pocketedBefore) {
        if(pocketed.contains(balls.get(8))){ //8 ball pocketed
            if(earlyEightBall()){ //If early eight ball
                System.out.println("Early 8 ball, player" + (currentPlayer+1)%2 + " won");
                //Lose
            }else{ //If not early eight ball
                System.out.println("Got 8 ball, player" + (currentPlayer+1)%2 + " won");
                //Win
            }
        }else{//8ball not pocketed
            if(pocketedCueBall()){
                resetCueBall();
                switchTurn();
                System.out.println("Pocketed cue ball!");
            } else if(pocketedOtherPlayers(pocketedBefore) || nothingInPocked(pocketedBefore) || hitOtherPlayerFirst()){//Pocketed other players ball or hit other players FIRST or pocketed cue ball or not pocketed any ball
                System.out.println("Pocketed others first/pocketed nothing/hit other players first");
                switchTurn();
            }else{ //We pocketed one of ours
                //Continue
            }
        }

        //Clear the "collisions" array between turns
        collisions.clear();
    }

    private void resetCueBall() {
        Ball cueBall = balls.get(0);

        pocketed.remove(cueBall);
        cueBall.onTable = true;
        cueBall.setPosition(100,100); //TODO: this should not be hard coded
    }

    //TODO: seems to be something wrong, dosent know when you hit wrong
    private boolean hitOtherPlayerFirst() {
        if(collisions.size() == 0 || (players[currentPlayer].getBallSet() <= collisions.get(0) && collisions.get(0) <= players[currentPlayer].getBallSet()+6)){ //Diden't hit anything, or hit mine
            System.out.println("Hit our first!");
            return false;
        }

        System.out.println("Hit other player first!");
        return true;
    }

    private boolean nothingInPocked(int pocketedBefore) {
        System.out.println("Nothing Pocketed = " + (pocketedBefore == pocketed.size()));
        return pocketedBefore == pocketed.size();
    }

    private boolean pocketedCueBall() {
        for (Ball ball : pocketed) {
            if(ball.number == 0){
                return true;
            }
        }
        return false;
    }

    //TODO: always returns true, are we off by 1 or are we just logically wrong?
    private boolean pocketedOtherPlayers(int pocketedBefore) {
        for (int i = pocketedBefore; i < pocketed.size(); i++) {
            if((players[currentPlayer].getBallSet() == 1 && pocketed.get(i).number >= 9) || (players[currentPlayer].getBallSet() == 9 && pocketed.get(i).number <= 7)){ //Other players ball
                System.out.println("Pocketed other players!");
                return true;
            }
        }
        System.out.println("Did not pocket other players ball!");
        return false;
    }

    private void checkForWinner() {
        if(setsSet && pocketedAll(currentPlayer)){
            System.out.println(players[currentPlayer] + " won!");
        }
    }

    public void switchTurn(){
        currentPlayer = (currentPlayer+1)%2;
    }

    private boolean earlyEightBall(){
        if(!balls.get(8).onTable){//If the 8 ball is pocketed
            if(setsSet && pocketedAll(currentPlayer)){ //Then we have to have pocketed all our balls TODO: This needs to be changed since if we get 8 ball and then our last in the same shot this will not recognise it
                return false;
            }else{ //Otherwise we have lost
                return true;
            }
        }

        return false;
    }

    private boolean pocketedAll(int currentPlayer) {
        int currentBallSet = players[currentPlayer].getBallSet();

        for(int i = currentBallSet; i <= currentBallSet+6;i++){ //magic 6 is because we have 7 balls each
            if(!balls.get(i).onTable){
                return false;
            }
        }

        return true;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }
}
