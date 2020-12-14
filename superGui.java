import org.json.simple.JSONObject;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.*;

public class superGui {

	private Client myself;

	private JFrame frame;
	private JTextField textField;
	private JTextArea textArea;
	private JTextArea textArea_1;
	private JTextArea textArea_2;
	private JScrollPane scrollPane;
	private JScrollPane scrollPane_1;
	private JScrollPane scrollPane_2;

	private JButton btnCreate;
	private JButton btnJoin;
	private JButton btnInvite;
	private JButton btnStart;
	private JButton btnQuit;

	public superGui(Client myself) {
		this.myself=myself;
		initialize();
	}

	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 848, 445);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addWindowListener(new MyWindowListener(myself));
		frame.getContentPane().setLayout(null);
		
		textArea = new JTextArea();
		scrollPane = new JScrollPane(textArea);
		textArea.setLineWrap(true);
		scrollPane.setViewportView(textArea);
		scrollPane.setBounds(10, 10, 419, 172);
		frame.getContentPane().add(scrollPane);
		//frame.getContentPane().add(textArea);
		
		textArea_1 = new JTextArea();
		scrollPane_1 = new JScrollPane(textArea_1);
		textArea_1.setLineWrap(true);
		scrollPane_1.setViewportView(textArea_1);
		scrollPane_1.setBounds(10, 213, 419, 172);
		frame.getContentPane().add(scrollPane_1);
		//frame.getContentPane().add(textArea_1);
		
		textArea_2 = new JTextArea();
		scrollPane_2 = new JScrollPane(textArea_2);
		textArea_2.setLineWrap(true);
		scrollPane_2.setViewportView(textArea_2);
		scrollPane_2.setBounds(439, 124, 383, 172);
		frame.getContentPane().add(scrollPane_2);
		//frame.getContentPane().add(textArea_2);
		
		textField = new JTextField();
		textField.setBounds(439, 11, 383, 33);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		
		btnCreate = new JButton("Create");
		btnCreate.setBounds(439, 54, 109, 33);
		frame.getContentPane().add(btnCreate);
		
		btnJoin = new JButton("Join");
		btnJoin.setBounds(580, 54, 109, 33);
		frame.getContentPane().add(btnJoin);
		
		btnInvite = new JButton("Invite");
		btnInvite.setBounds(713, 54, 109, 33);
		frame.getContentPane().add(btnInvite);
		
		btnStart = new JButton("Start");
		btnStart.setBounds(439, 352, 109, 33);
		frame.getContentPane().add(btnStart);
		
		btnQuit = new JButton("Quit");
		btnQuit.setBounds(713, 352, 109, 33);
		frame.getContentPane().add(btnQuit);

		//Button function
		btnCreate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				myself.createGame();
			}
		});

		btnJoin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(checkInput(textField.getText())){
					myself.join(textField.getText());
				} else {
					Notice4 notice4 = new Notice4();
					notice4.getFrame().setVisible(true);
				}
			}
		});

		btnInvite.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(checkInput(textField.getText())){
					myself.invite(textField.getText());
				} else {
					Notice4 notice4 = new Notice4();
					notice4.getFrame().setVisible(true);
				}
			}
		});

		btnQuit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				myself.back();
				state1();
			}
		});

		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				myself.startGame();
			}
		});
	}

	public void state1(){
		btnCreate.setVisible(true);
		btnJoin.setVisible(true);
		btnInvite.setVisible(false);
		btnStart.setVisible(false);
		btnQuit.setVisible(false);
	}

	public void state2(){
		btnInvite.setVisible(true);
		btnStart.setVisible(true);
		btnQuit.setVisible(true);
		btnCreate.setVisible(false);
		btnJoin.setVisible(false);
	}

	public JTextArea getTextArea(){
		return textArea;
	}

	public JTextArea getTextArea_1(){
		return textArea_1;
	}

	public JTextArea getTextArea_2(){
		return textArea_2;
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
