/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sudoku.Solvers;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import sudoku.*;

/**
 *
 * @author Jay
 */
public class Swordfish extends Solver{

    public Swordfish(SudokuGrid g){
        super(g);
    }

    public int solve(){
        SudokuGrid grid = getGrid();
        for(int i=0; i<9; i++){
            for(int j=i+1; j<9; j++){
                for(int k=j+1; k<9; k++){
                    Row r1 = grid.getRow(i);
                    Row r2 = grid.getRow(j);
                    Row r3 = grid.getRow(k);
                    for(int v=0; v<9; v++){
                        ArrayList<Cell> can1 = r1.getCellsContaining(v);
                        if(can1.size() == 2 || can1.size() == 3){
                            ArrayList<Cell> can2 = r2.getCellsContaining(v);
                            if(can2.size() == 2 || can2.size() == 3){
                                ArrayList<Cell> can3 = r3.getCellsContaining(v);
                                if(can3.size() == 2 || can3.size() == 3){
                                    //We have thre rows, all w/ only two cells for this candidate
                                    //Check alignment
                                    Set<Integer> x = new HashSet<Integer>();
                                    for(Cell c: can1)
                                        x.add(c.getX());
                                    for(Cell c: can2)
                                        x.add(c.getX());
                                    for(Cell c: can3)
                                        x.add(c.getX());
                                    if(x.size()==3){
                                        boolean found = false;
                                        Set<Cell> allcells = new HashSet<Cell>();
                                        for(Integer a: x){
                                            allcells.addAll(Arrays.asList(grid.getColumn(a).toArray()));
                                        }
                                        allcells.removeAll(can1);
                                        allcells.removeAll(can2);
                                        allcells.removeAll(can3);
                                        Iterator<Cell> it = allcells.iterator();
                                        while(it.hasNext())
                                            if(it.next().getValue()!=0)
                                                it.remove();
                                        for(Cell c: allcells)
                                            if(c.removeCandidate(v))
                                                found = true;           
                                        if(found) return success();
                                    }
                                }
                            }
                        }
                    }
                    Column c1 = grid.getColumn(i);
                    Column c2 = grid.getColumn(j);
                    Column c3 = grid.getColumn(k);
                    for(int v=0; v<9; v++){
                        ArrayList<Cell> can1 = c1.getCellsContaining(v);
                        if(can1.size() == 2 || can1.size() == 3){
                            ArrayList<Cell> can2 = c2.getCellsContaining(v);
                            if(can2.size() == 2 || can2.size() == 3){
                                ArrayList<Cell> can3 = c3.getCellsContaining(v);
                                if(can3.size() == 2 || can3.size() == 3){
                                    //We have thre rows, all w/ only two cells for this candidate
                                    //Check alignment
                                    Set<Integer> x = new HashSet<Integer>();
                                    for(Cell c: can1)
                                        x.add(c.getY());
                                    for(Cell c: can2)
                                        x.add(c.getY());
                                    for(Cell c: can3)
                                        x.add(c.getY());
                                    if(x.size()==3){
                                        boolean found = false;
                                        Set<Cell> allcells = new HashSet<Cell>();
                                        for(Integer a: x){
                                            allcells.addAll(Arrays.asList(grid.getRow(a).toArray()));
                                        }
                                        allcells.removeAll(can1);
                                        allcells.removeAll(can2);
                                        allcells.removeAll(can3);
                                        Iterator<Cell> it = allcells.iterator();
                                        while(it.hasNext())
                                            if(it.next().getValue()!=0)
                                                it.remove();
                                        for(Cell c: allcells)
                                            if(c.removeCandidate(v))
                                                found = true;
                                        if(found) return success();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return 0;
    }
}
