package com.example.gustav.poolclient;

import android.app.Activity;
import android.widget.TextView;
import com.example.gustav.poolclient.pool.ClientListener;

class TextChanger implements ClientListener{

    TextView text;
    Activity ac;

    public TextChanger(Activity ac,TextView text) {
        this.text = text;
        this.ac = ac;
    }

    @Override
    public void notifyChange(final int state) {
        System.out.print("got state change!");

        ac.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(state == 0){
                    //TODO: No clue, is it needed?
                }else if(state == 1){
                    text.setText("Your turn!");
                    text.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            text.setText("");
                        }
                    },1000);
                }else if(state == 2){
                    text.setText("Opponents turn!");
                    text.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            text.setText("");
                        }
                    },1000);
                }else if(state == 3){
                    text.setText("");
                }else if(state == 4){
                    text.setText("Opponent forfeited");
                }

            }
        });


    }
}
