import org.json.simple.JSONObject;
import java.io.*;
import java.net.MalformedURLException;
import java.net.Socket;
import java.applet.*;

public class Client {
    private Socket socket=null;
    private DataInputStream reader = null;
    private DataOutputStream writer = null;
    private boolean flag1=true;
    private Client myself = null;

    private Login2 gui1=null;
    private superGui gui2=null;

    private Game game=null;

    public static void main(String[] args) {
        try {
            //1
            Client myself=new Client();
            myself.myself=myself;
            //2
            myself.gui1=new Login2(myself);
            myself.gui1.getFrame().setVisible(true);
            //3
            while (myself.flag1);
            //4
            myself.closeSocket();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setSocket(String IP, int port){
        try {
            socket = new Socket(IP, port);
            reader = new DataInputStream(socket.getInputStream());
            writer = new DataOutputStream(socket.getOutputStream());
            MessageListener ml = new MessageListener(myself);
            ml.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeSocket(){
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createGame(){
        JSONObject message=new JSONObject();
        message.put("msg","4");
        try {
            myself.getWriter().writeUTF(message.toString());
            myself.getWriter().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void invite(String username){
        JSONObject message=new JSONObject();
        message.put("msg","1");
        message.put("username",username);
        try {
            writer.writeUTF(message.toString());
            writer.flush();
        } catch (IOException e) {
            System.out.println("1 wrong");
        }
    }

    public void refuse(String hostname){
        JSONObject message=new JSONObject();
        message.put("msg","3");
        message.put("hostname",hostname);
        try {
            writer.writeUTF(message.toString());
            writer.flush();
        } catch (IOException e) {
            System.out.println("IOE.");
        }
    }

    //Ask server to directly join a room
    public void join(String room){
        JSONObject message=new JSONObject();
        message.put("msg","8");
        message.put("hostname",room);
        try {
            writer.writeUTF(message.toString());
            writer.flush();
        } catch (IOException e) {
        }
    }

    public void loginServer(String name){
        JSONObject msg=new JSONObject();
        msg.put("msg","6");
        msg.put("name",name);
        try {
            writer.writeUTF(msg.toString());
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public DataInputStream getReader(){
        return reader;
    }

    public DataOutputStream getWriter(){
        return writer;
    }

    public void back() {
        JSONObject m1 = new JSONObject();
        m1.put("msg", "7");
        try {
            writer.writeUTF(m1.toString());
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //### About GUI ###
    public Login2 getGui1(){
        return gui1;
    }

    public superGui getGui2(){
        return gui2;
    }

    public void gui2(Client myself){
        gui2=new superGui(myself);
        gui2.state1();
        gui2.getFrame().setVisible(true);
    }

    //### About game ###
    public Game getGame(){
        return game;
    }

    //Ask server to start game
    public void startGame(){
        //voice controls

        JSONObject m1=new JSONObject();
        m1.put("msg","001");
        try {
            writer.writeUTF(m1.toString());
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setGame(JSONObject m1){
        String nameList=null;
        for (Object name:m1.keySet()) {
            if (m1.get(name).equals("inRoom") && nameList==null)
                nameList=name+"\t";
            else if (m1.get(name).equals("inRoom"))
                nameList = nameList + name+"\t";
        }
        gui2.getFrame().dispose();
        game=new Game(myself,nameList,m1.get("nameOfMyself").toString());
    }

    public void myTurn(){
        game.addLetter();
    }

    //send letter
    public void sendLetter(int p,int q, String s){
        JSONObject m1=new JSONObject();
        m1.put("msg","003");
        m1.put("p",p);
        m1.put("q",q);
        m1.put("s",s);
        try {
            writer.writeUTF(m1.toString());
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //push to next turn
    public void next(int increasement){
        JSONObject m1=new JSONObject();
        m1.put("msg","005");
        m1.put("increasement",increasement);
        try {
            writer.writeUTF(m1.toString());
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //ask vote
    public void askVote(String word1, String word2, int mark1, int mark2){
        JSONObject m1=new JSONObject();
        m1.put("msg","002");
        m1.put("word1",word1);
        m1.put("word2",word2);
        m1.put("mark1",mark1);
        m1.put("mark2",mark2);
        try {
            writer.writeUTF(m1.toString());
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //agree word
    public void agreeOrNot(String word,String result){
        JSONObject m1=new JSONObject();
        m1.put("msg","004");
        m1.put("word",word);
        m1.put("result",result);
        try {
            writer.writeUTF(m1.toString());
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //from game state back to hall
    public void backToHall(){
        JSONObject m1=new JSONObject();
        m1.put("msg","007");
        try {
            writer.writeUTF(m1.toString());
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

//public void joinGame(){
//        JSONObject message=new JSONObject();
//        message.put("msg","2");
//        try {
//            writer.writeUTF(message.toString());
//            writer.flush();
//        } catch (IOException e) {
//            System.out.println("IOE.");
//        }
//    }

//public void judgeWord(String word1,String word2){
//        JSONObject m1=new JSONObject();
//        m1.put("msg","002");
//        m1.put("word1",word1);
//        m1.put("word2",word2);
//        try {
//            writer.writeUTF(m1.toString());
//            writer.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

//try {
//            File f = new File("d:\\op.wav");
//            JavaClip frame=new JavaClip();
//            AudioClip aau = Applet.newAudioClip(f.toURL());
//            aau.play();
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }

//public void setFlag1(boolean flag1){
//this.flag1=flag1;
//}