package com.example.gustav.poolclient;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;

import com.example.gustav.poolclient.pool.Ball;
import com.example.gustav.poolclient.pool.EightBall;
import com.example.gustav.poolclient.pool.PoolTable;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Gustav on 2018-03-11.
 */

public class PoolView extends View {
    Paint p = new Paint();
    EightBall table;
    float scaleFactor;
    ColorScheme colorScheme;

    double[] endPos;

    public PoolView(Context context, EightBall table) {
        super(context);
        this.table = table;
        colorScheme = new EightBallColors();

        //double[] cuePosition = table.getCueBallPosition();
        endPos = new double[]{-1,-1};

        p.setStyle(Paint.Style.FILL);
        p.setAntiAlias(true);

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new redraw(), 0, 50);

    }

    public float getScaleFactor(){
        return scaleFactor;
    }

    class redraw extends TimerTask {
        @Override
        public void run() {
            postInvalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        scaleFactor = (canvas.getWidth()*1.0f)/table.xWidth; //Value to scale up/down the table to fir the screen

        //Draw the 1-15 balls
        p.setColor(Color.RED);
        Paint p2 = new Paint();
        p2.setTextSize(14*scaleFactor);
        p2.setColor(Color.WHITE);
        p2.setTextAlign(Paint.Align.CENTER);

        ArrayList<Ball> balls = table.getBalls();

        //TODO: add the stripes for ball 9-15, Might want to change this altogether to make them roll
        for (Ball ball : balls) {
            p.setColor(colorScheme.getColorFromId(ball.getNumber()));

            canvas.drawCircle((float)((ball.getxPosition())*scaleFactor), (float) ((ball.getyPosition())*scaleFactor), (float)ball.getRadius()*scaleFactor, p);
            if(ball.getNumber() != 0){ //0 indicates cue ball
                canvas.drawText(""+ball.getNumber(),(float)((ball.getxPosition())*scaleFactor), (float) ((ball.getyPosition()+p2.getTextSize()/4)*scaleFactor),p2);
            }
        }
        double[] cuePosition = table.getCueBallPosition();

        //Draw the sides of the table
        //Top
        canvas.drawLine(0, 1, table.xWidth*scaleFactor, 1, p);

        //Right
        canvas.drawLine(table.xWidth*scaleFactor-1, 0, table.xWidth*scaleFactor-1, table.yWidth*scaleFactor, p);

        //Bottom
        canvas.drawLine(0, table.yWidth*scaleFactor-1, table.xWidth*scaleFactor, table.yWidth*scaleFactor-1, p);

        //Left
        canvas.drawLine(1, 0, 1, table.yWidth*scaleFactor, p);


        //Draw the pool pockets
        p.setColor(Color.BLACK);
        double[][] pockets = table.getPocketPositions();
        double[] radius = table.getPocketRadius();
        int i = 0;

        for (double[] pocket : pockets) {
            canvas.drawCircle((float) pocket[0]*scaleFactor, (float)pocket[1]*scaleFactor, (float)radius[i]*scaleFactor, p);
            i++;
        }

        //Draw the "shoot" line

        //-1 && -1 means that we should not draw the line, the player is not aiming right now
        if(endPos[0] != -1 && endPos[1] != -1){

            p.setColor(Color.WHITE);

            double k;
            if((cuePosition[0]-endPos[0]) != 0) {
                k = (cuePosition[1] - endPos[1]) / (cuePosition[0] - endPos[0]); //Calculate the angle
            }else{
                k = Double.MAX_VALUE; //Pretty much a vertical line
            }


            //Find where it intersects with other objects
            double x = 1000;

            //Factor of which the balls a
            double speedScaleFactor = 1;

            //TODO: Something is wrong where, it creates a max box around the cue ball, this should be a circle, IE the length of the thick line is limited by a box, should be a circle
            //It might be that its only limited in the X direction now as well
            //It also messed with the direction of the line
            //However this is ONLY graphical, the ball speed is still capped by the table.MAX_SPEED constant
            if(Math.pow(cuePosition[0]-endPos[0],2)+Math.pow(cuePosition[1]-endPos[1],2) > Math.pow(table.MAX_SPEED,2)){ //if combined velocity to high,
                speedScaleFactor = table.MAX_SPEED/Math.sqrt(Math.pow(cuePosition[0]-endPos[0],2)+Math.pow(cuePosition[1]-endPos[1],2));
            }

            System.out.println("Scale Factor" + speedScaleFactor);
            double xThick = Math.sqrt(Math.pow((cuePosition[0]-endPos[0])*speedScaleFactor,2)+Math.pow((cuePosition[1]-endPos[1])*speedScaleFactor,2));



            //See if the player is aiming left or right
            if(cuePosition[0]-endPos[0] < 0){
                x = -x;
                xThick = -xThick;
            }

            //Draw the line from cue ball to object
            canvas.drawLine((float)cuePosition[0]*scaleFactor, (float)cuePosition[1]*scaleFactor, (float)(cuePosition[0]+x)*scaleFactor, (float)(cuePosition[1]+x*k)*scaleFactor, p);

            p.setStrokeWidth(3);

            //TODO: take care of the speed limit
            //table.MAX_SPEED
            float endX = (float)(cuePosition[0]+xThick)*scaleFactor;
            float endY = (float)(cuePosition[1]+xThick*k)*scaleFactor;

            System.out.println("endX: " + endX);
            System.out.println("endY: " + endY);

            canvas.drawLine((float)cuePosition[0]*scaleFactor, (float)cuePosition[1]*scaleFactor, endX, endY, p);

            p.setStrokeWidth(1);
        }


        //For all balls, check if the ray intersect, take into account how big the balls are


    }
}
