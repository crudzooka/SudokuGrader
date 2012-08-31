package sudoku;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public abstract class CellGrouping
{
    private Cell[] cells;
    
    public CellGrouping()
    {
        cells = new Cell[9];
    }
    
    public CellGrouping(Cell[] c)
    {
        cells = c;
    }
    
    public Cell getCell(int n)
    {
        return cells[n];
    }
    
    public int getCellValue(int n)
    {
        return cells[n].getValue();
    }
    
    public void setCell(Cell c, int n)
    {
        cells[n].setValue(c.getValue());
    }
    
    public void setCellValue(int a, int n)
    {
        cells[n].setValue(a);
    }
    
    public ArrayList<Cell> getCellsContaining(int n){
        ArrayList<Cell> c = new ArrayList<Cell>();
        for(Cell q: cells)
            if(q.getCandidates().contains(new Integer(n)))
                c.add(q);
        return c;
    }

    public Cell[] toArray(){
        return cells;
    }

    public boolean equals(CellGrouping cg){
        return Arrays.equals(cg.toArray(),cells);
    }

    public boolean contains(int n){
        for(int i=0; i<9; i++)
            if(cells[i].getValue()==n)
                return true;
        return false;
    }

}