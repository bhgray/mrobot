package org.havalinasw.Mazes;

public class MazeCell {
    private int x, y;
    
    public MazeCell(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public int X() {return x;}
    public int Y() {return y;}
    
    public boolean equals(Object obj) {
        if (obj instanceof MazeCell) {
            MazeCell p = (MazeCell)obj;
            return p.X() == X() && p.Y() == Y();
        } else {
            return false;
        }
    }
    
    public int getManhattanDist(MazeCell other) {
        return Math.abs(other.x - x) + Math.abs(other.y - y);
    }
        
    public String toString() {return "(" + x + ", " + y + ")";}
   
    public int hashCode() {return toString().hashCode();}
    
    public boolean isNeighbor(MazeCell other) {
        boolean xDiffer = Math.abs(X() - other.X()) == 1;
        boolean yDiffer = Math.abs(Y() - other.Y()) == 1;
        return (!xDiffer || !yDiffer) && (xDiffer || yDiffer);
    }
}
