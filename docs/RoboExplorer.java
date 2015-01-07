import java.io.DataOutputStream;
import java.io.IOException;

import lejos.robotics.subsumption.*;
import lejos.nxt.*;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;

public class RoboExplorer {

	public static void main(String[] args) {
		Motor.B.setSpeed(300);
		Motor.C.setSpeed(300);
		Behavior b1 = new DriveForward();
		Behavior b2 = new DetectWall();
		Behavior b3 = new Stop();
		Behavior[] behaviorList = { b1, b2, b3 };
		Arbitrator arbitrator = new Arbitrator(behaviorList);
		LCD.drawString("Robo-kurssin demo", 0, 1);
		Button.waitForPress();
		arbitrator.start();
	}
}

class DriveForward implements Behavior {

	private boolean _suppressed = false;

	public boolean takeControl() {
		return !Button.ESCAPE.isPressed();
	}

	public void suppress() {
		_suppressed = true;
	}

	public void action() {
		_suppressed = false;
		Motor.B.forward();
		Motor.C.forward();
		while (!_suppressed) {
			Thread.yield();
		}
		Motor.B.stop();
		Motor.C.stop();
	}
}

class DetectWall implements Behavior {
	int suunta = 0;

	public DetectWall() {
		touch = new TouchSensor(SensorPort.S1);
		sonar = new UltrasonicSensor(SensorPort.S4);
	}

	public boolean takeControl() {
		sonar.ping();
		return ((touch.isPressed() || sonar.getDistance() < 25) && !Button.ESCAPE
				.isPressed());
	}

	public void suppress() {
	}

	public void action() {
		suunta = suunta < 0 ? 180 : -180;
		Motor.A.setSpeed(100);
		Motor.A.rotateTo(suunta);
		Motor.A.stop();
		Sound.pause(30);
		SendSonarValues();
		Motor.B.rotate(-180, true);
		Motor.C.rotate(-360);
	}

	private TouchSensor touch;
	private UltrasonicSensor sonar;

	private void SendSonarValues() {
		try {
			int[] distances = new int[8];
			sonar.getDistances(distances);
//			NXTConnection connection = Bluetooth.waitForConnection(5000,
//					NXTConnection.PACKET);
			NXTConnection connection = Bluetooth.waitForConnection();
			if (connection == null) {
				drawSonarValuesToLCD(distances);
				return;
			}
			DataOutputStream dataOut = connection.openDataOutputStream();

			for (int i = 0; i < distances.length; i++) {
				dataOut.writeInt(distances[i]);
				dataOut.flush();
				System.out.println("i: " + distances[i]);
			}
			dataOut.close();
			connection.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	private void drawSonarValuesToLCD(int[] distances) {
		LCD.clear();
		for (int i = 0; i < distances.length; i++) {
			int merkki = 0;
			for (int j = 0; j < distances[i]; j++) {
				if (j % 10 == 0) {
					LCD.drawString("*", 4 + merkki, i);
					merkki++;
				}
			}
		}
		Button.waitForPress();
	}
}

class Stop implements Behavior {
	private boolean _supressed = false;

	public boolean takeControl() {
		return Button.ESCAPE.isPressed();
	}

	public void suppress() {
	}

	public void action() {
		_supressed = false;
		Motor.A.stop();
		Motor.B.stop();
		Motor.C.stop();
		System.exit(0);
	}
}
