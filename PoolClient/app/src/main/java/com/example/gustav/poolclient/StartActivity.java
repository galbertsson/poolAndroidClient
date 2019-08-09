package com.example.gustav.poolclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.gustav.poolclient.pool.Client;
import com.example.gustav.poolclient.pool.EightBall;

import java.io.IOException;
import java.util.ArrayList;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class StartActivity extends AppCompatActivity {

    private ListView listview;
    private View view;
    private ArrayAdapter<String> adapter;
    public static Client c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.start_fullscreen);

        listview = findViewById(R.id.channelList);
        view = findViewById(R.id.startFrame);

        //EightBall table = new EightBall(1000, 500,50);
        //final Client c;
        TextView tv = findViewById(R.id.turn);

        try {
            c = new Client(null);
        } catch (IOException e) {
            tv.setText("ERROR: connection to the server failed");
            e.printStackTrace();
            return;
        }

        new Thread(c).start();

        // Create an ArrayAdapter from List
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, c.getCachedChannelsString());

        listview.setAdapter(arrayAdapter);

        //TODO: this could be bad, we do this to make sure that the client is all set up and have connection with the server
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                c.getChannels();

                for (String s : c.getCachedChannelsString()) {
                    arrayAdapter.add(s);
                }

            }
        }).start();

        hide();

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    final int position, long id) {

                System.out.println("clicked on " + position);
                //c.cachedChannels.get(position);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        c.joinChannel(c.cachedChannels.get(position)); //Join the channel, dosent know if we succeded yet
                        //TODO: for now assume that it worked, this needs a check later,
                        Intent myIntent = new Intent(view.getContext(), FullscreenActivity.class);
                        //Bundle b = new Bundle();
                        startActivityForResult(myIntent, 0);
                    }
                }).start();
            }

        });
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


    public void create_room(View view) {
        c.createChannel();
        Intent myIntent = new Intent(view.getContext(), FullscreenActivity.class);
        startActivityForResult(myIntent, 0);
    }
}
