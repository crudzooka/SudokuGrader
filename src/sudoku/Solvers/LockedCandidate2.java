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
public class LockedCandidate2 extends Solver{

    //a candidate within a row is restricted to a box. Thus, that
    //candidate cannot be elsewhere in that box.
    
    public LockedCandidate2(SudokuGrid theGrid){
        super(theGrid);
    }
    
    public int solve() {
        SudokuGrid grid = getGrid();
        for (int i=1; i<=9; i++){
            for (int j=0; j<9; j++){
                Row row = grid.getRow(j);
                ArrayList<Cell> cells = row.getCellsContaining(i);
                if (cells.size()>1){
                    boolean complies = true;
                    int sb = -1;
                    for(Cell cell: cells){
                        //check to see if all cells are in same row
                        int b = 3 * (cell.getY()/3) + cell.getX()/3;
                        if(sb == -1)
                            sb=b;
                        else if(sb!=b)
                            complies = false;
                    }
                    if(complies){
                        SmallBox theBox = grid.getSmallBox(sb);
                        boolean found = false;
                        for(Cell c: theBox.toArray()){
                            if(!cells.contains(c) && c.getCandidates().contains(i)){
                                c.removeCandidate(i);
                                //System.out.println(i+": "+c.getX() +c.getY());
                                found = true;
                            }
                        }
                        if(found) return success();
                    }
                }
                Column column = grid.getColumn(j);
                cells = column.getCellsContaining(i);
                if (cells.size()>1){
                    boolean complies = true;
                    int sb = -1;
                    for(Cell cell: cells){
                        //check to see if all cells are in same row
                        int b = 3 * (cell.getY()/3) + cell.getX()/3;
                        if(sb == -1)
                            sb=b;
                        else if(sb!=b)
                            complies = false;
                    }
                    if(complies){
                        SmallBox theBox = grid.getSmallBox(sb);
                        boolean found = false;
                        for(Cell c: theBox.toArray()){
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
