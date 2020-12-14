import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.SwingConstants;

public class Game {
	ArrayList<String> player = null;
	String username = null;
	Client myself = null;
	JFrame chessbord = null;
	JSplitPane hSplitPane = null;
	JPanel panel1 = new JPanel();
	JPanel panel2 = new JPanel();
	JButton[][] grids = new JButton[20][20];
	List<String> x2 = new ArrayList<String>();
	List<String> x3 = new ArrayList<String>();
	List<String> x4 = new ArrayList<String>();
	List<String> x5 = new ArrayList<String>();
	private String s;
	private StringTokenizer token = null;
	private int x, y;
	
	JButton btnQuit = null;
	JButton btnPass = null;
	JButton passInAction = null;
	JLabel lblScore = null;
	JLabel lblName = null;
	JLabel red = null;
	JLabel green = null;
	JLabel yellow = null;
	JLabel blue = null;
	JScrollPane scrollPane = null;
	JTextArea textArea = null;
	private int num;

	public Game(Client client, String playermessage, String user) {
		this.myself = client;
		this.username = user;
		token = new StringTokenizer(playermessage, "\t");
		num = token.countTokens();
		player = new ArrayList<String>();
		while (token.hasMoreTokens()) {
			player.add(token.nextToken());
			player.add("0");
		}

		chessBord();
		
	}

