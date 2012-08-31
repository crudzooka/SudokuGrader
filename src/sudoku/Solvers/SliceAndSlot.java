package sudoku.Solvers;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.ArrayList;
import sudoku.*;

/**
 *
 * @author Jay
 */
public class SliceAndSlot extends Solver{

   public SliceAndSlot(SudokuGrid tg){
       super(tg);
   }

    public int solve() {
        SudokuGrid grid = getGrid();
        for(int i=0; i<grid.numEmptyCells(); i++){
            Cell c = grid.getEmptyCell(i);
            int x = c.getX();
            int y = c.getY();
            for(Integer candidate: c.getCandidates()){
                ArrayList<Integer> xclusions = new ArrayList<Integer>();
                ArrayList<Integer> yclusions = new ArrayList<Integer>();
                for(int j = x-x%3; j<(x-x%3 +3); j++)
                    if(j!=x)
                        if(grid.getColumn(j).contains(candidate))
                            xclusions.add(j);
                for(int j = y-y%3; j<(y-y%3 +3); j++)
                    if(j!=y)
                        if(grid.getRow(j).contains(candidate))
                            yclusions.add(j);
                boolean complies = true;
                for(Cell q: grid.getSmallBox(x/3 + (y/3)*3).toArray())
                    if((!xclusions.contains(q.getX()) && !yclusions.contains(q.getY())) && q.getValue()==0 && q!=c)
                        complies = false;
                if(complies){
                    c.setValue(candidate);
                    grid.addConstraint(c);
                    return success();
                }
            }
        }
        return 0;
    }

}
