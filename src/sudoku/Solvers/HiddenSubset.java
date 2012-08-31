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
public class HiddenSubset extends Solver{

    private int size;
    
    public HiddenSubset(SudokuGrid grid, int s){
        super(grid);
        size = s;
    }
    
    public int solve(){
        SudokuGrid grid = getGrid();
        for(int i=0; i<9; i++){
            if(hiddenOverlap(grid.getSmallBox(i).toArray()) == 1)
                return 1;
            else if(hiddenOverlap(grid.getRow(i).toArray()) == 1)
                return 1;
            else if(hiddenOverlap(grid.getColumn(i).toArray()) == 1)
                return 1;
        }
        return 0;
    }
        
    public int hiddenOverlap(Cell[] toc){
        ArrayList<Cell> toCompare = new ArrayList<Cell>();
        ArrayList<ArrayList<Cell>> ret = new ArrayList<ArrayList<Cell>>();
        toCompare.addAll(Arrays.asList(toc));
        
        //remove unneccessary comparisons;
        Iterator<Cell> q = toCompare.iterator();
        while(q.hasNext()){
            Cell c = q.next();
            if(c.getCandidates().size()==1)
                q.remove();
        }
        if(toCompare.size() < size) return 0;
        
        ArrayList<ArrayList<Cell>> possibilities = findCombinations(toCompare);
        for(ArrayList<Cell> poss : possibilities){
            HashSet<Integer> x = new HashSet<Integer>();
            for(Cell s: poss){
                x.addAll(s.getCandidates());
            }
            Set<Integer> outside = new HashSet<Integer>();
            for(Cell s: toCompare){
                if(!poss.contains(s))
                    for(Integer w: s.getCandidates())
                        if(x.contains(w))
                            outside.add(w);
            }
            if((x.size() - outside.size()) == size){
                //we found a hidden subset :)
                //remove extraneous candidates from cells;
                boolean found = false;
                for(Cell c: poss){
                    for(Integer w: outside)
                        if(c.removeCandidate(w.intValue()))
                            found = true;
                }
                if(found) return success();
            }

        }
        return 0;
    }
    
    private ArrayList<ArrayList<Cell>> findCombinations(ArrayList<Cell> cells){
        ArrayList<ArrayList<Cell>> combos = new ArrayList<ArrayList<Cell>>();
        int dec = (int) Math.pow(2, cells.size());
        for(int i=dec-1; i>=0; i--){
            ArrayList<Cell> count = new ArrayList<Cell>();
            String s = Integer.toBinaryString(i);
            for(int w= s.length(); w<cells.size(); w++)
                s = "0" + s;
            for(int p=0; p < s.length(); p++){
                if(s.charAt(p) == '1')
                    count.add(cells.get(p));
            }
            if(count.size() == size)
                combos.add(count);
        }
        return combos;
    }
}
