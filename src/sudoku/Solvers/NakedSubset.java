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
import javax.swing.JOptionPane;
import sudoku.*;

/**
 *
 * @author Jay
 */
public class NakedSubset extends Solver{

    private int size;
    
    public NakedSubset(SudokuGrid g, int s){
        super(g);
        size = s;
        
    }
    public int solve() {
        SudokuGrid grid = getGrid();
        for(int i=0; i<9; i++){
            ArrayList<ArrayList<Cell>> s = nakedOverlap(grid.getRow(i).toArray());
            if(s.size()!=0){
                for(ArrayList<Cell> q : s){
                    Set<Integer> remove = new HashSet<Integer>();
                    for(Cell c: q)
                        remove.addAll(c.getCandidates());
                    boolean found = false;
                    for(Cell c: grid.getRow(i).toArray()){
                        if(!q.contains(c)){
                            for(Integer x: remove)
                                if(c.removeCandidate(x.intValue())){
                                    found = true;
                                }
                        }
                    }
                    if(found) return success();
                }
            }
            s = nakedOverlap(grid.getColumn(i).toArray());
            if(s.size()!=0){
                for(ArrayList<Cell> q : s){
                    Set<Integer> remove = new HashSet<Integer>();
                    for(Cell c: q)
                        remove.addAll(c.getCandidates());
                    boolean found = false;
                    for(Cell c: grid.getColumn(i).toArray()){
                        if(!q.contains(c)){
                            for(Integer x: remove)
                                if(c.removeCandidate(x.intValue())){
                                    found = true;
                                }
                        }
                    }
                    if(found) return success();
                }
            }
            s = nakedOverlap(grid.getSmallBox(i).toArray());
            if(s.size()!=0){
                for(ArrayList<Cell> q : s){
                    Set<Integer> remove = new HashSet<Integer>();
                    for(Cell c: q)
                        remove.addAll(c.getCandidates());
                    boolean found = false;
                    for(Cell c: grid.getSmallBox(i).toArray()){
                        if(!q.contains(c)){
                            for(Integer x: remove)
                                if(c.removeCandidate(x.intValue())){
                                    found = true;
                                }
                        }
                    }
                    if(found) return success();
                }
            }
        }
        return 0;
    }
    
    public ArrayList<ArrayList<Cell>> nakedOverlap(Cell[] toc){
        ArrayList<Cell> toCompare = new ArrayList<Cell>();
        ArrayList<ArrayList<Cell>> ret = new ArrayList<ArrayList<Cell>>();
        toCompare.addAll(Arrays.asList(toc));
        
        //remove unneccessary comparisons;
        Iterator<Cell> q = toCompare.iterator();
        while(q.hasNext()){
            Cell c = q.next();
            if(c.getCandidates().size()==1 || c.getCandidates().size()> size)
                q.remove();
        }
        if(toCompare.size() < size) return ret;
        
        ArrayList<ArrayList<Cell>> possibilities = findCombinations(toCompare);
        for(ArrayList<Cell> poss : possibilities){
            HashSet<Integer> x = new HashSet<Integer>();
            for(Cell s: poss){
                x.addAll(s.getCandidates());
            }
            if(x.size()==size){
                //woohoo we have a naked subset!
                ret.add(poss);
            }
        }
        return ret;
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

    private void displaySituation(ArrayList<Cell> poss){
                  String st = "";
                for(Cell s: poss){
                    st+="Cell "+s.getX()+s.getY()+": ";
                    for(Integer a: s.getCandidates())
                        st+=""+a;
                    st+="\n";
                }
    }

}
