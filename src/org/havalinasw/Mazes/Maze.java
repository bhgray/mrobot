package org.havalinasw.Mazes;

import java.util.*;
import kareltherobot.World;
import java.awt.Point;
import org.havalinasw.MGUI.MainFrame;

public class Maze {
    private static final int KAREL_SHIFTY = 3;
    private static final int KAREL_SHIFTX = 3;
    private static final String SPACE = " ";
    private static final String ENDLINE = "\n";
    
    private int xSize, ySize;
    private MazeCell start, end;
    
    // if y is even, barriers[x][y] indicates whether MazeCell (x, y/2) is
    //   blocked above and MazeCell (x, y/2 - 1) is blocked below
    // if y is odd, barriers[x][y] indicates whether MazeCell (x, y/2) is
    //   blocked to the left and MazeCell (x - 1, y/2) is blocked to the right
    //
    private boolean[][] barriers;
    private MazeCell[][] cells;
    private Set<MazeCell> treasures;
    
    // Pre: xSize > 0; ySize > 0
    // Post: Generates a maze in which every cell is barricaded from every
    //       other cell
    public Maze(int xSize, int ySize) {
        this.xSize = xSize;
        this.ySize = ySize;
        start = end = null;
        cells = new MazeCell[xSize][ySize];
        for (int x = 0; x < xSize; ++x) {
            for (int y = 0; y < ySize; ++y) {
                cells[x][y] = new MazeCell(x, y);
            }
        }
        barriers = new boolean[getBarrierX()][getBarrierY()];
        for (int x = 0; x < getBarrierX(); ++x) {
            for (int y = 0; y < getBarrierY(); ++y) {
                barriers[x][y] = true;
            }
        }
        
        treasures = new LinkedHashSet<MazeCell>();
    }
    
    // Pre: 0 <= imperfection <= 1.0
    // Post: Randomly generates a maze of the given size, starting at start
    //       and ending at end; if imperfection = 0, the maze is perfect; if
    //       imperfection = 1, the maze has very few walls
    public void makeMaze(MazeCell start, MazeCell end, int numTreasures, double imperfection) {
        this.start = start;
        this.end = end;
        
        ArrayList<MazeCell> openList = new ArrayList<MazeCell>();
        Map<MazeCell,MazeCell> predecessors = new HashMap<MazeCell,MazeCell>();
        Set<MazeCell> visited = new LinkedHashSet<MazeCell>();
        openList.add(end);
        while (openList.size() > 0) {
            int i = (int)(Math.random() * openList.size()); 		// random from 0 to size() - 1
            MazeCell current = openList.get(i);
            openList.set(i, openList.get(openList.size() - 1));
            openList.remove(openList.size() - 1);
            if (!visited.contains(current)) {
                visited.add(current);
                if (predecessors.keySet().contains(current)) {
                    knockDownBetween(current, predecessors.get(current));
                }
                ArrayList<MazeCell> neighbors = getNeighbors(current);
                for (MazeCell neighbor: neighbors) {
                    openList.add(neighbor);
                    predecessors.put(neighbor, current);
                }
            } else if (Math.random() < imperfection) {
                if (predecessors.keySet().contains(current)) {
                    knockDownBetween(current, predecessors.get(current));
                }
            }
        }
        
        addTreasure(numTreasures);
    }
    
    private void addTreasure(int numTreasures) {
        treasures = new LinkedHashSet<MazeCell>();
        int numUntried = xSize * ySize - 2;
        for (int i = 0; i < xSize; ++i) {
            for (int j = 0; j < ySize; ++j) {
                MazeCell candidate = new MazeCell(i, j);
                if (!candidate.equals(getStart()) && !candidate.equals(getEnd())) {
                    double prob = (double)numTreasures / (double)numUntried;
                    if (Math.random() < prob) {
                        treasures.add(candidate);
                        numTreasures--;
                    }
                    numUntried--;
                }
            }
        }
    }
    
    public MazeCell getStart() {return start;}
    public MazeCell getEnd() {return end;}
    
    public boolean isStart(MazeCell mc) {return start.equals(mc);}
    public boolean isEnd(MazeCell mc) {return end.equals(mc);}
    public boolean isTreasure(MazeCell mc) {return treasures.contains(mc);}
    public boolean isTreasure(int x, int y) {return isTreasure(new MazeCell(x, y));}
    
    public Set<MazeCell> getTreasures() {return treasures;}
    
