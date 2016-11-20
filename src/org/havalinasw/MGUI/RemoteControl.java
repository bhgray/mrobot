package org.havalinasw.MGUI;
/*
 * Created on Oct 26, 2003
 *
 */

import org.havalinasw.botplugins.RobotWorker;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import javax.media.jai.NullCRIF;
import kareltherobot.*;
import java.awt.Frame;
import java.awt.Button;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/** Instantiating this class will create a robot and a remote control panel to
 * control it. Use this to walk through a proposed solution to a problem. 
 * You can instantiate more than one of these to get robots with distinct
 * controllers. You can create one (or more) of these in a program that has 
 * other robots moving under program control. The response time of the 
 * controller is determined by the delay set in the world. To get the fastest
 * response, use World.setDelay(1). If you click the close control of the 
 * controller's window, the window will be made invisible, but can be 
 * brought back under program control by setting it visible again. 
 * <p> The background color of the controller is the same as the color of the
 * robot's badge (or white if none), and the controllers are assigned numbers
 * that are consistent with the robot's ID numbers (only) if these robots are
 * created before any others in your program. 
 */
public class RemoteControl extends Frame implements Directions
{
 public RemoteControl(int street, int avenue, Direction direction, int beepers)
 { this(street, avenue, direction, beepers, null, null);
 }
 
 public RemoteControl(int street, int avenue, Direction direction)
 { this(street, avenue, direction, infinity, null, null);
 }
 
 public RemoteControl()
 { this(1, 1, North, infinity, null, null);
 }

 public RemoteControl(int street, int avenue, Direction direction, int beepers, Color color, String botClass)
 {
  super("Robot " + getID()+" Controller");
  if(color != null)setBackground(color);
  
  // here we need to instantiate the type of robot specified
  if (MainFrame.DEBUG) {
          System.out.println("RemoteControl:  botClass was " + botClass);
  }
      
     karel = new Robot(street, avenue, direction, beepers, color);
     initComponents();
 }
 
 private void initWorkerComponents() {
     Button work = new Button("Work");
     add(work);
     work.addActionListener(new Worker());
     addWindowListener(new Hider());
     setSize(300, 150);
     setLocation(560 + delta*(id-1),100 + delta*(id-1));
     setVisible(true);     
 }
 
 private void initComponents() {
  setLayout(new GridLayout(3,2));
  Button move = new Button("Move");
  move.addActionListener(new Mover());
  add(move);
  Button turn = new Button("Turn Left");
  turn.addActionListener(new LeftTurner());
  add(turn);
  Button pick = new Button("Pick Beeper");
  pick.addActionListener(new Picker());
  add(pick);
  Button put = new Button("Put Beeper");
  put.addActionListener(new Putter());
  add(put);
  Button stop = new Button("Turn Off");
  stop.addActionListener(new Stopper());
  add(stop);
  
  addWindowListener(new Hider());
  setSize(300, 150);
  setLocation(560 + delta*(id-1),100 + delta*(id-1));
  setVisible(true);  
 }
  
 private static int getID()
 {
     return id++;
 }

 private static int id = 0;
 private static int delta = 10;
 private  Robot karel = null;
 private RobotWorker bot = null;

 public RobotWorker getBot() {
     return bot;
 }
 
 private class Hider extends WindowAdapter
 { public void windowClosing(WindowEvent e)
  { setVisible(false);
  }
 }

 private class Worker implements ActionListener
 { public void actionPerformed(ActionEvent e)
  { 
       bot.work();
  }
 }
 
 private class Stopper implements ActionListener
 { public void actionPerformed(ActionEvent e)
  { karel.turnOff();
  }
 }

 private class Putter implements ActionListener
 { public void actionPerformed(ActionEvent e)
  { karel.putBeeper();
  }
 }

 private class Picker implements ActionListener
 { public void actionPerformed(ActionEvent e)
  { karel.pickBeeper();
  }
 }

 private class Mover implements ActionListener
 { public void actionPerformed(ActionEvent e)
  { karel.move();
  }
 }

 private class LeftTurner implements ActionListener
 { public void actionPerformed(ActionEvent e)
  { karel.turnLeft();
  }
 } 
}
