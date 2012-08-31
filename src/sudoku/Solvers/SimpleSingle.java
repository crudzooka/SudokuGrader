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
public class SimpleSingle extends Solver{

    public SimpleSingle(SudokuGrid g){
        super(g);
    }

    public int solve() {
        SudokuGrid theGrid = getGrid();
        for(int i=0; i<theGrid.numEmptyCells(); i++){
            Cell c = theGrid.getEmptyCell(i);
            ArrayList<Integer> can = c.findCandidatesSimple(SudokuGrid.COLUMN);
            can = c.findCandidatesSimple(SudokuGrid.COLUMN);
            if(can.size()==1){
                c.setValue(can.remove(0));
                theGrid.addConstraint(c);
                success();
                return 1;
            }
            can = c.findCandidatesSimple(SudokuGrid.ROW);
            if(can.size()==1){
                c.setValue(can.remove(0));
                theGrid.addConstraint(c);
                success();
                return 1;
            }
            can = c.findCandidatesSimple(SudokuGrid.SMALLBOX);
            if(can.size()==1){
                c.setValue(can.remove(0));
                theGrid.addConstraint(c);
                i--;
                success();
                return 1;
            }
        }
        return 0;
    }

}
