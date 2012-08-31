/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sudoku.Solvers;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import sudoku.*;

/**
 *
 * @author Jay
 */
public class XWing extends Solver{

    public XWing(SudokuGrid g){
        super(g);
    }
    
    public int solve(){
        SudokuGrid grid = getGrid();
        for(int i=0; i<9; i++){
            for(int j=i+1; j<9; j++){
                for(int q=0; q<9; q++){
                    if(grid.getCellValue(i,q) != 0 || grid.getCellValue(j,q) != 0) continue;
                    for(int z=q+1; z<9; z++){
                        if(grid.getCellValue(i,z) != 0 || grid.getCellValue(j,z) != 0) continue;
                        Cell c1 = grid.getCell(i,q);
                        Cell c2 = grid.getCell(i,z);
                        Cell c3 = grid.getCell(j,q);
                        Cell c4 = grid.getCell(j,z);
                        ArrayList<Integer> can1 = c1.getCandidates();
                        ArrayList<Integer> can2 = c2.getCandidates();
                        ArrayList<Integer> can3 = c3.getCandidates();
                        ArrayList<Integer> can4 = c4.getCandidates();
                        if(can1.size() <2 || can2.size()<2 || can3.size()<2 || can4.size()<2)
                            continue;
                        Set<Cell> allcells = new HashSet<Cell>();
                        allcells.addAll(Arrays.asList(grid.getColumn(i).toArray()));
                        allcells.addAll(Arrays.asList(grid.getColumn(j).toArray()));
                        allcells.addAll(Arrays.asList(grid.getRow(q).toArray()));
                        allcells.addAll(Arrays.asList(grid.getRow(z).toArray()));
                        //Clean allcells
                        allcells.remove(c1);
                        allcells.remove(c2);
                        allcells.remove(c3);
                        allcells.remove(c4);
                        Iterator<Cell> it = allcells.iterator();
                        while(it.hasNext())
                            if(it.next().getValue()!=0)
                                it.remove();
                        for(Integer candidate: can1)
                            if((grid.getRow(z).getCellsContaining(candidate.intValue()).size() == 2 &&
                                grid.getRow(q).getCellsContaining(candidate.intValue()).size() == 2) ||
                                (grid.getColumn(i).getCellsContaining(candidate.intValue()).size() == 2 &&
                                grid.getColumn(j).getCellsContaining(candidate.intValue()).size() == 2))
                            {
                                if(can2.contains(candidate) && can3.contains(candidate) && can4.contains(candidate)){
                                    boolean found = false;
                                    for(Cell c: allcells){
                                        if(c.getValue() == 0){
                                            if(c.removeCandidate(candidate.intValue()))
                                                found = true;
                                        }
                                    }
                                    if(found) return success();
                                }
                            }
                            
                    }
                }
            }
        }       
        return 0;
    }
}
