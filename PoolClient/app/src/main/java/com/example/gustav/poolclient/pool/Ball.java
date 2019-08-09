package com.example.gustav.poolclient.pool;

public class Ball {
    public static final double DECELERATE_COEFFICIENT = 0.988;

    private Vector2d velocity;
    private Vector2d position;

    final int number;
    final double weight;
    final double radius;

    boolean onTable = false;

    public Ball(int number, double weight, double radius, double xPosition, double yPosition){
        this.number = number;
        this.weight = weight;
        this.radius = radius;
        velocity = new Vector2d(0,0);
        position = new Vector2d(xPosition, yPosition);
    }

    public void timeTick(double deltaTime) {
        velocity.set(velocity.getX()*DECELERATE_COEFFICIENT, velocity.getY()*DECELERATE_COEFFICIENT);

        if(velocity.getX() > -0.08 && velocity.getX() < 0.08){
            velocity.setX(0);
        }
        if(velocity.getY() > -0.08 && velocity.getY() < 0.08){
            velocity.setY(0);
        }


        position.setX(position.getX() + (velocity.getX() * deltaTime));
        position.setY(position.getY() + (velocity.getY() * deltaTime));
    }

    public double getxPosition() {
        return position.getX();
    }

    public double getyPosition() {
        return position.getY();
    }

    /**
     * @return returns the y speed of the object.
     */
    public double getySpeed() {
        return velocity.getY();
    }

    public void invertY() {
        velocity.setY(-velocity.getY());
    }

    public void invertX() {
        velocity.setX(-velocity.getX());
    }

    public double getxSpeed() {
        return velocity.getX();
    }

    public void setSpeed(double newx, double newy) {
        velocity.set(newx, newy);
    }

    public double getRadius() {
        return radius;
    }

    public void setPosition(double x, double y) {
        position.set(x,y);
    }

    /*
    * Defensive copy method
    * */
    public Vector2d getPosition() {
        return new Vector2d(position.getX(), position.getY());
    }

    /*
     * Defensive copy method
     * */
    public Vector2d getVelocity() {
        return new Vector2d(velocity.getX(), velocity.getY());
    }

    public int getNumber() {
        return number;
    }
}
