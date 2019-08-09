package com.example.gustav.poolclient.pool;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/*
 * Physics implementation of a pool table with pockets
 * */
public class PoolTable extends Table {
    PoolPocket[] pockets;
    List<Ball> pocketed = new ArrayList<>();
    protected boolean cuePocketed = false;

    public PoolTable(int xWidth, int yWidth, int timeUnit) {
        super(xWidth, yWidth, -1); //set autoTime to false since we have our own time

        pockets = new PoolPocket[]{new PoolPocket(0, 0), new PoolPocket(xWidth / 2, 0), new PoolPocket(xWidth, 0), new PoolPocket(0, yWidth), new PoolPocket(xWidth / 2, yWidth), new PoolPocket(xWidth, yWidth)};

        if (timeUnit > 0) {
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new timeUnit(), 0, timeUnit);
        }
    }

    class timeUnit extends TimerTask {
        @Override
        public void run() {
            timeTick();
        }
    }

    public void timeTick() {
        super.timeTick();
        //resetCueIfPocketed();
        checkForPocketed();
    }


    private void resetCueIfPocketed() {
        if(!balls.get(0).onTable){
            balls.get(0).setPosition(100,100); //TODO: This needs to be changed somehow
            balls.get(0).onTable = true;
        }
    }

    private void checkForPocketed(){
        for (Ball ball : balls) {
            if(ball.onTable && isInPocket(ball)){
                //Move the ball out of the way
                ball.onTable = false;
                ball.setPosition(-ball.radius, -ball.radius);
                ball.setSpeed(0, 0);
                pocketed.add(ball); //Add it to pocketed
            }
        }
    }

    private boolean isInPocket(Ball ball) {
        for (PoolPocket pocket : pockets) {
            double distance = Math.sqrt(Math.pow((ball.getxPosition() - pocket.X), 2) + (Math.pow((ball.getyPosition() - pocket.Y), 2))); //Pythagoras to get the distance between the balls center
            if (distance < pocket.RADIUS) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the positions of the pockets as a two dimensional array
     * First position in the array is X the second is Y.
     * Example {{x1,y1},{x2,y2}}
     * */
    public double[][] getPocketPositions(){
        double[][] arr = new double[pockets.length][2];

        for (int i = 0;i<pockets.length;i++) {
            arr[i] = new double[]{pockets[i].X, pockets[i].Y};
        }

        return arr;
    }

    /**
     * Returns the pool pockets radius
     * */
    public double[] getPocketRadius(){
        double[] arr = new double[pockets.length];

        for (int i = 0;i<pockets.length;i++) {
            arr[i] = pockets[i].RADIUS;
        }

        return arr;
    }

}