package lejos.nxt;

import java.io.IOException;

import lejos.nxt.remote.NXTCommand;
import lejos.pc.comm.NXTCommandConnector;

/**
 * The message class is used to send messages between NXT bricks.
 * @author BB
 *
 */
public class Inbox {
	private static final NXTCommand nxtCommand = NXTCommandConnector.getSingletonOpen();
		
	public static int sendMessage(byte [] message, int inbox) {
		try {
			return nxtCommand.messageWrite(message, (byte)inbox);
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
			return -1;
		}
	}
	
	public static byte [] receiveMessage(int remoteInbox, int localInbox, boolean remove) {
		try {
			return nxtCommand.messageRead((byte)remoteInbox, (byte)localInbox, remove);
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
			return null;
		}
	}	
}
