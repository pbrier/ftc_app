package ftc_nxt;

import java.io.DataInputStream;
import java.io.IOException;

import lejos.nxt.LCD;
import lejos.nxt.comm.USB;
import lejos.nxt.comm.USBConnection;
import lejos.robotics.Gyroscope;

/**
 * USB server running on NXT block. Receives packets from Android app with data or commands.
 */
class AndroidConnector extends Thread {

	private USBConnection conn;
	private DataInputStream dIn;
	private int lastCommand;
	private float lastData;
	private boolean isConnected;
	
	public AndroidConnector() {
		this.setDaemon(true);
		this.start();	
	}


	/**
	 * Returns actual status of the server.
	 */	
	public boolean isConnected(){
		return isConnected;
	}
	
	/**
	 * Returns last command and forgets it.
	 */
	public int pullLastCommand(){
		int lc = lastCommand;
		lastCommand = 0;
		return lc; 
	}
	
	public float getData(){
		return  lastData;
	}
	
	/**
	 * Closes all resources on client disconnect.
	 */
	public void interrupt() {
		try {
		    dIn.close();
		    conn.close();
		}
		catch (IOException e) {}
		super.interrupt();
	}
	
	/**
	 * Reacts on usb client data. Updates state or remembers the command has been sent.
	 */
	private void readPacket() throws IOException {
		int header = dIn.readByte(); 		
		if (header == 1) {
			lastData = dIn.readFloat();
		}
		else if(header!=0) {
			lastCommand = header; 
			
		}
	}
	
	/**
	 * Executes on Usb client connected event. Opens all resources and changes 
	 * server status to disconnected. Performs reading of coming data.
	 */
	private void onConnected() {
		isConnected = true;
		LCD.clear();
		LCD.drawString("Connected", 0, 0);
		dIn = conn.openDataInputStream();
		while (true) 
		{
	        try {
	        	readPacket();
	        }
	        catch (IOException e) {
	        	onDisconnected();
	        	break;
	        }
		}
	
	}
	
	/**
	 * Executes on Usb client disconnected event. Closes all resources and changes 
	 * server status to disconnected. 
	 */
	private void onDisconnected() {
		isConnected = true;
		try {
			LCD.clear();
			LCD.drawString("Disconnected", 0, 0);
			Thread.sleep(2000);
			System.exit(0);
		} catch (InterruptedException e1) {
			LCD.clear();
			LCD.drawString("interrupted 1", 0, 0);
		}
		try {
			dIn.close();
			conn.close();
		} catch (IOException e) {
			LCD.clear();
			LCD.drawString("exc on disconnect", 0, 0);
		}
	}

	/**
	 * Binds usb connection for listening of connection. When connected starts receive data.
	 */
	public void run() {
		LCD.clear();
		LCD.drawString("Waiting for connection", 0, 0);
		conn = USB.waitForConnection();
		onConnected();
	}
	
}