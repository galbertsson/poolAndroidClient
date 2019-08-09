package com.example.gustav.poolclient.pool;

public class Vector2d {

    private double x;
    private double y;

    public Vector2d() {
        setX(0);
        setY(0);
    }

    public Vector2d(double x, double y) {
        setX(x);
        setY(y);
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getX() {
        return x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getY() {
        return y;
    }

    public void set(double x, double y) {
        setX(x);
        setY(y);
    }

    /*
     * Dot product between this and v2
     * */
    public double dot(Vector2d v2) {
        return getX() * v2.getX() + getY() * v2.getY();
    }

    public double getLength() {
        return Math.sqrt(Math.pow(getX(),2) + Math.pow(getY(),2));
    }

    public Vector2d add(Vector2d v2) {
        Vector2d result = new Vector2d();
        result.setX(getX() + v2.getX());
        result.setY(getY() + v2.getY());
        return result;
    }

    public Vector2d subtract(Vector2d v2) {
        Vector2d result = new Vector2d();
        result.setX(getX() - v2.getX());
        result.setY(getY() - v2.getY());
        return result;
    }

    public Vector2d multiply(double scaleFactor) {
        Vector2d result = new Vector2d();
        result.setX(getX() * scaleFactor);
        result.setY(getY() * scaleFactor);
        return result;
    }

    public Vector2d normalize() {
        double length = getLength();
        if (length != 0.0) {
            setX(getX() / length);
            setY(getY() / length);
        } else {
            setX(0);
            setY(0);
        }

        return this;
    }
}
