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
public class Single extends Solver {

    public Single(SudokuGrid grid) {
        super(grid);
    }

    public int solve(){
        SudokuGrid theGrid = getGrid();
        for(int i=0; i<theGrid.numEmptyCells(); i++){
            Cell c = theGrid.getEmptyCell(i);
            ArrayList<Integer> can = c.getCandidates();
            if(can.size()==1){
                c.setValue(can.remove(0));
                theGrid.addConstraint(c);
                success();
                return 1;
            } 
        }
        return 0;
    }

    public static int solveAsHelper(SudokuGrid theGrid){
        for(int i=0; i<theGrid.numEmptyCells(); i++){
            Cell c = theGrid.getEmptyCell(i);
            ArrayList<Integer> can = c.getCandidates();
            if(can.size()==1){
                c.setValue(can.remove(0));
                theGrid.addConstraint(c);
                return 1;
            }
        }
        return 0;
    }

}
