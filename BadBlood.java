import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import javax.swing.Action;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.ImageIcon;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import javax.swing.Timer;
import java.io.File;
import javax.imageio.ImageIO;
import java.util.LinkedList;
import java.awt.Insets;
/**
 * 
 * credits: Joseph Anthony C. Hermocilla
 * http://gamedev.stackexchange.com/questions/53705/how-can-i-make-a-sprite-sheet-based-animation-system
 *
 */


public class BadBlood extends JPanel implements Runnable, Constants{
	JFrame frame= new JFrame();
		
	/**
	 * Game timer, handler receives data from server to update game state
	 */
	Thread t=new Thread(this);
	ImageIcon imageIcon;
	JTextField nameField = new JTextField(10);
	LinkedList<Troop> deployedTroops = new LinkedList<Troop>();
	String selectedTroop = "a";
	int userID; //Player ID
	String name=""; //Player name
	boolean connected=false, connecting=false; //Flag to indicate whether this player has connected or not
	JPanel left_panel;
	int hp[] = new int[NUMBER_OF_PLAYERS];
	int x1,y1;
	int archerCount = 0, barbarianCount = 0, horsemanCount = 0;
	JLabel archerCountLabel = new JLabel(Integer.toString(archerCount));
	JLabel barbarianCountLabel = new JLabel(Integer.toString(barbarianCount));
	JLabel horsemanCountLabel = new JLabel(Integer.toString(horsemanCount));
	BufferedImage myPicture, castle[] = new BufferedImage[3];
	JButton doneButton = new JButton("DONE");
	JTextField chatField = new JTextField(100);
	JTextArea chat = new JTextArea();
	JTextPane chatText = new JTextPane();
	JScrollPane chatPane = new JScrollPane(chat);
	
	Timer trepaint = new Timer(500, new ActionListener() { //timer for repainting frame
		public void actionPerformed(ActionEvent e) {
		    frame.getContentPane().repaint();
			frame.repaint();
			frame.revalidate();	
		}
	});
	
	DatagramSocket socket = new DatagramSocket();
	String server="localhost"; //Server to connect to
	String serverData;
	
	public BadBlood(String server) throws Exception{
		this.server=server;
		this.name=name;
		
		frame.setTitle(APP_NAME+":"+name);
		socket.setSoTimeout(100);
		
		//prepare UI:
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1150, 720);
		frame.setVisible(true);
        	frame.setResizable(false);
		
