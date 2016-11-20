package org.havalinasw.MGUI;

import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;
import kareltherobot.*;
import org.havalinasw.botplugins.RobotWorker;

public class KJRBotInfo implements Directions {
        
    private int x;
    private int y;
    private int beepers;
    private Direction direction;
    private Color color;
    private String classFile;
    private RobotWorker theBot;
    
    public KJRBotInfo(int newX, int newY, Direction newDir, int newBeepers, Color newColor) {
        x = newX;
        y = newY;
        direction = newDir;
        beepers = newBeepers;
        color = newColor;
    }
    
    
    public KJRBotInfo(int newX, int newY, Direction newDir, int newBeepers, Color newColor, String newClass) {
        this(newX, newY, newDir, newBeepers, newColor);
        classFile = newClass;
        theBot = null;
    }
    
    public int getX() {
        return x;
    }
    
    public void setX(int newX) {
        x = newX;
    }
    
    public int getY() {
        return y;
    }
    
    public void setY(int newY) {
        y = newY;
    }
    
    public int getBeepers() {
        return beepers;
    }
    
    public Direction getDirection() {
        return direction;
    }
    
    public Color getColor() {
        return color;
    }
    
    public String getBotClass() {
        return classFile;
    }
    
    public RobotWorker getBot() {
        return theBot;
    }
    
    public void setBot(RobotWorker aBot) {
        theBot = aBot;
    }
}   