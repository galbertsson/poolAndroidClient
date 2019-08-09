package com.example.gustav.poolclient.pool;

public class Player {
    public final String NAME;
    public final int LOCAL_ID;
    private int ballSet = -1;


    public Player(String name, int id){
        NAME = name;
        LOCAL_ID = id;
    }

    /**
     * @param ballSet the ballSet to be used, 1 = low balls(1-7), 9 = high balls(9-13)
     * */
    void setBallSet(int ballSet){
        if(ballSet == 1 || ballSet == 9){
            this.ballSet = ballSet;
        }
    }

    public int getBallSet() {
        return ballSet;
    }
}
