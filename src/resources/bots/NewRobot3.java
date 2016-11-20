/*
 * NewRobot3.java
 *
 * Created on June 12, 2007, 1:35 PM
 * 
 * Instructions:  change the code in the work() method, below.
 *
 */

import java.awt.Color;
import kareltherobot.*;
import org.teachopensource.botplugins.RobotWorker;

/**
 *
 * @author YOU
 *
 */
public class NewRobot3 extends Robot implements RobotWorker {

    /** Creates a new instance of NewRobot3 */
    public NewRobot3(int street, int avenue, Directions.Direction direction, int beepers, java.awt.Color color) {
        super(street, avenue, direction, beepers, color);
        // multi-threaded setup
        World.setupThread(this);
        World.showSpeedControl(true);
    }
    
    public void run() {
      this.work();
    }
  
    public void init() {
 
    }
    
    public boolean setParam(String key, Object param) {
      return true;
    }
 
    public void solve() {
 
 }

 /**************************************************************************************************
  * 
  *  This method is the engine for the robot.  
  *  
  *************************************************************************************************/
    public void work() {

    	/**************************************************************************************************
    	 * 
    	 *  	Add code BELOW this line 																  */
    	 

    
    
    	/**  	Add code ABOVE this line 
    	 * 
    	 ************************************************************************************************/

    }
    
    /*
     * Add any helper methods below.
     */
    
    
    
}
