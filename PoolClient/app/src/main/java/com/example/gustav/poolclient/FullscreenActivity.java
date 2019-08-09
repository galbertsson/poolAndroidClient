package com.example.gustav.poolclient;

import android.annotation.SuppressLint;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.gustav.poolclient.pool.Client;
import com.example.gustav.poolclient.pool.EightBall;

import java.io.IOException;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {

    private View view;
    public Client c = StartActivity.c; //TODO: consider making client a singelton instead

    public void setUp(Client c){
        this.c = c;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        view = findViewById(R.id.baseFrame);

        @SuppressLint("WrongViewCast")
        FrameLayout layout = (FrameLayout)view;

        EightBall table = new EightBall(1000, 500,50);
        c.setTable(table);

        /*
        try {
            c = new Client(table);
        } catch (IOException e) {
            tv.setText("ERROR: connection to the server failed");
            e.printStackTrace();
        }

        new Thread(c).start();*/



        PoolView pv = new PoolView(this, table);

        touchListener tl = new touchListener(c);

        TextView tv = findViewById(R.id.turn);
        TextChanger tc = new TextChanger(this,tv);
        c.addListener(tc);

        pv.setOnTouchListener(tl);

        layout.addView(pv);
        hide();
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }
}
