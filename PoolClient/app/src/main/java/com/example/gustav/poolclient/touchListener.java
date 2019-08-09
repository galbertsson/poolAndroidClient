package com.example.gustav.poolclient;

import android.support.v4.util.Pools;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.gustav.poolclient.pool.Client;

/**
 * Created by Gustav on 2018-03-24.
 */

public class touchListener implements View.OnTouchListener {

    Client client;

    public touchListener(Client c){
        client = c;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        if(view instanceof PoolView){
            double scaleFactor = ((PoolView)view).getScaleFactor();
            //((PoolView) view).endPos = new double[]{motionEvent.getX()/scaleFactor, motionEvent.getY()/scaleFactor};


            final double x = motionEvent.getX()/scaleFactor;
            final double y = motionEvent.getY()/scaleFactor;

            if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                ((PoolView) view).endPos = new double[]{-1,-1};
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        client.shoot(x,y);
                    }
                }).start(); //Create a new thread and shoot, needs new thread since view thread cannot do that.
            }else if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                ((PoolView) view).endPos = new double[]{x,y};
            }else if(motionEvent.getAction() == MotionEvent.ACTION_MOVE){
                ((PoolView) view).endPos = new double[]{x,y};
            }
        }

        return true;
    }
}
