package ftc_nxt;

import java.lang.String;

public class NxtControllerApp {
	
	/**
	 * Point of entry for NXT application.
	 * @throws InterruptedException
	 */
	public static void main(String [] args) throws InterruptedException {
		AndroidConnector s = new AndroidConnector();
		FtcNxtController c = new FtcNxtController(s);
		c.control();
	}

}
