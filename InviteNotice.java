import org.json.simple.JSONObject;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class InviteNotice {

	private JFrame frame;

	private Client myself;
	private JSONObject m1;

	public InviteNotice(JSONObject m1, Client myself) {
		this.m1=m1;
		this.myself=myself;
		initialize();
	}

	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.addWindowListener(new MyWindowListener2(myself,this));
		frame.getContentPane().setLayout(null);
		
		JButton btnAccept = new JButton("Accept");
		btnAccept.setBounds(49, 157, 107, 38);
		frame.getContentPane().add(btnAccept);
		
		JLabel lblNewLabel = new JLabel(m1.get("invitor").toString()+" from room "
				+m1.get("hostname").toString()+" invites you");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(68, 45, 288, 38);
		frame.getContentPane().add(lblNewLabel);

		btnAccept.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				myself.join(m1.get("hostname").toString());
				frame.dispose();
			}
		});
	}

	public JFrame getFrame(){
		return frame;
	}

	public JSONObject getM1(){
		return m1;
	}
}
