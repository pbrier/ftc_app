package ftc_nxt;

import lejos.nxt.LCD;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTMotor;
import lejos.nxt.Motor;
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
	NXTMotor lego_m1 = new NXTMotor(MotorPort.A);
	NXTMotor lego_m2 = new NXTMotor(MotorPort.B);
	
	
	public FtcNxtController(AndroidConnector srv) {
		this.srv = srv;
		
		// Instantiate the factory and get a Motor and servo controller. We assume that there is one of 
		 // each daisy-chained.
		 cf = new TetrixControllerFactory(SensorPort.S1);
		 mc = cf.newMotorController();
		 sc = cf.newServoController();
		 
		 // Display the voltage from the motor controller
		 //System.out.println("v=" + mc.getVoltage());
		 
		 // Get an encoder motor instance. The physical motor (with encoder) is connected to the Motor 2 terminals on the controller
		 mot1 = mc.getEncoderMotor(TetrixMotorController.MOTOR_1);
		 mot2 = mc.getEncoderMotor(TetrixMotorController.MOTOR_2);

		 servo1 = sc.getServo(TetrixServoController.SERVO_4);
		 servo2 = sc.getServo(TetrixServoController.SERVO_5);
		 servo3 = sc.getServo(TetrixServoController.SERVO_6);
		 
		 servo1.setAngle(100);
		 servo2.setAngle(120);
		 servo3.setAngle(60);

		 mot1.setRegulate(false); // does not seem to work OK without Encoders
		 mot1.setPower(100);
		 mot1.flt();

		 mot2.flt();
		 mot2.setPower(100);
		 mot2.setRegulate(false); // does not seem to work OK without Encoders
		 

		 lego_m1.flt();
		 lego_m2.flt();
		 lego_m1.setPower(100);
		 lego_m2.setPower(100);	
	}
	
	/**
	 * Executes command server has received
	 * The gamepad class is updated in the background
	 */
	private void loop()
	{
		int p1,p2,t1,t2;
		//System.out.println("c=" +  cmd + " d=" + srv.getData() ) ;
		//System.out.println("v=" + mc.getVoltage());

		p1 = (int)(Math.abs(srv.gamepad.left_stick_y*100.0));
		p2 = (int)(Math.abs(srv.gamepad.right_stick_y*100.0));
		t1 = (int)(srv.gamepad.left_trigger*30.);
		t2 = (int)(srv.gamepad.right_trigger*30.0);
		
		LCD.drawString(" t: " + srv.gamepad.runtime + "              ", 0, 1);
		LCD.drawString("ly: " + p1 + "             ", 0, 2);
		LCD.drawString("ry: " + p2 + "             ", 0, 3);
		LCD.drawString("lt: " + t1 + "             ", 0, 4);
		LCD.drawString("rt: " + t2 + "             ", 0, 5);

		// set the Servos
		servo1.setAngle(100+t1);
		
		servo2.setAngle(120+t2);
		servo3.setAngle(60-t2);
		
		// Set the DC motors
		mot1.setPower( p1 );
		mot2.setPower( p2 ); 
	
		if ( srv.gamepad.left_stick_y > 0)
			mot1.forward();
		else
			mot1.backward();
	
		if ( srv.gamepad.right_stick_y > 0)
			mot2.backward();
		else
			mot2.forward();
		
		
		// Set the lego motors
		if (srv.gamepad.left_bumper ) 
		{
			lego_m1.forward();
			lego_m2.forward();
		} else if (srv.gamepad.right_bumper )
		{
			lego_m1.backward();
			lego_m2.backward();
		}
		else
		{
		  lego_m1.flt();
		  lego_m2.flt();
		}
		
	}

	/**
	 * Waits until connection between phone and NXT has been established.
	 * Then start execute commands coming from the client.
	 */
	public void control () {
		while (true) {
			try {
				Thread.sleep(1000);
				//waiting while connect
				if (srv.isConnected()) {
					while(true) {
						loop();				
					//	Thread.sleep(10);
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