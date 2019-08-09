package com.example.gustav.poolclient.pool;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Client implements Runnable{
    private EightBall t;
    private AtomicBoolean ourTurn = new AtomicBoolean(false);

    private Socket s; //= new Socket("192.168.1.129", 25515);
    private BufferedReader in; //= new BufferedReader(new InputStreamReader(s.getInputStream()));
    private PrintWriter out; //= new PrintWriter(s.getOutputStream(),true);
    public ArrayList<Channel> cachedChannels = new ArrayList<>();

    private List<ClientListener> listeners = new ArrayList<>();

    public Client(EightBall t) throws IOException {
        this.t = t;
    }

    public void shoot(double x, double y){
        System.out.println("Got message from client.shoot()");

        if(ourTurn.getAndSet(false)){
            notifyListeners(3);

            double dx = t.getCueBallPosition()[0]-x;
            double dy = t.getCueBallPosition()[1]-y;

            out.write("/sho " + dx + ":" + dy + "\n");
            out.flush();
            System.out.println("Sending: " + "/sho " + dx + ":" + dy);


            t.shoot(dx,dy);
            System.out.println("Sent the message from client.shoot()");
        }

    }

    public void setTable(EightBall table){
        this.t = table;
    }

    public void getChannels(){
        while(out == null){}
        System.out.println("Sent req");

        out.write("/req\n");
        out.flush();
    }

    public void joinChannel(Channel c){
        out.write("/join " + c.getRoomId() + "\n");
        out.flush();
    }

    public void createChannel(){
        while(out == null){}

        out.write("/crt\n");
        out.flush();
    }

    @Override
    public void run() {
        try {
            s = new Socket("192.168.1.198", 25515);
            in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            out = new PrintWriter(s.getOutputStream(),true);

            System.out.println("Ready");
            while(true) {
                System.out.println("Ready for next command");
                String message = in.readLine();
                System.out.println("Got the message!");
                if(message.startsWith("/trn")){//Our turn to shoot
                    ourTurn.set(true);
                    notifyListeners(1);
                    System.out.println("My turn!");
                }else if(message.startsWith("/skp")){//Someones turn in skipped, Change GUI
                    if(ourTurn.compareAndSet(true,false)){ //If it was our turn, set it to not our turn
                        notifyListeners(5);
                    }else{ //If it wasn't our turn, we will soon get a new /trn message
                        notifyListeners(4);
                    }
                    System.out.println("Skipped turn, toggle UI");
                }else if(message.startsWith("/sho")){//The other client shot
                    notifyListeners(3);
                    message = message.substring(message.indexOf(" ")+1);
                    double x = Double.parseDouble(message.substring(0,message.indexOf(":")));
                    double y = Double.parseDouble(message.substring(message.indexOf(":")+1));

                    t.shoot(x,y);
                }else if(message.startsWith("/opff")){
                    notifyListeners(4);
                    //TODO: might want to terminate here, or we do that after the server told us to.
                }else if(message.startsWith("/rms")){
                    message = message.substring(message.indexOf(" ")+1);
                    parseChannels(message);
                } else if(message.startsWith("/succ")){
                    message = message.substring(message.indexOf(" ")+1);
                    notifyListeners(6);
                    //parseChannels(message);
                }
                else{
                    System.out.println("PARSE ERROR: received " + message);
                }
            }

        } catch (IOException e) {
            System.out.println("Failed to open socket");
            e.printStackTrace();
        }
    }

    private void parseChannels(String message) {
        cachedChannels.clear();

        while(message.length() != 0){
            String id = message.substring(0,message.indexOf(","));
            String ppl = message.substring(message.indexOf(",")+1, message.indexOf(":"));
            int pplnumb = Integer.parseInt(ppl);

            cachedChannels.add(new Channel(id, pplnumb));

            message = message.substring(message.indexOf(":")+1);
        }
    }

    public void addListener(ClientListener cl){
        listeners.add(cl);
    }

    /**
     * Notify all listeners that the state has changed
     * @param state the state that the client has entered,
     *              state = 0, idle state (Might not be used?)
     *              state = 1, our turn to shoot
     *              state = 2, opponents turn to shoot
     *              state = 3, balls are moving on table
     *              state = 4, opponents turn skipped
     *              state = 5, our turn skipped
     *              state = 6, joined a game
     * */
    private void notifyListeners(int state) {
        System.out.println("Sending nortify to " + listeners.size() + " listeners!");
        for (ClientListener listener : listeners) {
            listener.notifyChange(state);
        }
    }

    public ArrayList<String> getCachedChannelsString() {
        ArrayList<String> a = new ArrayList<>();

        for (Channel cachedChannel : cachedChannels) {
            a.add(cachedChannel.getRoomId() + " " + cachedChannel.getPeopleInRoom() + "/" + cachedChannel.getCapacity());
        }

        return a;
    }
}
