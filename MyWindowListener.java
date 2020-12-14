import org.json.simple.JSONObject;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

class MyWindowListener extends WindowAdapter {

    private Client myself;

    MyWindowListener(Client myself){
        this.myself=myself;
    }

    public void windowClosing(WindowEvent e) {
        JSONObject message=new JSONObject();
        message.put("msg","5");
        try {
            myself.getWriter().writeUTF(message.toString());
            myself.getWriter().flush();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}