    public int getXMin() {return 0;}
    public int getYMin() {return 0;}
    public int getXMax() {return xSize - 1;}
    public int getYMax() {return ySize - 1;}
    public int getXSize() {return xSize;}
    public int getYSize() {return ySize;}
    
    // Pre: c.isNeighbor(n)
    // Post: Returns true if it is not possible to travel from c to n in one
    //       step; returns false otherwise
    public boolean blocked(MazeCell c, MazeCell n) {
        if (!c.isNeighbor(n)) {
            throw new IllegalArgumentException(c + " is not a neighbor to " + n);
        }
        
        int xDiff = c.X() - n.X();
        int yDiff = c.Y() - n.Y();
        if (xDiff == 1) {
            return westBlocked(c);
        } else if (xDiff == -1) {
            return eastBlocked(c);
        } else if (yDiff == 1) {
            return northBlocked(c);
        } else if (yDiff == -1) {
            return southBlocked(c);
        } else {
            throw new IllegalStateException("Should never get here");
        }
    }
    
    public boolean northBlocked(MazeCell c) {
        return barriers[c.X()][c.Y() * 2];
    }
    
    public boolean southBlocked(MazeCell c) {
        return barriers[c.X()][(c.Y() + 1) * 2];
    }
    
    public boolean eastBlocked(MazeCell c) {
        return barriers[c.X() + 1][(c.Y() * 2) + 1];
    }
    
    public boolean westBlocked(MazeCell c) {
        return barriers[c.X()][(c.Y() * 2) + 1];
    }
    
    public String toString() {
        String result = "";
        for (int y = 0; y < getBarrierY(); ++y) {
            for (int x = 0; x < getBarrierX(); ++x) {
                if (barriers[x][y]) {
                    result += "#";
                } else {
                    result += " ";
                }
                if (y % 2 == 0 && x < getBarrierX() - 1) {
                    result += "#";
                } else {
                    result += " ";
                }
            }
            result += "\n";
        }
        return result;
    }
    
    public String toKarelString() {
        StringBuffer result = new StringBuffer();
        result.append("KarelWorld" + "\n");
        // streets go e-w
        result.append("streets " + (getYSize()+ 2*getKarelShiftY()) + "\n");
        // aves go n-s
        result.append("avenues " + (getXSize()+ 2*getKarelShiftX()) + "\n");
        result.append(makeKarelBounds());
        for (int x = 0; x < getXSize(); ++x) {
            for (int y = 0; y < getYSize(); ++y) {
                // eastwestwalls <north of street> <first ave crossed> <last ave crossed>
                // northsouthwalls <east of ave> <first st crossed> <last st crossed>
                MazeCell c = new MazeCell(x, y);
                if (northBlocked(c)) { // want a ew wall immediately above
                    result.append("eastwestwalls" + SPACE + translateKarelY(y) + SPACE + translateKarelX(x) + SPACE + translateKarelX(x) + ENDLINE);
                }
                if (westBlocked(c)) {  // want a ns wall imeediately left
                    //result.append("northsouthwalls " + translateKarelX(x-1) + " " + translateKarelY(y) + " " + translateKarelY(y) + "\n");
                }
                if (southBlocked(c)) { // want a ew wall immediately below
                    //result.append("eastwestwalls " + translateKarelY(y) + " " + translateKarelX(x) + " " + translateKarelX(x) + "\n");
                }
                if (eastBlocked(c)) { // want a ns wall immediately right
                    result.append("northsouthwalls" + SPACE + translateKarelX(x) + SPACE + translateKarelY(y) + SPACE + translateKarelY(y) + ENDLINE);
                }
            }
        }
        
        // SET A BEEPER AT THE FINISH
        // beepers street avenue number
        if (MainFrame.DEBUG) {
            System.out.println("Start:  (" + getStart().X() + " " + getStart().X() + ")");
            System.out.println("Finish:  (" + getEnd().X() + " " + getEnd().X() + ")");
            
        }
        result.append("beepers" + SPACE + translateKarelY(getEnd().Y()) + SPACE + translateKarelX(getEnd().X()) + SPACE + "1" + ENDLINE);

        return result.toString();
    }
    
