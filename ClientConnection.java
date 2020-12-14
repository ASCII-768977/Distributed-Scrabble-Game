import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.print.DocFlavor;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ClientConnection extends Thread {

    private Socket clientSocket;
    private DataInputStream reader;
    private DataOutputStream writer;

    private boolean flag1=false;
    private String roomName;

    private int order;
    private int action;
    private int passNum=0;

    private String word1;
    private String word2;
    private int mark1;
    private int mark2;
    private int count1;
    private int count2;

    public ClientConnection(Socket clientSocket) {
        try {
            this.clientSocket = clientSocket;
            reader = new DataInputStream(clientSocket.getInputStream());
            writer = new DataOutputStream(clientSocket.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        // The JSON Parser
        JSONParser parser = new JSONParser();
        JSONObject message;
        try {
            while (true) {
                //Listening message
                message = (JSONObject) parser.parse(reader.readUTF());
                if(parser(message)==null)
                    break;
            }

            if(flag1==false){
                clientSocket.close();
                ServerState.getInstance().clientDisconnected(this);
                System.out.println("Client " +Thread.currentThread().getName()
                        + " disconnected.\nNow, "
                        +ServerState.getInstance().getConnectedClients().size()
                        + " people online.");
                close();
            }else {
                clientSocket.close();
                System.out.println("Invalid login.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String parser(JSONObject message){
        //1, invite
        if(message.get("msg").equals("1"))
            invite(message);

        //3, refusal
        if(message.get("msg").equals("3")){
            refuse(message.get("hostname").toString());
        }

        //4, create a game
        if(message.get("msg").equals("4"))
            createGame();

        //5, exit
        if(message.get("msg").equals("5"))
            return null;

        //6, log in and set client name
        if(message.get("msg").equals("6")){
            checkUser(message.get("name").toString());
            if(flag1==true)
                return null;
        }

        //7, back to hall from room
        if (message.get("msg").equals("7")){
            back();
        }

        //deal with request of entering room, and
        if (message.get("msg").equals("8")){
            addToRoom(message.get("hostname").toString());
        }

        //About game
        if (message.get("msg").equals("001"))
            setGame();

        if (message.get("msg").equals("002"))
            judgeWord(message);

        //update all players'chessboard
        if (message.get("msg").equals("003"))
            sendLetter(message);

        //argeeOrNot
        if (message.get("msg").equals("004")){
            agreeOrNot(message.get("word").toString(),message.get("result").toString());
        }

        if (message.get("msg").equals("005"))
            next(Integer.parseInt(message.get("increasement").toString()));

        //from game state back to hall
        if (message.get("msg").equals("007"))
            backToHall(message);

        return "No_exit";
    }

    //send message to invitee
    public void invite(JSONObject message){
        List<ClientConnection> clients = ServerState.getInstance().getConnectedClients();
        //check if this client is already in room and not in hall
        for (ClientConnection client:clients) {
            if ( message.get("username").toString().equals(client.getName()) && client.roomName!=null){
                JSONObject feedback=new JSONObject();
                feedback.put("msg","3");
                feedback.put("name",client.getName());
                write(feedback.toString());
                return;
            }
        }
        //Normal inviting operation
        for(ClientConnection client : clients) {
            //check every client for sending invitation
            if(message.get("username").toString().compareTo(client.getName())==0){
                JSONObject feedback=new JSONObject();
                feedback.put("msg","1");
                feedback.put("hostname",roomName);
                feedback.put("invitor",currentThread().getName());
                client.write(feedback.toString());
                return;
            }
        }
        //if this client doesn't exist
        JSONObject feedback=new JSONObject();
        feedback.put("msg","3");
        feedback.put("noExist",message.get("username").toString());
        write(feedback.toString());
        return;
    }

    public void refuse(String hostname){
            List<ClientConnection> clients = ServerState.getInstance().getConnectedClients();
            for(ClientConnection client : clients) {
                if(hostname.compareTo(client.getName())==0){
                    JSONObject m1=new JSONObject();
                    m1.put("msg","3");
                    m1.put("Name",currentThread().getName());
                    client.write(m1.toString());
                    return;
                }
            }
    }

    //Operation to take the client into room
    public void addToRoom(String hostname){
        JSONObject m1=new JSONObject();

        HashMap<String, List<ClientConnection>> inGameRooms=ServerState.getInstance().getCreatedRoomInGameState();
        //if the room is in game state
        if (inGameRooms.containsKey(hostname)){
            m1.put("msg","8");
            m1.put("permission","no");
            write(m1.toString());
            return;
        //check whether the corresponding room in game
        }else if (!ServerState.getInstance().getRoom().containsKey(hostname)){
            m1.put("msg","8");
            m1.put("permission","noRoom");
            write(m1.toString());
            return;
        }

        //if you are in certain room, end this operation
        if(roomName!=null){
            return;
        }

        HashMap<String, List<ClientConnection>> rooms=ServerState.getInstance().getRoom();
        rooms.get(hostname).add(this);
        roomName=hostname;

        m1.put("msg","8");
        m1.put("permission","yes");
        getInRoomPeople(m1, rooms.get(hostname));
        for (ClientConnection client : rooms.get(hostname)){
            client.write(m1.toString());
        }
    }

    public void createGame(){
        //create a room in room list
        roomName= Integer.toString(Server.num);
        ServerState.getInstance().roomCreated(roomName);
        //add this client to the room
        ServerState.getInstance().addClientIntoRoom(roomName,this);

        //Set message including name list
        JSONObject message= new JSONObject();
        message.put("msg","4");
        message.put("text","Room created.\n");
        message.put("roomName",Integer.toString(Server.num));
        roomName = Integer.toString(Server.num);
        getOnlinePeople(message);
        HashMap<String, List<ClientConnection>> rooms=ServerState.getInstance().getRoom();
        getInRoomPeople(message, rooms.get(roomName));

        write(message.toString());
        message.replace("msg","10");
        getCreatedRooms(message);
        for (ClientConnection client:ServerState.getInstance().getConnectedClients()){
            if (client.roomName==null){
                client.write(message.toString());
            }
        }
        Server.num++;
    }

    public void checkUser(String name){
        JSONObject feedback=new JSONObject();
        Thread.currentThread().setName(name);
        //check same name
        List<ClientConnection> clients = ServerState.getInstance().getConnectedClients();
        for(ClientConnection client : clients) {
            if(name.compareTo(client.getName())==0){
                feedback.put("msg","11");
                feedback.put("text","Warning: "+name+" already in server");
                write(feedback.toString());
                flag1=true;
                return;
            }
        }

        //If no same name
        if (flag1==false){
            ServerState.getInstance().clientConnected(this);
            Thread.currentThread().setName(name);
            System.out.println("Now, "+ServerState.getInstance().getConnectedClients().size()
                    +" people online.");
            System.out.println("Reading messages from client - "+Thread.currentThread().getName()+".");
            //Feedback for the corresponding client.
            feedback.put("msg","6");
            feedback.put("text","Hi, "+name+".\n");
            getOnlinePeople(feedback);
            getCreatedRooms(feedback);
            write(feedback.toString());
            feedback.replace("msg","9");
            for (ClientConnection client:clients) {
                client.write(feedback.toString());
            }
        }
    }

    //from room to hall
    public void back(){
        ServerState.getInstance().getRoom().get(roomName).remove(this);
        JSONObject m1=new JSONObject();
        //
        if (ServerState.getInstance().getRoom().get(roomName).size()==0){
            ServerState.getInstance().getRoom().remove(roomName);
            m1.put("msg","10");
            getCreatedRooms(m1);

            roomName=null;
            for (ClientConnection client:ServerState.getInstance().getConnectedClients()) {
                if (client.roomName==null){
                    client.write(m1.toString());
                }
            }
            m1.replace("msg","9");
            getOnlinePeople(m1);
            write(m1.toString());
        }else {
            m1.put("msg","7");
            getInRoomPeople(m1,ServerState.getInstance().getRoom().get(roomName));
            for (ClientConnection client :ServerState.getInstance().getRoom().get(roomName)) {
                client.write(m1.toString());
            }
            roomName=null;
            m1.replace("msg","10");
            getCreatedRooms(m1);
            write(m1.toString());
        }
    }

    //when client disconnects to server
    public void close(){
        //tell all in the server, I disconnect
        List<ClientConnection> clients = ServerState.getInstance().getConnectedClients();
        JSONObject m1=new JSONObject();
        m1.put("msg","9");
        getOnlinePeople(m1);
        for(ClientConnection client:clients){
            client.write(m1.toString());
        }

        //Someone in the room disconnects
        if(roomName!=null){
            //ServerState.getInstance().getRoom().get(roomName).remove(this);
            //client disconnects from game
            if (ServerState.getInstance().getCreatedRoomInGameState().containsKey(roomName)){
                //1 for the left guy
                if (ServerState.getInstance().getRoom().get(roomName).size()<=2){
                    ServerState.getInstance().getRoom().get(roomName).remove(this);
                    //for the only one left in game
                    for (ClientConnection client: ServerState.getInstance().getRoom().get(roomName)){
                        JSONObject m2=new JSONObject();
                        m2.put("msg","007");
                        getOnlinePeople(m2);
                        getCreatedRooms(m2);
                        client.write(m2.toString());
                        client.roomName=null;
                    }

                    //remove this room from normal room list
                    ServerState.getInstance().getRoom().remove(roomName);
                    //remove this room from in game state list
                    ServerState.getInstance().logoutRoomInGameState(roomName);

                    m1.put("msg","10");
                    getCreatedRooms(m1);
                    for (ClientConnection client:ServerState.getInstance().getConnectedClients()) {
                        if (client.roomName==null)
                            client.write(m1.toString());
                    }
                //there are at least 2 clients in the game, so game continues
                } else {
                    ServerState.getInstance().getRoom().get(roomName).remove(this);
                    m1.replace("msg","009");
                    m1.put("deserter",currentThread().getName());
                    for (ClientConnection client :ServerState.getInstance().getRoom().get(roomName)) {
                        client.write(m1.toString());
                    }
                }
            }else {
                ServerState.getInstance().getRoom().get(roomName).remove(this);
                //if this client in room of non-game state and there is no client in the room
                if (ServerState.getInstance().getRoom().get(roomName).size()==0){
                    //remove this room from normal room list
                    ServerState.getInstance().getRoom().remove(roomName);
                    m1.put("msg","10");
                    getCreatedRooms(m1);
                    for (ClientConnection client:ServerState.getInstance().getConnectedClients()) {
                        if (client.roomName==null)
                            client.write(m1.toString());
                    }
                } else {
                    getInRoomPeople(m1,ServerState.getInstance().getRoom().get(roomName));
                    m1.replace("msg","7");
                    for (ClientConnection client :ServerState.getInstance().getRoom().get(roomName)) {
                        client.write(m1.toString());
                    }
                }
            }
        }
    }

    //Needs to be synchronized because multiple threads can me invoking this method at the same
    //time
    public synchronized void write(String message) {
        try {
            writer.writeUTF(message);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getOnlinePeople(JSONObject message){
        for(ClientConnection client : ServerState.getInstance().getConnectedClients()) {
            message.put(client.getName(),"online");
        }
    }

    public void getInRoomPeople(JSONObject message, List<ClientConnection> room){
        for (ClientConnection client: room) {
            message.put(client.getName(),"inRoom");
        }
    }

    public void getCreatedRooms(JSONObject message){
        for (String name:ServerState.getInstance().getRoom().keySet()) {
            message.put(name,"roomName");
        }
    }

    //### About game

    //all men in room enter game state
    public void setGame(){

        //if only one man in room, refuse
        if ( ServerState.getInstance().getRoom().get(roomName).size()<2){
            JSONObject m1=new JSONObject();
            m1.put("msg","001");
            m1.put("permission","no");
            write(m1.toString());
            return;
        }

        int order=0, action=0;
        //add room to list including rooms in game state
        ServerState.getInstance().createdRoomInGameState(roomName);
        JSONObject m1=new JSONObject();
        m1.put("msg","001");
        getInRoomPeople(m1,ServerState.getInstance().getRoom().get(roomName));
        //set each man in room
        for (ClientConnection client: ServerState.getInstance().getRoom().get(roomName)) {
            m1.put("nameOfMyself",client.getName());
            client.write(m1.toString());
            client.order=order++;
            client.action=action;
        }
        //push game
        next(0);
    }

    public void judgeWord(JSONObject m){
        word1=m.get("word1").toString();
        word2=m.get("word2").toString();

        mark1=Integer.parseInt(m.get("mark1").toString());
        mark2=Integer.parseInt(m.get("mark2").toString());

        count1=0;count2=0;

        JSONObject m1=new JSONObject(m);
        m1.remove("mark1");
        m1.remove("mark2");
        for (ClientConnection client:ServerState.getInstance().getRoom().get(roomName)) {
            if (!currentThread().getName().equals(client.getName()))
                client.write(m1.toString());
        }
    }

    public void sendLetter(JSONObject m1){
        for (ClientConnection client:ServerState.getInstance().getRoom().get(roomName)) {
            if (!currentThread().getName().equals(client.getName())){
                client.write(m1.toString());
            }
        }
    }

    public void next(int increasement){
        //check if all pass
        if (increasement==1){
            for (ClientConnection client :ServerState.getInstance().getRoom().get(roomName)) {
                client.passNum++;
            }
            if(passNum==ServerState.getInstance().getRoom().get(roomName).size()){
                String tempname=roomName;
                for (ClientConnection client:ServerState.getInstance().getRoom().get(roomName)) {
                    client.roomName=null;
                }
                ServerState.getInstance().getRoom().remove(tempname);
                ServerState.getInstance().logoutRoomInGameState(tempname);
                //update situation of hall
                JSONObject m1=new JSONObject();
                m1.put("msg","007");
                getCreatedRooms(m1);
                getOnlinePeople(m1);
                for (ClientConnection client:ServerState.getInstance().getConnectedClients()) {
                    if (client.roomName==null){
                        client.write(m1.toString());
                    }
                }
                return;
            }
        }else{
            for (ClientConnection client :ServerState.getInstance().getRoom().get(roomName)) {
                client.passNum=0;
            }
        }

        //Normal case
        JSONObject m1=new JSONObject();
        m1.put("msg","005");

        for (ClientConnection client:ServerState.getInstance().getRoom().get(roomName)) {
            if ( client.order == (client.action % ServerState.getInstance().getRoom().get(roomName).size()) )
                client.write(m1.toString());
        }

        for (ClientConnection client:ServerState.getInstance().getRoom().get(roomName)) {
            client.action++;
        }
    }

    public void agreeOrNot(String word,String result) {
        for (ClientConnection client : ServerState.getInstance().getRoom().get(roomName)) {
            if (client.word1 != null) {

                if (word.equals(client.word1) && result.equals("agree")) {
                    client.count1++;
                } else if (word.equals(client.word2) && result.equals("agree")) {
                    client.count2++;
                } else if (word.equals(client.word1) && result.equals("disagree")) {
                    client.count1++;
                    client.mark1 = 0;
                } else if (word.equals(client.word2) && result.equals("disagree")) {
                    client.count2++;
                    client.mark2 = 0;
                }

                if ((client.count1+client.count2) == 2*(ServerState.getInstance().getRoom().get(roomName).size() - 1)) {

                    JSONObject m1 = new JSONObject();
                    m1.put("msg", "006");
                    m1.put("name", client.getName());
                    int mark=client.mark1+client.mark2;
                    m1.put("marks", mark);
                    for (ClientConnection clint : ServerState.getInstance().getRoom().get(roomName)) {
                        clint.write(m1.toString());
                    }
                    client.word1 = null;
                    client.word2 = null;
                    client.mark1 = 0;
                    client.mark2 = 0;
                }
            }
        }
    }

    //only for game state back to hall
    public void backToHall(JSONObject message){

        //if only one client in room after a client left
        if (ServerState.getInstance().getRoom().get(roomName).size()==2){
            String tempName=roomName;
            //for the only one left in game, send it
            for (ClientConnection client: ServerState.getInstance().getRoom().get(roomName)){
                JSONObject m2=new JSONObject();
                m2.put("msg","007");
                getOnlinePeople(m2);
                getCreatedRooms(m2);
                client.write(m2.toString());
                client.roomName=null;
            }

            //double remove from list in game state and not in
            ServerState.getInstance().logoutRoomInGameState(tempName);
            ServerState.getInstance().getRoom().remove(tempName);

            JSONObject m1=new JSONObject();
            m1.put("msg","008");
            getOnlinePeople(m1);
            getCreatedRooms(m1);
            //to every client
            for (ClientConnection client:ServerState.getInstance().getConnectedClients()) {
                if (client.roomName==null){
                    client.write(m1.toString());
                }
            }
        }else {
            //there are still at leasst 2 people in game. Message only for myself
            ServerState.getInstance().getRoom().get(roomName).remove(this);

            JSONObject m1=new JSONObject();
            m1.put("msg","009");
            m1.put("deserter",currentThread().getName());
            for (ClientConnection client :ServerState.getInstance().getRoom().get(roomName)) {
                client.write(m1.toString());
            }

            //message:msg:007 to myself
            roomName=null;
            getCreatedRooms(message);
            getOnlinePeople(message);
            write(message.toString());
        }
    }
}




//    public void joinRoom(){
//        JSONObject m1=new JSONObject();
//        m1.put("msg","7");
//
//        HashMap<String, List<ClientConnection>> rooms=ServerState.getInstance().getRoom();
//
//        for(String name : rooms.keySet()) {
//            m1.put(name,null);
//        }
//        write(m1.toString());
//    }

//    public void joinRoom(){
//        JSONObject m1=new JSONObject();
//        m1.put("msg","7");
//
//        HashMap<String, List<ClientConnection>> rooms=ServerState.getInstance().getRoom();
//
//        for(String name : rooms.keySet()) {
//            m1.put(name,null);
//        }
//        write(m1.toString());
//    }

//2, join a game
//        if(message.get("msg").equals("2"))
//                joinRoom();