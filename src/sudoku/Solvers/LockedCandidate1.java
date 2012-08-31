/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sudoku.Solvers;
import java.util.ArrayList;
import sudoku.*;

/**
 *
 * @author Jay
 */
public class LockedCandidate1 extends Solver{

    //a candidate within a box is restricted to one row or column.
    //so it cannot be in that column in other blocks
    
    public LockedCandidate1(SudokuGrid theGrid){
        super(theGrid);
    }
    
    public int solve() {
        SudokuGrid grid = getGrid();
        for (int i=1; i<=9; i++){
            for (int j=0; j<9; j++) {
                SmallBox box = grid.getSmallBox(j);
                ArrayList<Cell> cells = box.getCellsContaining(i);
                if (cells.size()>1){
                    boolean complies = true;
                    int row = -1;
                    for(Cell cell: cells){
                        //check to see if all cells are in same row
                        int y = cell.getY();
                        if(row == -1)
                            row=y;
                        else if(row!=y)
                            complies = false;
                    }
                    if(complies){
                        Row theRow = grid.getRow(row);
                        boolean found = false;
                        for(Cell c: theRow.toArray()){
                            if(!cells.contains(c) && c.getCandidates().contains(i)){
                                c.removeCandidate(i);
                                //System.out.println(i+": "+c.getX() +c.getY());
                                found = true;
                            }
                        }
                        if(found) return success();
                    }
                    complies = true;
                    int column = -1;
                    for(Cell cell: cells){
                        //check to see if all cells are in same row
                        int x = cell.getX();
                        if(column == -1)
                            column=x;
                        else if(column!=x)
                            complies = false;
                    }
                    if(complies){
                        Column theColumn = grid.getColumn(column);
                        boolean found = false;
                        for(Cell c: theColumn.toArray()){
                            if(!cells.contains(c) && c.getCandidates().contains(i)){
                                c.removeCandidate(i);
                                //System.out.println(i+": "+c.getX() +c.getY());
                                found = true;
                            }  
                        }
                        if(found) return success();
                    }
                }
            }
        }
        return 0;

    }

}
