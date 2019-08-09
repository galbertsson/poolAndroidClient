package com.example.gustav.poolclient.pool;


public interface ClientListener {

    /**
     * @param state the state that the client has entered,
     *              state = 0, idle state (Might not be used?)
     *              state = 1, our turn to shoot
     *              state = 2, opponents turn to shoot
     *              state = 3, balls are moving on table
     *              state = 4, opponent forfeited.
     * */
    void notifyChange(int state);
}
