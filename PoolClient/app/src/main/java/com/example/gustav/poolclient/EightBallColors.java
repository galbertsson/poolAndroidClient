package com.example.gustav.poolclient;

import android.graphics.Color;
import android.util.Pair;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

public class EightBallColors implements ColorScheme {
    HashMap<Integer, Integer> colorMap = new HashMap<>();

    public EightBallColors(){
        colorMap.put(0,Color.WHITE);
        colorMap.put(1, -480229); //Dark yellow
        colorMap.put(2, Color.BLUE);
        colorMap.put(3, Color.RED);
        colorMap.put(4, -14016427); //PURPLE
        colorMap.put(5, -165047); //ORANGE
        colorMap.put(6, Color.GREEN);
        colorMap.put(7, -9235689); //Dark red
        colorMap.put(8, Color.BLACK);
        colorMap.put(9, -480229); //Dark yellow
        colorMap.put(10, Color.BLUE);
        colorMap.put(11, Color.RED);
        colorMap.put(12, -14016427);
        colorMap.put(13, -165047);
        colorMap.put(14, Color.GREEN);
        colorMap.put(15, -9235689);
    }

    @Override
    public int getColorFromId(int id) {
        return colorMap.get(id);
    }
}
