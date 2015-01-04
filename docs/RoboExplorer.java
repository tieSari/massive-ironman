import lejos.robotics.subsumption.*;
import lejos.nxt.*;

public class RoboExplorer
{

  public static void main(String[] args)
  {
    Motor.B.setSpeed(300);
    Motor.C.setSpeed(300);
    Behavior b1 = new DriveForward();
    Behavior b2 = new DetectWall();
    Behavior b3 = new Stop();
    Behavior[] behaviorList =
    {
      b1, b2, b3
    };
    Arbitrator arbitrator = new Arbitrator(behaviorList);
    LCD.drawString("Robo-kurssin demo",0,1);
    Button.waitForPress();
    arbitrator.start();
  }
}


class DriveForward implements Behavior
{

  private boolean _suppressed = false;

  public boolean takeControl()
  {
    return true;
  }

  public void suppress()
  {
    _suppressed = true;
  }

  public void action()
  {
    _suppressed = false;
    Motor.B.forward();
    Motor.C.forward();
    while (!_suppressed)
    {
      Thread.yield();
    }
    Motor.B.stop();
    Motor.C.stop();
  }
}


class DetectWall implements Behavior
{

  public DetectWall()
  {
    touch = new TouchSensor(SensorPort.S1);
    sonar = new UltrasonicSensor(SensorPort.S4);
  }

  public boolean takeControl()
  {
    sonar.ping();
    if(touch.isPressed())
    {
 //   	Sound.systemSound(false, 4);
    	return true;
    }
    if(sonar.getDistance() < 20)
    {
    	Sound.systemSound(false, 2);
    	return true;
    }
    return false;
  }

  public void suppress()
  {
  }

  public void action()
  {
	Motor.A.setSpeed(200);
	Motor.A.rotateTo(360);
    Motor.A.rotateTo(-360);
    Motor.A.stop();
    //sonar.ping();
    Sound.pause(30);
    drawGraph();
    //sonar.continuous();
    Motor.B.rotate(-180, true);// start Motor.A rotating backward
    Motor.C.rotate(-360);  // rotate C farther to make the turn
  }
  private TouchSensor touch;
  private UltrasonicSensor sonar;
  
  private void drawGraph()
  {
	LCD.clear();
  	int[] distances = new int[8];
  	sonar.getDistances(distances);
  	for(int i = 0; i<distances.length;i++)
  	{
  		int merkki = 0;
  	  	for(int j = 0; j<distances[i];j++)
  	  	{
  	  		if(j%10==0)
  	  		{
  	  			LCD.drawString("*", 4+merkki, i);
  	  			merkki++;
  	  		}
  	  	}
  	}
  }
}

class Stop implements Behavior
{
  private boolean _supressed = false;


  public boolean takeControl()
  {
	return Button.ESCAPE.isPressed();
  }

  public void suppress()
  {
    _supressed = true;
  }

  public void action()
  {
	_supressed = false;
	Motor.A.stop();
    Motor.B.stop();
    Motor.C.stop();
    System.exit(0);
  }
}