		//start repaint timer:
		t.start();		
	}
	
	/**
	 * Helper method for sending data to server
	 * @param msg
	 */
	public void send(String msg){
		try{
			byte[] buf = msg.getBytes();
			InetAddress address = InetAddress.getByName(server);
			DatagramPacket packet = new DatagramPacket(buf, buf.length, address, MIDDLEPORT);
			socket.send(packet);
		}catch(Exception e){}
		
	}
	
	/**
	 * Function to check if the player has selected enough troops.
	 */
	public boolean isTroopComplete(){
		if(archerCount+barbarianCount+horsemanCount == MAX_TROOP)
			return true;
		return false;
	}
	
	/**
	 * Function to update labels.
	 */
	public void updateCountLabels(){
		archerCountLabel.setText(Integer.toString(archerCount));
		barbarianCountLabel.setText(Integer.toString(barbarianCount));
		horsemanCountLabel.setText(Integer.toString(horsemanCount));
		frame.getContentPane().repaint();
		frame.repaint();
		frame.revalidate();
		frame.repaint();
	}
	
	/**
	 * The main thread function.
	 */
	public void run(){
		while(true){
			try{
				Thread.sleep(1);
			}catch(Exception ioe){}
						
			//Get the data from players
			byte[] buf = new byte[512];
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			try{
			socket.receive(packet);
			}catch(Exception ioe){}
			
			serverData=new String(buf);
			serverData=serverData.trim();
			
			if (!connected && serverData.startsWith("CONNECTED")){
				connected=true;
				//get User ID from server data
				userID = Integer.parseInt(serverData.split(" ")[1].trim());
				System.out.println("Connected. userID = " + userID);

				//base midpoint - location of each castle
				if(userID == 0){
					x1 = 153;
					y1 = 308;

				}else if(userID == 1){
					 x1 = 596;
					 y1 = 168;

				}else if(userID == 2){
					x1 = 596;
					y1 = 523;
				}
				
			}else if (!connected && !connecting){
				
				JButton connectButton = new JButton();
				connectButton.setPreferredSize(new Dimension(75,75));
				connectButton.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e)
					{
						if(!nameField.getText().isEmpty()){
							((JButton) e.getSource()).setEnabled(false);
							connecting = true;
						}
					}
				});

				ImageIcon connectIcon = new ImageIcon("res/playIcon.png");
				Image connectImg = connectIcon.getImage() ;  
				Image connectNewimg = connectImg.getScaledInstance(80, 80,  java.awt.Image.SCALE_SMOOTH );  
				connectIcon = new ImageIcon(connectNewimg);
				connectButton.setIcon(connectIcon);
						
				frame.setLayout(new GridBagLayout());
				GridBagConstraints cons1 = new GridBagConstraints();
				frame.getContentPane().setBackground(Color.BLACK);
				JPanel center_panel = new JPanel();
				center_panel.setLayout(new GridBagLayout());
				center_panel.setBackground(Color.BLACK);
				GridBagConstraints cons = new GridBagConstraints();
				GridBagConstraints cons2 = new GridBagConstraints();

				JLabel titleLabel = new JLabel();
				titleLabel.setIcon(new ImageIcon("res/titleBanner.png"));
				cons.gridx = 2;
				cons.gridy = 0;
				center_panel.add(titleLabel,cons);

				cons2.fill = GridBagConstraints.BOTH;
				cons2.gridx = 2;
				cons2.gridy = 2;
				cons2.insets = new Insets(70,10,20,0);
				nameField.setPreferredSize(new Dimension(70, 50));
				nameField.setHorizontalAlignment(JTextField.CENTER);
				center_panel.add(nameField,cons2);
				
				cons.gridx = 2;
				cons.gridy = 3;
				center_panel.add(connectButton,cons);

				cons1.gridx = 2;
				cons1.gridy = 0;
				frame.getContentPane().add(center_panel,cons1);
				frame.getContentPane().repaint();
				frame.repaint();
				frame.revalidate();

				//wait until the player click the play icon button
				while(connectButton.isEnabled()){	
					try{
						Thread.sleep(1);
					}catch(Exception ioe){}
				}
				nameField.setEditable(false);
				name = nameField.getText();
				
			}else if (!connected && connecting){
				System.out.println("Connecting..");
				System.out.println(name);
				send("TOSERVER CONNECT "+ name);
			}else if (connected){
				if (serverData.startsWith("SELECT")){
					frame.getContentPane().removeAll();
					frame.getContentPane().repaint();
					frame.repaint();
					frame.revalidate();
					
					
					frame.setLayout(new BorderLayout());
					frame.getContentPane().setBackground(Color.BLACK);
					doneButton.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e)
						{
							if(isTroopComplete())
								((JButton) e.getSource()).setEnabled(false);
						}
					});
				
					JButton archerButtonMinus = new JButton();
					JButton barbarianButtonMinus = new JButton();
					JButton horsemanButtonMinus = new JButton();
				
					JButton archerMug = new JButton();
					JButton barbarianMug = new JButton();
					JButton horsemanMug = new JButton();
					
					JPanel center_panel1 = new JPanel();
					GridBagConstraints cons = new GridBagConstraints();

					JLabel titleLabel = new JLabel();
					titleLabel.setIcon(new ImageIcon("images_rpg/final/characters.png"));
					//cons.gridx = 2;
					//cons.gridy = 0;
					center_panel1.add(titleLabel,cons);

					archerMug.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e){
							if(!isTroopComplete()){
								archerCount++;
								updateCountLabels();
							}
					}});
					barbarianMug.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e){
							if(!isTroopComplete()){
								barbarianCount++;
								updateCountLabels();
							}
					}});
					horsemanMug.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e){
							if(!isTroopComplete()){
								horsemanCount++;
								updateCountLabels();
							}
					}});
					archerButtonMinus.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e){
							if(archerCount > 0){
								archerCount--;
								updateCountLabels();
							}
					}});
					barbarianButtonMinus.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e){
							if(barbarianCount > 0){
								barbarianCount--;
								updateCountLabels();
							}
					}});
					horsemanButtonMinus.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e){
							if(horsemanCount > 0){
								horsemanCount--;
								updateCountLabels();
							}
					}});
					JPanel centerPanel = new JPanel();
					JPanel archerPanel = new JPanel();
					JPanel barbarianPanel = new JPanel();
					JPanel horsemanPanel = new JPanel();
					JPanel archerBottomPanel = new JPanel();
					JPanel barbarianBottomPanel = new JPanel();
					JPanel horsemanBottomPanel = new JPanel();
					
					
					
					archerMug.setIcon(new ImageIcon("add.png"));
					barbarianMug.setIcon(new ImageIcon("add.png"));
					horsemanMug.setIcon(new ImageIcon("add.png"));

					archerButtonMinus.setIcon(new ImageIcon("minus.png"));
					barbarianButtonMinus.setIcon(new ImageIcon("minus.png"));
					horsemanButtonMinus.setIcon(new ImageIcon("minus.png"));
					
					archerBottomPanel.setLayout(new GridLayout(1,3));
					barbarianBottomPanel.setLayout(new GridLayout(1,3));
					horsemanBottomPanel.setLayout(new GridLayout(1,3));
					
					archerPanel.setLayout(new BorderLayout());
				//	archerPanel.add(new JLabel("ARCHERS"), BorderLayout.NORTH);
					archerPanel.add(archerMug, BorderLayout.SOUTH);
				//	archerBottomPanel.add(archerButtonAdd);
					archerBottomPanel.add(archerCountLabel);
					archerBottomPanel.add(archerButtonMinus);
					archerBottomPanel.add(archerMug);
					archerPanel.add(archerBottomPanel, BorderLayout.SOUTH);
					
					barbarianPanel.setLayout(new BorderLayout());
				//	barbarianPanel.add(new JLabel("BARBARIANS"), BorderLayout.NORTH);
					barbarianPanel.add(barbarianMug, BorderLayout.SOUTH);
				//	barbarianBottomPanel.add(barbarianButtonAdd);
					barbarianBottomPanel.add(barbarianCountLabel);
					barbarianBottomPanel.add(barbarianButtonMinus);
					barbarianBottomPanel.add(barbarianMug);
					barbarianPanel.add(barbarianBottomPanel, BorderLayout.SOUTH);
					
					horsemanPanel.setLayout(new BorderLayout());
				//	horsemanPanel.add(new JLabel("HORSEMEN"), BorderLayout.NORTH);
					horsemanPanel.add(horsemanMug, BorderLayout.SOUTH);
				//	horsemanBottomPanel.add(horsemanButtonAdd);
					horsemanBottomPanel.add(horsemanCountLabel);
					horsemanBottomPanel.add(horsemanButtonMinus);
					horsemanBottomPanel.add(horsemanMug);
					horsemanPanel.add(horsemanBottomPanel, BorderLayout.SOUTH);
					
					centerPanel.setLayout(new GridLayout(1,3));
					centerPanel.add(archerBottomPanel);
					centerPanel.add(barbarianBottomPanel);
					centerPanel.add(horsemanBottomPanel);
					
					frame.getContentPane().add(centerPanel, BorderLayout.SOUTH);
					frame.getContentPane().add(doneButton, BorderLayout.CENTER);
					frame.getContentPane().add(center_panel1,BorderLayout.NORTH);
					frame.getContentPane().repaint();
					frame.repaint();
					frame.revalidate(); 
					while(doneButton.isEnabled() || !isTroopComplete()){	
						try{
							Thread.sleep(100);
						}catch(Exception ioe){}
					}
					
					send("TOSERVER TROOPS "+ name);
					//archerButtonAdd.setEnabled(false);
					//barbarianButtonAdd.setEnabled(false);
					//horsemanButtonAdd.setEnabled(false);
					archerButtonMinus.setEnabled(false);
					barbarianButtonMinus.setEnabled(false);
					horsemanButtonMinus.setEnabled(false);
				}
				if (serverData.startsWith("START")){
					for(int i=0; i<NUMBER_OF_PLAYERS; i++)
						hp[i] = 2000; //initialize health points
					frame.getContentPane().removeAll();
					frame.getContentPane().repaint();
					frame.repaint();
					frame.revalidate();
					
					frame.setLayout(new GridBagLayout());
					GridBagConstraints c = new GridBagConstraints();
					
					myPicture = null;
					castle[0] = null;
					castle[1] = null;
					castle[2] = null;
					try {
						myPicture = ImageIO.read(new File("res/base_final.png"));
						castle[0] = ImageIO.read(new File("res/castle0.png"));
						castle[1] = ImageIO.read(new File("res/castle1.png"));
						castle[2] = ImageIO.read(new File("res/castle2.png"));
					} catch (IOException e) {
					}


					left_panel = new JPanel(){
					    @Override
					    protected void paintComponent(Graphics g) {
							super.paintComponent(g);
							g.drawImage(myPicture, 0, 0, null);
							for(int i=0; i<deployedTroops.size();i++){
								if(deployedTroops.get(i).isAlive()){
									g.drawImage(deployedTroops.get(i).getSprite(), deployedTroops.get(i).getX(), deployedTroops.get(i).getY(), null);
							}
							}
							if(hp[0] > 0){
								g.drawImage(castle[0], 153, 308, null);
        							g.drawString("HP: "+hp[0], 203, 508);

							}
							if(NUMBER_OF_PLAYERS>1){
								if(hp[1] > 0){
									g.drawImage(castle[1], 596, 168, null);
									g.drawString("HP: "+hp[1], 646, 368);
								}
							}
							if(NUMBER_OF_PLAYERS>2){
								if(hp[2] > 0){
									g.drawImage(castle[2], 596, 523, null);
									g.drawString("HP: "+hp[2], 646, 708);

								}
							}
						}
					};


					left_panel.setLayout(new GridBagLayout());


					imageIcon = new ImageIcon(myPicture); // load the image to a imageIcon
					Image image = imageIcon.getImage(); // transform it 
					Image newimg = image.getScaledInstance(1000, 689,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way  
					imageIcon = new ImageIcon(newimg); // transform it back

					JLabel picLabel = new JLabel(imageIcon);
					left_panel.setBackground(Color.BLACK);
					//left_panel.add(picLabel);
					left_panel.setPreferredSize(new Dimension(1000, 689));
					
					c.fill = GridBagConstraints.BOTH;
					c.gridx = 0;
					c.weightx=0.90;
					c.weighty=1.0;
					c.gridy = 0;
					left_panel.addMouseListener(new CustomMouseListener());

					frame.add(left_panel, c);
					//frame.add(transparent_panel,c);


					JPanel right_panel = new JPanel();
					right_panel.setLayout(new GridLayout(4,1));
					right_panel.setBackground(Color.BLACK);
					
					
					//Archer
					JButton label5 = new JButton();
					label5.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e){
							if(archerCount != 0){
								selectedTroop = "a";
								System.out.println("Will deploy an archer.");
							}
					}});
					ImageIcon icon5 = new ImageIcon("Archer.png");
					Image img5 = icon5.getImage() ;  
					Image newimg5 = img5.getScaledInstance(100, 100,  java.awt.Image.SCALE_SMOOTH );  
					icon5 = new ImageIcon(newimg5);
					label5.setIcon(icon5);

					right_panel.add(label5);

					//Horseman
					JButton label6 = new JButton();
					label6.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e){
							if(horsemanCount != 0){
								selectedTroop = "h";
								System.out.println("Will deploy a horseman.");
							}
					}});
					ImageIcon icon6 = new ImageIcon("archer_mug1.png");
					Image img6 = icon6.getImage() ;  
					Image newimg6 = img6.getScaledInstance(100, 100,  java.awt.Image.SCALE_SMOOTH );  
					icon6 = new ImageIcon(newimg6);
					label6.setIcon(icon6);

					right_panel.add(label6);
					
					//Barbarian
					JButton label7 = new JButton();
					label7.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e){
							if(barbarianCount != 0){
								selectedTroop = "b";
								System.out.println("Will deploy a barbarian.");
							}
					}});
					ImageIcon icon7 = new ImageIcon("Barbarian.png");
					Image img7 = icon7.getImage() ;  
					Image newimg7 = img7.getScaledInstance(100, 100,  java.awt.Image.SCALE_SMOOTH );  
					icon7 = new ImageIcon(newimg7);
					label7.setIcon(icon7);

					right_panel.add(label7);

					
					c.fill = GridBagConstraints.BOTH;
					c.gridx = 1;
					c.gridy = 0;
					c.weightx=0.10;
					c.weighty=1.0;
					
					JPanel chatPanel = new JPanel();
					Action chatEnter = new AbstractAction(){
						@Override
						public void actionPerformed(ActionEvent e){
							send("TOSERVER MESSAGE "+ name + " : " + chatField.getText());
							chatField.setText("");
						}
					};
					chatPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
					chatPanel.setLayout(new BorderLayout());
					chatPanel.add(chatPane, BorderLayout.CENTER);
					chatPanel.add(chatField, BorderLayout.SOUTH);
					chatField.addActionListener(chatEnter);
					chat.setText("CHATBOX\n");
					chatText.setEditable(false);
					chat.setLineWrap(true);
					chat.setEditable(false);
					right_panel.add(chatPanel);
					
					frame.add(right_panel, c);

					if(archerCount != 0){
						selectedTroop = "a";
					}
					else if(barbarianCount != 0){
						selectedTroop = "b";
					}
					else{
						selectedTroop = "c";
					}

					left_panel.repaint();
					frame.revalidate();
				}
				if (serverData.startsWith("MESSAGE")){
					chat.append("\n"+serverData.substring(7)+"\n");
					chat.setCaretPosition(chat.getText().length());
				}
				else if(serverData.startsWith("TROOP")){
					serverData = serverData.substring(6);
					String tokens[] = serverData.split(":");
					String health[] = tokens[0].split(" ");
					for(int i=0; i<hp.length; i++)
						hp[i] = Integer.parseInt(health[i].trim());
					deployedTroops = new LinkedList<Troop>();
					for(int i=1; i<tokens.length; i++){
						deployedTroops.add(new Troop("TROOP "+ tokens[i]));
					}
					left_panel.repaint();
				}
			}			
		}
	}

	class CustomMouseListener implements MouseListener{

		public void mouseClicked(MouseEvent e) {
			System.out.println("Mouse Clicked: ("+e.getX()+", "+e.getY() +")");

			if(selectedTroop=="a" && archerCount>0){
				send("TOSERVER DEPLOY "+userID+" "+selectedTroop+" "+e.getX()+" "+e.getY());
				archerCount--;
				if(archerCount==0){
					if(horsemanCount > 0) selectedTroop = "h";
					else if(barbarianCount > 0) selectedTroop = "b";
					else selectedTroop = "x"; //No more troop
				}
			}
			else if(selectedTroop=="h" && horsemanCount>0){
				send("TOSERVER DEPLOY "+userID+" "+selectedTroop+" "+e.getX()+" "+e.getY());
				horsemanCount--;
				if(horsemanCount==0){
					if(archerCount > 0) selectedTroop = "a";
					else if(barbarianCount > 0) selectedTroop = "b";
					else selectedTroop = "x"; //No more troop
				}
			}
			else if(selectedTroop=="b" && barbarianCount>0) {
				send("TOSERVER DEPLOY "+userID+" "+selectedTroop+" "+e.getX()+" "+e.getY());
				barbarianCount--;
				if(barbarianCount==0){
					if(archerCount > 0) selectedTroop = "a";
					else if(horsemanCount > 0) selectedTroop = "h";
					else selectedTroop = "x"; //No more troop
				}
			}
		}

		public void mousePressed(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}
	}
	
	
	public static void main(String args[]) throws Exception{
		if (args.length == 0)
			new BadBlood("localhost");
		else
			new BadBlood(args[0]);
	}
}