    public String makeKarelBounds() {
        StringBuffer theBounds = new StringBuffer();
        // need ns walls along karelShiftX() coordinate from top to bottom
        // need ew walls along bottom of maze
        // eastwestwalls <north of street> <first ave crossed> <last ave crossed>
        // northsouthwalls <east of ave> <first st crossed> <last st crossed>
                
        for (int i = getKarelShiftX(); i <= getKarelShiftX() + getXMax(); i++) {
            // bottom
            theBounds.append("eastwestwalls" + SPACE + translateKarelY(getYMax()+1) + SPACE + i + SPACE + i + ENDLINE);
        }
         for (int i = getKarelShiftY() + 1; i <= getKarelShiftY() + getYMax() + 1; i++) {
            // left
            theBounds.append("northsouthwalls " + SPACE + translateKarelX(getXMin()-1) + SPACE + i + SPACE + i + ENDLINE);
         }
         return theBounds.toString();
    }

    public int translateKarelX(int x){
        return x + getKarelShiftX();
    }
    public int translateKarelY(int y){
        return getYMax() + getKarelShiftY() - (y-1);
    }
    private int getKarelShiftY() {
        return KAREL_SHIFTY;
    }
    private int getKarelShiftX() {
        return KAREL_SHIFTX;
    }
    public int getKarelMinX(){
        return getXMin() + getKarelShiftX();
    }
    public int getKarelMinY(){
        return getYMin() + getKarelShiftY();
    }
    public int getKarelMaxX(){
        return getXMax() + getKarelShiftX();
    }
    public int getKarelMaxY(){
        return getYMax() + getKarelShiftY();
    }
    
    public Point translateKarelPoint(Point p) {
        return new Point(translateKarelX((int)p.getX())-1, translateKarelY((int)p.getY())+1);
    }
    
    private int getBarrierX() {return xSize + 1;}
    private int getBarrierY() {return ySize*2 + 1;}
    
    private void knockDown(MazeCell current, LinkedHashSet<MazeCell> visited) {
        visited.add(current);
        ArrayList<MazeCell> neighbors = getNeighbors(current);
        for (MazeCell neighbor: neighbors) {
            if (!visited.contains(neighbor)) {
                knockDownBetween(current, neighbor);
                knockDown(neighbor, visited);
            }
        }
    }
    
    // Pre: first and second are Manhattan neighbors
    // Post: Knocks down a wall between them, if it exists
    private void knockDownBetween(MazeCell first, MazeCell second) {
        int xDiff = first.X() - second.X();
        int yDiff = first.Y() - second.Y();
        
        if (xDiff == 1) {
            barriers[first.X()][(first.Y() * 2) + 1] = false;
        } else if (xDiff == -1) {
            barriers[first.X() + 1][(first.Y() * 2) + 1] = false;
        } else if (yDiff == 1) {
            barriers[first.X()][first.Y() * 2] = false;
        } else if (yDiff == -1) {
            barriers[first.X()][(first.Y() + 1) * 2] = false;
        }
    }
    
    // Pre: none
    // Post: Returns all legal neighbors of current in a randomly permuted
    //       ordering
    private ArrayList<MazeCell> getNeighbors(MazeCell current) {
        ArrayList<MazeCell> neighbors = new ArrayList<MazeCell>(4);
        
        if (current.X() > getXMin()) {
            neighbors.add(cells[current.X() - 1][current.Y()]);
        }
        
        if (current.X() < getXMax()) {
            neighbors.add(cells[current.X() + 1][current.Y()]);
        }
        
        if (current.Y() > getYMin()) {
            neighbors.add(cells[current.X()][current.Y() - 1]);
        }
        
        if (current.Y() < getYMax()) {
            neighbors.add(cells[current.X()][current.Y() + 1]);
        }
        
        Collections.shuffle(neighbors);
        return neighbors;
    }
    
    public static void main(String[] args) {
//        if (args.length != 2) {
//            System.out.println("Usage: Maze xSize ySize");
//            System.exit(1);
//        }
        
//        int xSize = Integer.parseInt(args[0]);
//        int ySize = Integer.parseInt(args[1]);
        
        int xSize = 20;
        int ySize = 20;
        Maze m = new Maze(xSize, ySize);
        System.out.println("Before knockdown");
        System.out.println(m);

        m.makeMaze(new MazeCell(m.getXMin(), m.getYMin()),
                new MazeCell(m.getXMax(), m.getYMax()), 0, 0);
        System.out.println("Maze-ified");
        System.out.println(m);
        World.getWorld(m.toKarelString());
        World.setVisible(true);
        MazeViewer theView = new MazeViewer(m);
        theView.setVisible(true);
    }
}