	private void chessBord() {
		chessbord = new JFrame();
		chessbord.setTitle("Scrabble");
		chessbord.setBounds(100, 100, 1300, 1000);
		chessbord.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		chessbord.addWindowListener(new MyWindowListener(myself));

		hSplitPane = new JSplitPane();
		hSplitPane.setDividerLocation(1000);
		chessbord.getContentPane().add(hSplitPane, BorderLayout.CENTER);
		hSplitPane.setLeftComponent(panel1);
		hSplitPane.setEnabled(false);

		panel1.setLayout(new GridLayout(20,20));
		
		for (int i = 0; i < grids.length; i++) {
			for (int j = 0; j < grids.length; j++) {
				int x = i;
				int y = j;
				grids[i][j] = new JButton("");
				grids[i][j].setFont(new Font("Arial",Font.PLAIN,20));
				grids[i][j].setOpaque(true);
				grids[i][j].setEnabled(false);
				panel1.add(grids[i][j]);

				grids[i][j].addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						JDialog jd = new JDialog();
						jd.setBounds(320, 180, 412, 165);
						jd.setTitle("Input Letter");
						jd.getContentPane().setLayout(null);

						JLabel label = new JLabel("Please enter a letter:");
						label.setBounds(39, 24, 162, 29);
						label.setFont(new Font("Lucida Grande", Font.PLAIN, 15));
						jd.getContentPane().add(label);

						JTextField textfield = new JTextField(40);
						textfield.setFont(new Font("Lucida Grande", Font.PLAIN, 15));
						textfield.setBounds(223, 20, 145, 38);
						jd.getContentPane().add(textfield);

						JButton button = new JButton("Confirm");
						button.setFont(new Font("Lucida Grande", Font.PLAIN, 15));
						button.setBounds(258, 83, 122, 38);
						jd.getContentPane().add(button);

						JLabel lblJustOneLetter = new JLabel("");
						lblJustOneLetter.setFont(new Font("Lucida Grande", Font.PLAIN, 15));
						lblJustOneLetter.setBounds(24, 93, 207, 16);
						jd.getContentPane().add(lblJustOneLetter);

						button.addActionListener(new ActionListener(){
							public void actionPerformed(ActionEvent arg0) {
								s = textfield.getText();
								if (s.length() > 1) {
									lblJustOneLetter.setText("Just one letter, please ^_^");
								}else if (!s.matches("[a-z]")){
									lblJustOneLetter.setText("Please input a letter^_^");
								}else if (s != null && s.equals("") == false) {
									grids[x][y].setText(s);
									grids[x][y].setForeground(Color.BLACK);
									grids[x][y].setEnabled(false);

									myself.sendLetter(x, y, s);
									jd.dispose();

									JDialog dialog_vote = new JDialog();
									dialog_vote.setBounds(320, 180, 350, 200);
									dialog_vote.getContentPane().setLayout(null);
									JLabel label_vote = new JLabel("Do you want a poll?");
									label_vote.setBounds(100, 35, 160, 40);
									JButton button_voteYes = new JButton("Yes");
									button_voteYes.setBounds(55, 100, 95, 45);
									button_voteYes.addActionListener(new ActionListener() {
										public void actionPerformed(ActionEvent arg0) {
											String message = x + "\t" + y;
											vote(message);
											for (int i = 0; i < 20; i ++) {
												for (int j = 0; j < 20; j++) {
													grids[i][j].setEnabled(false);
												}
											}
											passInAction.setVisible(false);
											btnPass.setVisible(true);
											myself.next(0);
											dialog_vote.dispose();
										}
									});
									JButton button_voteNo = new JButton("No");
									button_voteNo.setBounds(200, 100, 95, 45);
									button_voteNo.addActionListener(new ActionListener() {
										public void actionPerformed(ActionEvent arg0) {
											for (int i = 0; i < 20; i ++) {
												for (int j = 0; j < 20; j++) {
													grids[i][j].setEnabled(false);
												}
											}
											passInAction.setVisible(false);
											btnPass.setVisible(true);
											myself.next(0);
											dialog_vote.dispose();
										}
									});
									dialog_vote.addWindowListener(new WindowAdapter() {
										public void windowClosing(WindowEvent e) {
											myself.next(0);
										}
									});

									dialog_vote.getContentPane().add(label_vote);
									dialog_vote.getContentPane().add(button_voteYes);
									dialog_vote.getContentPane().add(button_voteNo);
									dialog_vote.setModal(true);
									dialog_vote.setVisible(true);
								}
							}
						});
						jd.setModal(true);
						jd.setVisible(true);
					}
				});
			}
		}
		
		// 8 red
				grids[0][0].setBackground(Color.RED);
				grids[0][19].setBackground(Color.RED);
				grids[19][0].setBackground(Color.RED);
				grids[19][19].setBackground(Color.RED);
				grids[8][8].setBackground(Color.RED);
				grids[11][8].setBackground(Color.RED);
				grids[8][11].setBackground(Color.RED);
				grids[11][11].setBackground(Color.RED);
				x5.add("(0,0)");
				x5.add("(0.19)");
				x5.add("(19,0)");
				x5.add("(19,19)");
				x5.add("(8,8)");
				x5.add("(11,8)");
				x5.add("(8,11)");
				x5.add("(11,11)");

				// 16 green
				for(int i = 8;i<12;i++) {
					grids[i][1].setBackground(Color.GREEN);
					x4.add("(" + i + "," + 1 + ")");
				}
				for(int i = 8;i<12;i++) {
					grids[i][18].setBackground(Color.GREEN);
					x4.add("(" + i + "," + 18 + ")");
				}
				for(int i = 8;i<12;i++) {
					grids[1][i].setBackground(Color.GREEN);
					x4.add("(" + 1 + "," + i + ")");
				}
				for(int i = 8;i<12;i++) {
					grids[18][i].setBackground(Color.GREEN);
					x4.add("(" + 18 + "," + i + ")");
				}
				
				// 28 yellow
				for (int i = 1; i < 8; i++) {
					grids[i][i].setBackground(Color.YELLOW);
					x3.add("(" + i + "," + i + ")");
				}
				for (int i = 12; i < 19; i++) {
					grids[i][i].setBackground(Color.YELLOW);
					x3.add("(" + i + "," + i + ")");
				}
				for (int i = 1; i < 8; i++) {
					int j = 19 - i;
					grids[i][19 - i].setBackground(Color.YELLOW);
					x3.add("(" + i + "," + j + ")");
				}
				for (int i = 18; i > 11; i--) {
					int j = 19 - i;
					grids[i][19 - i].setBackground(Color.YELLOW);
					x3.add("(" + i + "," + j + ")");
				}

				// 40 blue
				for (int i = 5; i > 0; i--) {
					int j = 15 - i;
					grids[i][15 - i].setBackground(Color.BLUE);
					x2.add("(" + i + "," + j + ")");
				}
				for (int i = 1; i < 6; i++) {
					int j = i + 4;
					grids[i][i + 4].setBackground(Color.BLUE);
					x2.add("(" + i + "," + j + ")");
				}
				for (int i = 5; i < 10; i++) {
					int j = i - 4;
					grids[i][i - 4].setBackground(Color.BLUE);
					x2.add("(" + i + "," + j + ")");
				}
				for (int i = 14; i > 9; i--) {
					int j = 15 - i;
					grids[i][15 - i].setBackground(Color.BLUE);
					x2.add("(" + i + "," + j + ")");
				}
				for(int i = 14;i<19;i++) {
					int j = 23 - i;
					grids[i][23 - i].setBackground(Color.BLUE);
					x2.add("(" + i + "," + j + ")");
				}
				for(int i = 14;i<19;i++) {
					int j = i-4;
					grids[i][i-4].setBackground(Color.BLUE);
					x2.add("(" + i + "," + j + ")");
				}
				for(int i = 10;i<15;i++) {
					int j = i+4;
					grids[i][i+4].setBackground(Color.BLUE);
					x2.add("(" + i + "," + j + ")");
				}
				for(int i = 9;i>4;i--) {
					int j = 23-i;
					grids[i][23-i].setBackground(Color.BLUE);
					x2.add("(" + i + "," + j + ")");
				}
		
		hSplitPane.setRightComponent(panel2);
		hSplitPane.setEnabled(false);
		panel2.setLayout(null);
		
		red = new JLabel("Red is x5");
		red.setBounds(110, 600, 150, 35);
		red.setForeground(Color.RED);
		red.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
		panel2.add(red);
		
		green = new JLabel("Green is x4");
		green.setBounds(110, 650, 150, 35);
		green.setForeground(Color.GREEN);
		green.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
		panel2.add(green);
		
		yellow = new JLabel("Yellow is x3");
		yellow.setBounds(110, 700, 150, 35);
		yellow.setForeground(Color.YELLOW);
		yellow.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
		panel2.add(yellow);
		
		blue = new JLabel("Blue is x2");
		blue.setBounds(110, 750, 150, 35);
		blue.setForeground(Color.BLUE);
		blue.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
		panel2.add(blue);

		btnQuit = new JButton("Quit");
		btnQuit.setBounds(100, 550, 110, 35);
		panel2.add(btnQuit);
		
		btnPass = new JButton("Pass");
		btnPass.setBounds(95, 418, 117, 46);
		panel2.add(btnPass);
		
		passInAction = new JButton("Pass");
		passInAction.setBounds(95, 418, 117, 46);
		panel2.add(passInAction);
		passInAction.setVisible(false);

		passInAction.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				JDialog dialog2 = new JDialog();
				dialog2.setBounds(320, 180, 350, 200);
				dialog2.getContentPane().setLayout(null);
				JLabel label2 = new JLabel("Do you want to pass?");
				label2.setBounds(100, 35, 160, 40);
				JButton yes = new JButton("Yes");
				yes.setBounds(55,100,95, 45);
				yes.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						for (int i = 0; i < 20; i ++) {
							for (int j = 0; j < 20; j++) {
								grids[i][j].setEnabled(false);	
							}
						}
						passInAction.setVisible(false);
						btnPass.setVisible(true);
						if(num==1){
							myself.backToHall();
						}else {
							myself.next(1);
						}
						dialog2.dispose();
					}
				});

				JButton no = new JButton("No");
				no.setBounds(200, 100, 95, 45);
				no.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						dialog2.dispose();
					}
				});
				dialog2.getContentPane().add(label2);
				dialog2.getContentPane().add(yes);
				dialog2.getContentPane().add(no);
				dialog2.setModal(true);
				dialog2.setVisible(true);
			}
		});
		
		lblScore = new JLabel("Score:");
		lblScore.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
		lblScore.setBounds(22, 36, 77, 39);
		panel2.add(lblScore);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(47, 87, 201, 247);
		panel2.add(scrollPane);
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		int size = player.size();
		for (int i = 0; i < size; i += 2) {
			String scorelist = player.get(i) + ": " + player.get(i + 1) + "\n";
			textArea.append(scorelist);
		}
		scrollPane.setViewportView(textArea);
		
		lblName = new JLabel(username);
		lblName.setHorizontalAlignment(SwingConstants.CENTER);
		lblName.setBounds(112, 21, 54, 20);
		lblName.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
		panel2.add(lblName);
		
		btnPass.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				JDialog dialog1 = new JDialog();
				dialog1.setBounds(320, 180, 350, 200);
				dialog1.getContentPane().setLayout(null);
				JLabel label1 = new JLabel("It's not your turn.");
				label1.setBounds(100, 35, 160, 40);
				JButton button1 = new JButton("Yes");
				button1.setBounds(127, 100, 95, 45);
				button1.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						dialog1.dispose();
					}
				});
				dialog1.getContentPane().add(label1);
				dialog1.getContentPane().add(button1);
				dialog1.setModal(true);
				dialog1.setVisible(true);
			}
		});
		
		btnQuit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				JDialog dialog1 = new JDialog();
				dialog1.setBounds(320, 180, 250, 100);
				dialog1.getContentPane().setLayout(null);
				JLabel label1 = new JLabel("Are you sure to exit?");
				label1.setBounds(50, 10, 150, 39);
				JButton quitYes = new JButton("Yes");
				quitYes.setBounds(50,50,50,20);
				quitYes.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						//from game state back to hall
						myself.backToHall();
						dialog1.dispose();
					}
				});
				JButton quitNo = new JButton("No");
				quitNo.setBounds(150,50,50,20);
				quitNo.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						dialog1.dispose();
					}
				});
				dialog1.getContentPane().add(label1);
				dialog1.getContentPane().add(quitYes);
				dialog1.getContentPane().add(quitNo);
				dialog1.setModal(true);
				dialog1.setVisible(true);
			}
		});
		
		chessbord.setVisible(true);
	}
	
	//
	public void addLetter() {
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < 20; i++) {
			for (int j = 0; j < 20; j++) {
				if (!grids[i][j].getText().equals("")) {
					String coordinate = "(" + i + "," + j + ")";
					list.add(coordinate);
				}
			}
		}
		
		pass(); //
		
		if (list.size() == 0) {
			for (int i = 0; i < 20; i++) {
				for (int j = 0; j < 20; j++) {
					x = i;
					y = j;
					grids[i][j].setEnabled(true);
				}
			}
		} else {
			for (int i = 0; i < 20; i++) {
				for (int j = 0; j < 20; j++) {
					final int p = i;
					final int q = j;
					String position = "(" + i + "," + j + ")";
					if (!list.contains(position)) {
						String neighbour1 = "(" + (i + 1) + "," + j + ")";
						String neighbour2 = "(" + (i - 1) + "," + j + ")";
						String neighbour3 = "(" + i + "," + (j + 1) + ")";
						String neighbour4 = "(" + i + "," + (j - 1) + ")";
						if (list.contains(neighbour1) || list.contains(neighbour2) || list.contains(neighbour3) || list.contains(neighbour4)) {
							grids[p][q].setEnabled(true);
						}
					}
				}
			}
		}
	}
	
	//
	public void vote(String message) {
		token = new StringTokenizer(message, "\t");
		int x = Integer.parseInt(token.nextToken());
		int y = Integer.parseInt(token.nextToken());
		String word1 = grids[x][y].getText();
		int twofold1 = 0;
		int threefold1 = 0;
		int fourfold1 = 0;
		int fivefold1 = 0;
		int twofold2 = 0;
		int threefold2 = 0;
		int fourfold2 = 0;
		int fivefold2 = 0;
		
		if (x2.contains("(" + x + "," + y + ")")) {
			twofold1 += 1;
			twofold2 += 1;
		}
		if (x3.contains("(" + x + "," + y + ")")) {
			threefold1 += 1;
			threefold2 += 1;
		}
		if (x4.contains("(" + x + "," + y + ")")) {
			fourfold1 += 1;
			fourfold2 += 1;
		}
		if (x5.contains("(" + x + "," + y + ")")) {
			fivefold1 += 1;
			fivefold2 += 1;
		}
		
		for (int i = x - 1; i >= 0; i--) {
			if (!grids[i][y].getText().equals("")) {
				word1 = grids[i][y].getText() + word1;
			} else {
				break;
			}
		}

		for (int i = x + 1; i < 20; i++) {
			if (!grids[i][y].getText().equals("")) {
				word1 = word1 + grids[i][y].getText();
			} else {
				break;
			}
		}

		String word2 = grids[x][y].getText();
		
		for (int i = y - 1; i >= 0; i--) {
			if (!grids[x][i].getText().equals("")) {
				word2 = grids[x][i].getText() + word2;
			} else {
				break;
			}
		}
		for (int i = y + 1; i < 20; i++) {
			if (!grids[x][i].getText().equals("")) {
				word2 = word2 + grids[x][i].getText();
			} else {
				break;
			}
		}

		int mark1 = (int) (word1.length() * Math.pow(2, twofold1) * Math.pow(3, threefold1) * Math.pow(4, fourfold1)
				* Math.pow(5, fivefold1));
		int mark2 = (int) (word2.length() * Math.pow(2, twofold2) * Math.pow(3, threefold2) * Math.pow(4, fourfold2)
				* Math.pow(5, fivefold2));

		myself.askVote(word1,word2,mark1,mark2);
	}
	
	//
	public void judgement(String message) {
		token = new StringTokenizer(message, "\t");
		String word_1 = token.nextToken();
		String word_2 = token.nextToken();
		
		//
		JDialog dialog_1 = new JDialog();
		dialog_1.setBounds(320, 180, 400, 200);
		dialog_1.getContentPane().setLayout(null);
		
		JLabel label_1 = new JLabel("Do you think " + word_1 + " is a word?");
		label_1.setBounds(100, 35, 300, 40);
		JButton yes_1 = new JButton("Yes");
		yes_1.setBounds(80,100,95,45);
		yes_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dialog_1.dispose();
				myself.agreeOrNot(word_1,"agree");
			}
		});
		JButton no_1 = new JButton("No");
		no_1.setBounds(225, 100, 95, 45);
		no_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dialog_1.dispose();
				myself.agreeOrNot(word_1,"disagree");
			}
		});
		dialog_1.getContentPane().add(label_1);
		dialog_1.getContentPane().add(yes_1);
		dialog_1.getContentPane().add(no_1);
		dialog_1.setModal(true);
		dialog_1.setVisible(true);
		
		dialog_1.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				myself.agreeOrNot(word_1,"agree");
			}
		});

		JDialog dialog_2 = new JDialog();
		dialog_2.setBounds(320, 180, 400, 200);
		dialog_2.getContentPane().setLayout(null);
		
		JLabel label_2 = new JLabel("Do you think " + word_2 + " is a word?");
		label_2.setBounds(100, 35, 300, 40);
		JButton yes_2 = new JButton("Yes");
		yes_2.setBounds(80,100,95,45);
		yes_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dialog_2.dispose();
				myself.agreeOrNot(word_2,"agree");
			}
		});
		JButton no_2 = new JButton("No");
		no_2.setBounds(225, 100, 95, 45);
		no_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dialog_2.dispose();
				myself.agreeOrNot(word_2,"disagree");
			}
		});
		dialog_2.getContentPane().add(label_2);
		dialog_2.getContentPane().add(yes_2);
		dialog_2.getContentPane().add(no_2);
		dialog_2.setModal(true);
		dialog_2.setVisible(true);
		
		dialog_2.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				myself.agreeOrNot(word_2,"agree");
			}
		});
	}

	public void pass() {
		btnPass.setVisible(false);
		passInAction.setVisible(true);
	}

	//
	public void updateMark(String message) {
		token = new StringTokenizer(message, "\t");
		int index = player.indexOf(token.nextToken());
		int markPlus = Integer.parseInt(token.nextToken()) + Integer.parseInt(player.get(index + 1));
		String mark = "" + markPlus;
		player.set(index + 1, mark);
		int size = player.size();
		textArea.setText("");
		for (int i = 0; i < size; i += 2) {
			String scorelist = player.get(i) + ": " + player.get(i + 1) + "\n";
			textArea.append(scorelist);
		}
	}
	
	public void updateChessBord(String message) {
		token = new StringTokenizer(message, "\t");
		int x = Integer.parseInt(token.nextToken());
		int y = Integer.parseInt(token.nextToken());
		grids[x][y].setText(token.nextToken());
	}

	public JFrame getFrame(){
		return chessbord;
	}
}
