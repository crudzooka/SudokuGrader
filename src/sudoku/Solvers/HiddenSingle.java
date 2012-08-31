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
public class HiddenSingle extends Solver{

    public HiddenSingle(SudokuGrid tg){
        super(tg);
    }
    
    public int solve(){
        SudokuGrid theGrid = getGrid();
        for(int i=0; i<theGrid.numEmptyCells(); i++){
            Cell c = theGrid.getEmptyCell(i);
            ArrayList<Integer> cCan = c.getCandidates();
            int x = c.getX();
            int y = c.getY();
            //Check by row
            Cell[] group = theGrid.getRow(y).toArray();
            ArrayList<Integer> gCan = new ArrayList<Integer>();
            for(int j=0; j<9; j++)
                if(group[j] != c)
                    gCan.addAll(group[j].getCandidates());
            for(Integer a: cCan){
                if(!gCan.contains(a)){
                    c.setValue(a);
                    theGrid.addConstraint(c);
                    success();
                    return 1;
                }
            }
            //Check by column
            group = theGrid.getColumn(x).toArray();
            gCan.clear();
            for(int j=0; j<9; j++)
                if(group[j] != c)
                    gCan.addAll(group[j].getCandidates());
            for(Integer a: cCan){
                if(!gCan.contains(a)){
                    c.setValue(a);
                    theGrid.addConstraint(c);
                    success();
                    return 1;
                }
            }
            //Check by cellblock
            int sb = (y/3 * 3) + x/3;
            group = theGrid.getSmallBox(sb).toArray();
            gCan.clear();
            for(int j=0; j<9; j++){
                if(group[j] != c)
                    gCan.addAll(group[j].getCandidates());
            }
            for(Integer a: cCan){
                if(!gCan.contains(a)){
                    c.setValue(a);
                    theGrid.addConstraint(c);
                    success();
                    return 1;
                }
            }
        }
        return 0;
    }

    /*public static int solveAsHelper(SudokuGrid theGrid){
    for(int i=0; i<theGrid.numEmptyCells(); i++){
    Cell c = theGrid.getEmptyCell(i);
    ArrayList<Integer> cCan = c.getCandidates();
    int x = c.getX();
    int y = c.getY();
    //Check by row
    Cell[] group = theGrid.getRow(y).toArray();
    ArrayList<Integer> gCan = new ArrayList<Integer>();
    for(int j=0; j<9; j++)
    if(group[j] != c)
    gCan.addAll(group[j].getCandidates());
    for(Integer a: cCan){
    if(!gCan.contains(a)){
    c.setValue(a);
    theGrid.addConstraint(c);
    return 1;
    }
    }
    //Check by column
    group = theGrid.getColumn(x).toArray();
    gCan.clear();
    for(int j=0; j<9; j++)
    if(group[j] != c)
    gCan.addAll(group[j].getCandidates());
    for(Integer a: cCan){
    if(!gCan.contains(a)){
    c.setValue(a);
    theGrid.addConstraint(c);
    return 1;
    }
    }
    //Check by cellblock
    int sb = (y/3 * 3) + x/3;
    group = theGrid.getSmallBox(sb).toArray();
    gCan.clear();
    for(int j=0; j<9; j++){
    if(group[j] != c)
    gCan.addAll(group[j].getCandidates());
    }
    for(Integer a: cCan){
    if(!gCan.contains(a)){
    c.setValue(a);
    theGrid.addConstraint(c);
    return 1;
    }
    }
    }
    return 0;
    }*/

}
