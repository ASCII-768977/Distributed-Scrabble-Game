import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JLabel;

public class Login2 {

	private JFrame frame;
	private JTextField textField;
	private JButton btnLogin;
	
	private Client myself;
	private JLabel lblIp;
	private JTextField textField_1;
	private JTextField textField_2;

	public Login2(Client myself) {
		this.myself=myself;
		initialize();
	}

	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 644, 405);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		textField = new JTextField();
		textField.setBounds(10, 10, 608, 45);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		
		btnLogin = new JButton("Login");
		btnLogin.setBounds(257, 220, 116, 52);
		frame.getContentPane().add(btnLogin);
		
		lblIp = new JLabel("IP");
		lblIp.setBounds(10, 80, 54, 15);
		frame.getContentPane().add(lblIp);
		
		textField_1 = new JTextField();
		textField_1.setBounds(53, 65, 565, 45);
		frame.getContentPane().add(textField_1);
		textField_1.setColumns(10);
		
		JLabel lblPort = new JLabel("PORT");
		lblPort.setBounds(10, 135, 54, 15);
		frame.getContentPane().add(lblPort);
		
		textField_2 = new JTextField();
		textField_2.setBounds(53, 120, 565, 45);
		frame.getContentPane().add(textField_2);
		textField_2.setColumns(10);
		
		//for login
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (checkInput(textField.getText()) && checkInput(textField_1.getText()) && checkInput(textField_2.getText())){
					myself.setSocket(textField_1.getText(),Integer.parseInt(textField_2.getText()));
					myself.loginServer(textField.getText());
				}else{
					Notice4 notice4=new Notice4();
					notice4.getFrame().setVisible(true);
				}
			}
		});	
	}

	public JFrame getFrame(){
		return frame;
	}

	public boolean checkInput(String input){
		//input is null or contains any space
		if (input==null || input.indexOf(" ")!=-1 || input.equals("")){
			return false;
		}else {
			return true;
		}
	}
}