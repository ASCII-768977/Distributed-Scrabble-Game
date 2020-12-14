import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.swing.*;
import java.io.*;

//import java.io.DataInputStream;
import java.net.SocketException;
import java.util.Iterator;

public class MessageListener extends Thread {

    private Client myself;

    public MessageListener(Client myself) {
         this.myself= myself;
    }

    @Override
    public void run() {
        try {
            //Read messages from the server while the end of the stream is not reached
            while(true) {
                parser((JSONObject) new JSONParser().parse(myself.getReader().readUTF()));
            }
        } catch (EOFException e){

        } catch (SocketException e) {
            myself.getGui2().getFrame().dispose();
            //check whether have begin the game
            if(myself.getGame()!=null){
                myself.getGame().getFrame().dispose();
            }
            Notice5 notice5=new Notice5();
            notice5.getFrame().setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void parser(JSONObject message){

        //1, for receiving inviting message
        if(message.get("msg").equals("1")){
            InviteNotice notice=new InviteNotice(message,myself);
            notice.getFrame().setVisible(true);
        }

        //3, for getting refusal or that client doesn't exist
        if(message.get("msg").equals("3")){
            if (message.containsKey("noExist")){
                myself.getGui2().getTextArea_2().append(message.get("noExist").toString()+" doesn't exist.\n");
            }else if (message.containsKey("name")){
                myself.getGui2().getTextArea_2().append(message.get("name").toString()+" is not in hall.\n");
            }else {
                myself.getGui2().getTextArea_2().append(message.get("Name").toString()+" refused you.\n");
            }
        }

        //4, create a game
        if(message.get("msg").equals("4")){
            myself.getGui2().getTextArea_2().append(message.get("text").toString());
            parserOnlineNameList(message);
            parserInRoomNameList(message);
            myself.getGui2().state2();
        }

        //make sure I have logged in successfully
        if(message.get("msg").equals("6")){
            myself.getGui1().getFrame().dispose();
            myself.gui2(myself);
            //Receive welcome message
            myself.getGui2().getTextArea_2().append(message.get("text").toString());
            parserOnlineRoom(message);
            parserOnlineNameList(message);
        }

        //only update name list of client in room
        if(message.get("msg").equals("7"))
            parserInRoomNameList(message);

        //receive message that can I join the room
        if(message.get("msg").equals("8")){
            //check permission from server
            if (message.get("permission").equals("noRoom")){
                Notice3 notice3 = new Notice3();
                notice3.getFrame().setVisible(true);
            }else if(message.get("permission").equals("no")){
                Notice2 notice=new Notice2();
                notice.getFrame().setVisible(true);
            }else {
                myself.getGui2().state2();
                //receive a list of men in room
                parserInRoomNameList(message);
            }
        }

        //parser online people list
        if(message.get("msg").equals("9")){
            parserOnlineNameList(message);
        }

        //10, update information about room list
        if (message.get("msg").equals("10"))
            parserOnlineRoom(message);

        if(message.get("msg").equals("11")){
            Notice warning=new Notice(message.get("text").toString());
            warning.getFrame().setVisible(true);
        }

        //### About game

        //check can game begin
        if (message.get("msg").equals("001")){
            if (message.containsKey("permission")){
                myself.getGui2().getTextArea_2().append("You need more player.\n");
            }else {
                myself.setGame(message);
            }
        }

        //for sending judge requisition
        if (message.get("msg").equals("002")){
            String str=message.get("word1").toString()+"\t"+message.get("word2").toString();
            myself.getGame().judgement(str);
        }

        //003, for update chessboard
        if (message.get("msg").equals("003")){
            myself.getGame().updateChessBord(message.get("p").toString()
                    +"\t"+message.get("q").toString()+"\t"+message.get("s"));
        }

        //005, for pushing game to next turn
        if (message.get("msg").equals("005")){
            myself.myTurn();
        }

        //for update mark
        if (message.get("msg").equals("006")){
            String m1=message.get("name").toString()+"\t"+message.get("marks").toString();
            //System.out.println(m1);
            myself.getGame().updateMark(m1);
        }

        //All clients pass or only one left (directly disconnect from server), in game state
        if (message.get("msg").equals("007")){
            //from chessboard to hall
            if (myself.getGame()!=null){
                myself.getGame().chessbord.dispose();
            }
            myself.getGui2().getFrame().setVisible(true);
            myself.getGui2().state1();
            myself.getGui2().getTextArea_2().append("Game Over.\n");
            //get new info
            parserOnlineNameList(message);
            parserOnlineRoom(message);
        }

        //only for getting new online client list and online room list
        if (message.get("msg").equals("008")){
            parserOnlineNameList(message);
            parserOnlineRoom(message);
        }

        //someone in game quit
        if(message.get("msg").equals("009")){
            Notice6 notice6=new Notice6(message.get("deserter").toString());
            notice6.getFrame().setVisible(true);
        }
    }

    public void parserInRoomNameList(JSONObject message){
        myself.getGui2().getTextArea_1().setText(null);
        myself.getGui2().getTextArea_1().append("People in room:\n");
        for (Object name: message.keySet()) {
            if (message.get(name).equals("inRoom"))
                myself.getGui2().getTextArea_1().append(name+"\n");
        }
    }

    public void parserOnlineNameList(JSONObject message){
        myself.getGui2().getTextArea().setText(null);
        myself.getGui2().getTextArea().append("People online:\n");
        for (Object name: message.keySet()) {
            if (message.get(name).equals("online") || message.get(name).equals("inRoom"))
                myself.getGui2().getTextArea().append(name+"\n");
        }
    }

    public void parserOnlineRoom(JSONObject message){
        myself.getGui2().getTextArea_1().setText(null);
        myself.getGui2().getTextArea_1().append("Room list:\n");
        for (Object name: message.keySet()) {
            if (message.get(name).equals("roomName"))
                myself.getGui2().getTextArea_1().append(name+"\n");
        }
    }
}

////5, exit
//        if(message.get("msg").equals("5")){
//            myself.closeSocket();
//        }

