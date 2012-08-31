/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sudoku.Solvers;

import sudoku.*;

/**
 *
 * @author Jay
 */
public abstract class Solver {
    private SudokuGrid theGrid;
    private int timesrun = 0;

    public Solver(SudokuGrid a){
        theGrid=a;
    }
    public SudokuGrid getGrid(){
        return theGrid;
    }
    public abstract int solve();

    public int success(){
        timesrun++;
        return 1;
    }

    public int getRuns(){
        return timesrun;
    }
}
