package org.havalinasw.Mazes;

import java.awt.*;
import javax.swing.*;

public class MazePanel extends JPanel {
    private Maze m;
    private MazePath mp;
    
    public MazePanel() {
        super();
        setBackground(Color.white);
        m = null;
        mp = null;
    }
    
    public void setMaze(Maze m) {
        this.m = m;
        mp = null;
    }
    
    public void setPath(MazePath mp) {
        this.mp = mp;
    }
    
    protected void paintComponent(Graphics g) {
        g.setColor(Color.white);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.black);
        g.drawRect(0, 1, getWidth() - 1, getHeight() - 1);
        if (m == null) {return;}
        
        double xUnit = (double)getWidth() / (double)m.getXSize();
        double yUnit = (double)getHeight() / (double)m.getYSize();
        int xFill = (int)(xUnit*2)/3;
        int yFill = (int)(yUnit*2)/3;
        int xIndent = (int)(xUnit - xFill)/2;
        int yIndent = (int)(yUnit - yFill)/2;
        
        for (int x = 0; x < m.getXSize(); ++x) {
            for (int y = 0; y < m.getYSize(); ++y) {
                g.setColor(Color.black);
                MazeCell c = new MazeCell(x, y);
                int xBase = (int)((double)x * xUnit);
                int yBase = (int)((double)y * yUnit);
                int xNext = (int)(xBase + xUnit);
                int yNext = (int)(yBase + yUnit);
                if (m.northBlocked(c)) {
                    g.drawLine(xBase, yBase, xNext, yBase);
                }
                if (m.westBlocked(c)) {
                    g.drawLine(xBase, yBase, xBase, yNext);
                }
                if (m.southBlocked(c)) {
                    g.drawLine(xBase, yNext, xNext, yNext);
                }
                if (m.eastBlocked(c)) {
                    g.drawLine(xNext, yBase, xNext, yNext);
                }
                
                if (m.isStart(c)) {
                    g.setColor(Color.yellow);
                    g.fillRect(xBase + xIndent, yBase + yIndent, xFill, yFill);
                } else if (m.isEnd(c)) {
                    g.setColor(Color.blue);
                    g.fillRect(xBase + xIndent, yBase + yIndent, xFill, yFill);
                } else if (m.isTreasure(c)) {
                    g.setColor(new Color(0, 255, 255));
                    g.fillRect(xBase + xIndent, yBase + yIndent, xFill, yFill);
                }
            }
        }
        
        if (mp == null) {return;}
        g.setColor(Color.red);
        xFill = (int)(xUnit*3)/8;
        yFill = (int)(yUnit*3)/8;
        xIndent = (int)(xUnit - xFill)/2;
        yIndent = (int)(yUnit - yFill)/2;
        for (int i = 0; i < mp.getLength(); ++i) {
            MazeCell p = mp.getNth(i);
            if (i == mp.getLength() - 1) {
                g.setColor(Color.green);
            }
            g.fillRect((int)(p.X() * xUnit) + xIndent, 
                       (int)(p.Y() * yUnit) + yIndent, 
                       xFill, yFill);
        }
    }
}
