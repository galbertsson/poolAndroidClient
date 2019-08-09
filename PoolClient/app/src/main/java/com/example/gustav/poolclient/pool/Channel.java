package com.example.gustav.poolclient.pool;

public class Channel {

    private int capacity;
    private String roomId;
    private int peopleInRoom;

    public Channel(String id, int pplInRoom){
        capacity = 2;
        roomId = id;
        peopleInRoom = pplInRoom;
    }

    public int getCapacity() {
        return capacity;
    }

    public String getRoomId() {
        return roomId;
    }

    public int getPeopleInRoom() {
        return peopleInRoom;
    }
}
