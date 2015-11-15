import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.awt.event.ActionListener;
import javax.swing.Timer;
import java.awt.event.ActionEvent;


public class LoadTester implements Runnable, Constants{

	DatagramSocket socket = null;
	Thread t = new Thread(this);
	String server="localhost";
	

	public LoadTester(String server){
		this.server = server;
		try {
            		socket = new DatagramSocket();
			socket.setSoTimeout(100);
		} catch (IOException e) {
			System.err.println("Could not connect to a port.");
			System.exit(-1);
		}catch(Exception e){}
		
		System.out.println("Load tester has started...");
		t.start();
	}
	
	/**
	 * Send a message to a player
	 * @param player
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

	public void sendToServer(String msg){
		try{
			DatagramPacket packet;	
			byte buf[] = msg.getBytes();		
			packet = new DatagramPacket(buf, buf.length, InetAddress.getByName(server), PORT);
			socket.send(packet);
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
	}
	
	/**
	 * The juicy part
	 */
	public void run(){
		while(true){
			send("RANDOM STUFF FROM LOAD TESTER");
			try {
				t.sleep(500);
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	
	public static void main(String args[]){
		if (args.length == 0)
			new LoadTester("localhost");
		else
			new LoadTester(args[0]);
	}
}

