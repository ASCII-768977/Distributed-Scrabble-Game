import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
//for inviting refusal
class MyWindowListener2 extends WindowAdapter {

    private Client myself;
    private InviteNotice i1;

    MyWindowListener2(Client myself, InviteNotice i1){
        this.myself=myself;
        this.i1=i1;
    }

    public void windowClosing(WindowEvent e) {
        myself.refuse(i1.getM1().get("invitor").toString());
    }
}