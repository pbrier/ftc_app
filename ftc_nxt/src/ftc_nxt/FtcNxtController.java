package ftc_nxt;

import lejos.nxt.LCD;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.addon.tetrix.*;


/**
 * Class performs moving control of FTC Robot
 */
class FtcNxtController  
{	
	AndroidConnector srv;
	TetrixMotorController mc;
	TetrixServoController sc;
	TetrixControllerFactory cf;
	TetrixEncoderMotor mot1, mot2; 
	TetrixServo servo1, servo2, servo3;
	
	public FtcNxtController(AndroidConnector srv) {
		this.srv = srv;
		
		// Instantiate the factory and get a Motor and servo controller. We assume that there is one of 
		 // each daisy-chained.
		 cf = new TetrixControllerFactory(SensorPort.S1);
		 mc = cf.newMotorController();
		 sc = cf.newServoController();
		 
		 // Display the voltage from the motor controller
		 System.out.println("v=" + mc.getVoltage());
		 
		 // Get an encoder motor instance. The physical motor (with encoder) is connected to the Motor 2 terminals on the controller
		 mot1 = mc.getEncoderMotor(TetrixMotorController.MOTOR_1);
		 mot2 = mc.getEncoderMotor(TetrixMotorController.MOTOR_2);
		 servo1 = sc.getServo(TetrixServoController.SERVO_4);
		 servo2 = sc.getServo(TetrixServoController.SERVO_5);
		 servo3 = sc.getServo(TetrixServoController.SERVO_6);
		 
		 servo1.setAngle(90);
		 servo2.setAngle(90);
		 servo3.setAngle(90);
		 
	}
	
	/**
	 * Executes last command server has received
	 */
	private void executeCommand(){
		int cmd = srv.pullLastCommand();
		LCD.drawInt( cmd , 0, 1);
		LCD.drawInt( (int)(100*srv.getData()) , 0, 2);
		System.out.println("v=" + mc.getVoltage());

		switch (cmd) {
		case 1:
			//	pilot.rotate(10);
			mot1.forward();
			mot2.forward();
				break;
		case 2:
		//	pilot.rotate(10);
			break;
		case 3:
		//	pilot.forward();
			break;
		case 4:
		//	pilot.backward();
			break;
		case 5:
		//	pilot.stop();
			break;
		default:
			break;
		}
	}

	/**
	 * Waits until connection between phone and NXT has been established.
	 * Than creates SegowayPilot and starts execute commands coming from the client.
	 */
	public void control () {
		while (true) {
			try {
				Thread.sleep(1000);
				//waiting while connect
				if (srv.isConnected()) {
					//when connected create pilot instance
					// makePilot(srv.getGyroscope());
					while(true) {
						//then every second execute command if any
						executeCommand();
						Thread.sleep(1);
					}
				}
			}
			catch (Exception e) {
				LCD.drawString(e.toString(), 0, 1);
		  		try {
		  			Thread.sleep(1000);
		  		}
		  		catch (InterruptedException e1 ) {}
			}
		}
	}
}