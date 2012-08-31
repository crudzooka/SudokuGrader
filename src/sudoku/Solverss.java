/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sudoku;

import java.util.ArrayList;

/**
 *
 * @author Jay
 */
public class Solverss {
 

    public static void simpleSingle(Cell c, SudokuGrid grid){
        ArrayList<Integer> can = c.getCandidates();
        c.findCandidatesSimple(SudokuGrid.COLUMN);
        if(can.size()==1){
            c.setValue(can.remove(0));
            grid.addConstraint(c);
            return;
        }
        c.findCandidatesSimple(SudokuGrid.ROW);
        if(can.size()==1){
            c.setValue(can.remove(0));
            grid.addConstraint(c);
            return;
        }
        c.findCandidatesSimple(SudokuGrid.SMALLBOX);
        if(can.size()==1){
            c.setValue(can.remove(0));
            grid.addConstraint(c);
        }
    }

    public static void Single(Cell c, SudokuGrid grid){
        ArrayList<Integer> can = c.getCandidates();
        c.findCandidates();
        if(can.size()==1){
            c.setValue(can.remove(0));
            grid.addConstraint(c);
        }
    }
}
