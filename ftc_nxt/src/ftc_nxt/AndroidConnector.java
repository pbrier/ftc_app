package ftc_nxt;

import java.io.DataInputStream;
import java.io.IOException;

import lejos.nxt.LCD;
import lejos.nxt.comm.USB;
import lejos.nxt.comm.USBConnection;


class Gamepad {
	double runtime;
	 
	 public float left_stick_x;
	 public float left_stick_y;
	 public float right_stick_x;
	 public float right_stick_y;
	 public boolean dpad_up;
	 public boolean dpad_down;
	 public boolean dpad_left;
	 public boolean dpad_right;
	 public boolean a;
	 public boolean b;
	 public boolean x;
	 public boolean y;
	 public boolean guide;
	 public boolean start;
	 public boolean back;
	 public boolean left_bumper;
	 public boolean right_bumper;
	 public float left_trigger;
	 public float right_trigger;
}

/**
 * USB server running on NXT block. Receives packets from Android app with data or commands.
 */
class AndroidConnector extends Thread {

	private USBConnection conn;
	private DataInputStream dIn;
	private int lastCommand;
	private float lastData;
	public Gamepad gamepad;
	public int state;
	private boolean isConnected;
	
	public AndroidConnector() {
		gamepad = new Gamepad();
		gamepad.left_stick_x = 0;
		gamepad.left_stick_y = 0;
		gamepad.right_stick_x = 0;
		gamepad.right_stick_y = 0;
		gamepad.runtime = 0;
		gamepad.left_trigger = 0;
		gamepad.right_trigger = 0;
		gamepad.left_bumper = false;
		gamepad.right_bumper = false;
		state = 0; 
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
		lastCommand = dIn.readByte(); 		
		lastData = dIn.readFloat();
		// Decode the command: TODO: put all in one packet
		switch (lastCommand)
		{
		case 1: gamepad.runtime = lastData; break;
		case 2: gamepad.left_stick_y = lastData; break;
		case 3: gamepad.right_stick_y = lastData; break;
		case 4: gamepad.left_trigger = lastData; break;
		case 5: gamepad.right_trigger = lastData; break;
		case 6: gamepad.left_bumper = (lastData > 0.0 ? true : false); break;
		case 7: gamepad.right_bumper = (lastData > 0.0 ? true : false); break;
		case 255: state = (lastData > 0.0 ? 1 : 0);
		}
	}
	
	/**
	 * Executes on Usb client connected event. Opens all resources and changes 
	 * server status to disconnected. Performs reading of coming data.
	 */
	private void onConnected() {
		isConnected = true;
		LCD.clear();
		LCD.drawString("Connected to Android", 0, 0);
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
		LCD.drawString("Wait for Android...", 0, 0);		
	 	conn = USB.waitForConnection();
	 	onConnected();
	}
	
}