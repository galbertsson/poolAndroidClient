package com.example.gustav.poolclient.pool;


import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * Physic implementation of a plane that handles spheres
 */
public class Table {
    public final int xWidth;
    public final int yWidth;

    private List<CollisionListener> collisionListeners = new ArrayList<>();

    public final static double FRICTION = 0.95;

    ArrayList<Ball> balls = new ArrayList<>(15);

    public Table(int xWidth, int yWidth, int timeUnit) {
        this.xWidth = xWidth;
        this.yWidth = yWidth;

        if (timeUnit > 0) {
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new timeUnit(), 0, timeUnit);
        }
    }

    public boolean noBallsMoving() {
        for (Ball ball : balls) {
            if (ball.getySpeed() != 0 || ball.getxSpeed() != 0) {
                return false;
            }
        }

        return true;
    }

    public void calculateCollisions() {
        Ball firstBall;
        Ball secondBall;

        for (int i = 0; i < balls.size(); i++) {
            //firstBall = balls.get(i);
            for (int k = i + 1; k < balls.size(); k++) {
                firstBall = balls.get(i);
                secondBall = balls.get(k);

                //Math is simpler if the ball to the left is firstBall, and the ball to the right is second ball
                //Assume firstBall is to the left and then check if we are right
                if(firstBall.getxPosition() - firstBall.getRadius() > secondBall.getxPosition() - secondBall.getRadius()){
                    Ball tmp = firstBall;
                    firstBall = secondBall;
                    secondBall = tmp;
                }

                if(firstBall.onTable && secondBall.onTable
                        && firstBall.getxPosition() + firstBall.radius + secondBall.radius > secondBall.getxPosition()
                        && firstBall.getxPosition() < secondBall.getxPosition() + firstBall.radius + secondBall.radius
                        && firstBall.getyPosition() + firstBall.radius + secondBall.radius > secondBall.getyPosition()
                        && firstBall.getyPosition() < secondBall.getyPosition() + firstBall.radius + secondBall.radius){  //Boxes around the balls are close, so the balls might also be colliding

                    double distance = Math.sqrt(Math.pow((firstBall.getxPosition() - secondBall.getxPosition()), 2) + (Math.pow((firstBall.getyPosition() - secondBall.getyPosition()), 2))); //Pythagoras to get the distance between the balls center
                    if (distance < firstBall.radius + secondBall.radius) { //If the actual balls have collided
                        Vector2d delta = (firstBall.getPosition().subtract(secondBall.getPosition()));

                        double d = delta.getLength();

                        Vector2d mtd;
                        if (d != 0.0) {
                            mtd = delta.multiply(((firstBall.getRadius() + secondBall.getRadius()) - d) / d); // minimum translation distance to push balls apart after intersecting

                        } else // Special case. Balls are exactly on top of eachother.  Don't want to divide by zero.
                        {
                            d = secondBall.getRadius() + firstBall.getRadius() - 1.0f;
                            delta = new Vector2d(secondBall.getRadius() + firstBall.getRadius(), 0.0f);

                            mtd = delta.multiply(((firstBall.getRadius() + secondBall.getRadius()) - d) / d);
                        }

                        // resolve intersection
                        double im1 = 1 / firstBall.weight; // inverse mass quantities
                        double im2 = 1 / secondBall.weight;

                        Vector2d firstBallVector = firstBall.getPosition().add(mtd.multiply(im1 / (im1 + im2)));
                        firstBall.setPosition(firstBallVector.getX(), firstBallVector.getY());

                        Vector2d secondBallVector = secondBall.getPosition().subtract(mtd.multiply(im2 / (im1 + im2)));
                        secondBall.setPosition(secondBallVector.getX(), secondBallVector.getY());

                        // impact speed
                        Vector2d v = (firstBall.getVelocity().subtract(secondBall.getVelocity()));
                        double vn = v.dot(mtd.normalize());

                        // sphere intersecting but moving away from each other already
                        if (vn > 0.0) return; //TODO: should this be changed?

                        // collision impulse
                        double impulseMultiplier = (-(1.0 + FRICTION) * vn) / (im1 + im2);
                        Vector2d impulse = mtd.multiply(impulseMultiplier);

                        // change in momentum
                        //firstBall.velocity =
                        Vector2d firstBallVelocity = firstBall.getVelocity().add(impulse.multiply(im1));
                        //secondBall.velocity =
                        Vector2d secondBallVelocity = secondBall.getVelocity().subtract(impulse.multiply(im2));

                        firstBall.setSpeed(firstBallVelocity.getX(), firstBallVelocity.getY());
                        secondBall.setSpeed(secondBallVelocity.getX(), secondBallVelocity.getY());

                        notifyCollisionListeners(firstBall.number, secondBall.number);
                    }
                }
            }
        }
    }

    private void notifyCollisionListeners(int ball1, int ball2) {
        for (CollisionListener collisionListener : collisionListeners) {
            collisionListener.collisionOccured(ball1,ball2);
        }
    }

    public void addCollisionListener(CollisionListener l){
        collisionListeners.add(l);
    }

    public double[][] getBallPositions() {
        double[][] arr = new double[balls.size()-1][2];
        for (int i = 1; i < balls.size(); i++) {
            arr[i-1] = new double[]{balls.get(i).getxPosition(), balls.get(i).getyPosition()};
        }

        return arr;
    }

    public double[] getBallRadius(){
        double[] arr = new double[balls.size()-1];
        for (int i = 1; i < balls.size(); i++) {
            arr[i-1] = balls.get(i).radius;
        }

        return arr;
    }

    public void setCueBallSpeed(double x, double y) {
        balls.get(0).setSpeed(x, y);
    }

    public double[] getCueBallPosition() {
        return new double[]{balls.get(0).getxPosition(), balls.get(0).getyPosition()};
    }

    public void addBall(Ball b) {
        if (!balls.contains(b)) {
            balls.add(b);
            b.onTable = true;
        }
    }

    public double getCueBallRadius() {
        return balls.get(0).radius;
    }

    /**
     * Deep copy of ball list
     * */
    public ArrayList<Ball> getBalls() {
        ArrayList<Ball> localBalls = new ArrayList<>(balls.size());
        for (Ball b : balls) {
            Ball tmp = new Ball(b.number,b.weight,b.radius,b.getxPosition(), b.getyPosition());
            localBalls.add(tmp);
        }
        return localBalls;
    }

    class timeUnit extends TimerTask {
        @Override
        public void run() {
            timeTick();
        }
    }

    public void timeTick() {
        for (Ball ball : balls) {
            /*sb.append(ball.xPosition);
            sb.append(":");
            sb.append(ball.yPosition);
            sb.append(",");
*/
            if ((ball.getyPosition() - ball.radius <= 0 && ball.getySpeed() < 0) || (ball.getyPosition() + ball.radius >= yWidth && ball.getySpeed() > 0)) { //If we are hitting top and are going up or hit bottom and are going down
                ball.invertY();
            }
            if ((ball.getxPosition() - ball.radius <= 0 && ball.getxSpeed() < 0) || (ball.getxPosition() + ball.radius >= xWidth && ball.getxSpeed() > 0)) {
                ball.invertX();
            }

            ball.timeTick(1);//TODO: change the hardcoded
        }/*
        if (balls.size() > 0) {
            sb.append(";");
        }*/

        calculateCollisions();

        if (noBallsMoving()) {
            synchronized (this) {
                notify();//Notify any waiting thread that the balls have stopped moving
            }
            //System.out.println(sb);
        }


    }

    //StringBuilder sb = new StringBuilder();
}