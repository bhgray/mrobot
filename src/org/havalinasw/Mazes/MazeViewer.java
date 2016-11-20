package org.havalinasw.Mazes;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;

public class MazeViewer extends JFrame {
    
    private Maze m;
    private MazePanel mp;
    private MazePath path;
    private JButton makeMaze, startPath, checkPath, up, down, left, right, solve;
    private JSlider perfector;
    private JTextField nField, dField, bField, sField;
    private JTextField mazeX, mazeY, treasure;
    private MoveListen mListen = new MoveListen();
    private JComboBox heuristics = 
    new JComboBox(new String[]{"BreadthFirst"});
    
    public MazeViewer(Maze m) {
        this();
        mp.setMaze(m);
        mp.repaint();
    }
    
    public MazeViewer() {
        setTitle("Maze Viewer");
        setSize(700, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        Container pane = getContentPane();
        pane.setLayout(new BorderLayout());
        
        JPanel leftSide = new JPanel(new FlowLayout());
        pane.add(leftSide, BorderLayout.WEST);
        leftSide.add(new JLabel("Perfection"));
        perfector = new JSlider(JSlider.VERTICAL, 0, 100, 0);
        leftSide.add(perfector);
        
        JPanel top = new JPanel(new GridLayout(2, 1));
        pane.add(top, BorderLayout.NORTH);
        
        JPanel controls = new JPanel(new FlowLayout());
        top.add(controls);
        controls.add(new JLabel("# Treasures"));
        treasure = new JTextField(5);
        treasure.setText("0");
        controls.add(treasure);
        makeMaze = new JButton("Make Maze");
        makeMaze.addActionListener(new MakeListen());
        controls.add(makeMaze);
        startPath = new JButton("Start Path");
        startPath.addActionListener(mListen);
        controls.add(startPath);
        
        controls.add(new JLabel("Width"));
        mazeX = new JTextField(4);
        mazeX.setText("10");
        controls.add(mazeX);
        controls.add(new JLabel("Height"));
        mazeY = new JTextField(4);
        mazeY.setText("10");
        controls.add(mazeY);
        
        checkPath = new JButton("Verify Path");
        checkPath.addActionListener(new Validator());
        controls.add(checkPath);
        
        JPanel data = new JPanel(new FlowLayout());
        top.add(data);
        
        data.add(heuristics);
        solve = new JButton("Solve");
        solve.addActionListener(new SolveMaze());
        data.add(solve);
        
        nField = new JTextField(4);
        data.add(new JLabel("Nodes"));
        data.add(nField);
        
        dField = new JTextField(4);
        data.add(new JLabel("depth"));
        data.add(dField);
        
        bField = new JTextField(4);
        data.add(new JLabel("b*"));
        data.add(bField);
        
        sField = new JTextField(4);
        data.add(new JLabel("Solution length"));
        data.add(sField);
        
        JPanel superDir = new JPanel(new GridLayout(5, 1));
        pane.add(superDir, BorderLayout.EAST);
        superDir.add(new JPanel(new FlowLayout()));
        superDir.add(new JPanel(new FlowLayout()));
        
        JPanel directions = new JPanel(new BorderLayout());
        superDir.add(directions);
        up = new JButton("Up");
        up.addActionListener(mListen);
        directions.add(up, BorderLayout.NORTH);
        down = new JButton("Down");
        down.addActionListener(mListen);
        directions.add(down, BorderLayout.SOUTH);
        left = new JButton("Left");
        left.addActionListener(mListen);
        directions.add(left, BorderLayout.WEST);
        right = new JButton("Right");
        right.addActionListener(mListen);
        directions.add(right, BorderLayout.EAST);
        
        mp = new MazePanel();
        pane.add(mp, BorderLayout.CENTER);
    }
    
    private class MakeListen implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int xSize = Integer.parseInt(mazeX.getText());
            int ySize = Integer.parseInt(mazeY.getText());
            m = new Maze(xSize, ySize);
            double perfection = (double)perfector.getValue()/(double)perfector.getMaximum();
            m.makeMaze(new MazeCell(m.getXMax(), m.getYMin()),
                       new MazeCell(m.getXMin(), m.getYMax()),
                       Integer.parseInt(treasure.getText()), perfection);
            mp.setMaze(m);
            mp.repaint();
        }
    }
    
    private class MoveListen implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == startPath || path == null) {
                path = new MazePath(m.getStart().X(), m.getStart().Y());
                mp.setPath(path);
                mp.repaint();
            }
            
            MazeCell next = null;
            if (e.getSource() == up && !m.northBlocked(path.getEnd())) {
                next = new MazeCell(path.getEnd().X(), path.getEnd().Y() - 1);
            }

            if (e.getSource() == down && !m.southBlocked(path.getEnd())) {
                next = new MazeCell(path.getEnd().X(), path.getEnd().Y() + 1);
            }

            if (e.getSource() == left && !m.westBlocked(path.getEnd())) {
                next = new MazeCell(path.getEnd().X() - 1, path.getEnd().Y());
            }

            if (e.getSource() == right && !m.eastBlocked(path.getEnd())) {
                next = new MazeCell(path.getEnd().X() + 1, path.getEnd().Y());
            }
            
            if (next != null) {
                path.append(next);
                mp.repaint();
            }
        }
    }
    
    private class Validator implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (path == null) {
                JOptionPane.showMessageDialog(null, "No path started");
            } else if (path.solvesMaze(m)) {
                JOptionPane.showMessageDialog(null, "Path solves maze");
            } else {
                JOptionPane.showMessageDialog(null, "Path does not solve maze");
            }
        }
    }
    
    private class SolveMaze implements ActionListener {
        public void actionPerformed(ActionEvent e) {        
            // Create and invoke a BestFirstSearcher from here
        }
    }
    
    public static void main(String[] args) {
        MazeViewer gui = new MazeViewer();
        gui.setVisible(true);
    }
}
