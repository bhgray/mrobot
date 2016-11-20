/*
 * KJRInfo.java
 *
 * Created on June 8, 2007, 3:39 PM
 *
 */

package org.havalinasw.MGUI;

import java.awt.Dimension;
import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;
import kareltherobot.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;
import org.havalinasw.Mazes.Maze;
import org.havalinasw.Mazes.MazeCell;
import org.havalinasw.botplugins.RobotWorker;

/**
 *
 * @author bhgray
 */
public class KJRInfo implements Directions {
    
    private Dimension worldDimension;
    private String worldFile;
    private Maze theMaze;
    private boolean isMaze;
    private String mazeFileName;
    // the information for each robot
    private ArrayList<KJRBotInfo> bots;
    // robot plugins available and loaded
    private Hashtable<String, Class> botTable;
    
    /** Creates a new instance of KJRInfo */
    public KJRInfo() {
        worldDimension = new Dimension(1, 1);
        worldFile = "";
        theMaze = null;
        bots = new ArrayList();
        botTable = new Hashtable<String, Class>();
        bots = new ArrayList<KJRBotInfo>();
    }
    
    public void addRobot(int x, int y, Direction direction, int beepers, Color color) {
        bots.add(new KJRBotInfo(x, y, direction, beepers, color));
    }
    
    public void addRobot(int x, int y, Direction direction, int beepers, Color color, String classFile) {
        bots.add(new KJRBotInfo(x, y, direction, beepers, color, classFile));
    }

    public int numRobots() {
        return bots.size();
    }
    
    public Iterator getBotIterator() {
        return bots.iterator();
    }
    
    public Dimension getWorldDimension() {
        return (Dimension)worldDimension.clone();
    }
    
    public void setWorldDimension(int x, int y) {
        worldDimension = new Dimension(x, y);
        theMaze = new Maze(x, y);
        theMaze.makeMaze(   new MazeCell(theMaze.getXMin(), theMaze.getYMin()),
                            new MazeCell(theMaze.getXMax(), theMaze.getYMax()), 0, 0);
    }
    
    public void addBotPlugin(String name, Class worker) {
        botTable.put(name, worker);
    }
    
    public Class getBotPluginForName(String name) {
        return botTable.get(name);
    }
    
    public Enumeration getBotPluginNames() {
        return botTable.keys();
    }
    
    public String getWorldFileLocation() {
        return worldFile;
    }
    
    public void setWorldFileLocation(String loc) {
        worldFile = loc;
    }
    
    public ArrayList<KJRBotInfo> getBotInfo() {
        return (ArrayList)bots.clone();
    }
    
    public void setIsMaze(boolean b) {
        isMaze = b;
    }
    
    public boolean isMaze() {
        return isMaze;
    }

    public void setMazeFileName(String name) {
        mazeFileName = name;
    }
    
    public String getMazeFileName() {
        return mazeFileName;
    }
    
}