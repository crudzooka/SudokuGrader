package sudoku;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import javax.swing.JOptionPane;
/*
The Actual Sudoku Grid Value
Functions like an Integer  
*/
public class Cell
{
    private SudokuGrid grid;
    private int value;
    private ArrayList<Integer> candidates;
    private int x;
    private int y;

    
    public Cell()
    {
        value = 0;
        candidates = new ArrayList<Integer>(9);
        x = 0;
        y = 0;
        grid = null;
    }
    
    public Cell(SudokuGrid tgrid, int v, int xC, int yC)
    {
        value = v;
        candidates = new ArrayList<Integer>(9);
        x = xC;
        y = yC;
        grid = tgrid;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }
    
    public int getValue()
    {
        return value;
    }
    
    public void setValue(int s)
    {
        value = s;
        candidates.clear();
        candidates.add(new Integer(s));
        updateCGConstraints();
    }

    public void clearCandidates(){
        candidates.clear();
    }

    public void findCandidates(){       
        candidates.clear();
        if(getValue()!=0){
            candidates.add(value);
            return;
        }
        int[] unavailable = new int[10];
        for(int i=0; i<10; i++) unavailable[i]=0;
        //Search Rows
            for(int i=0; i<9; i++)
                if(grid.getCellValue(x, i) != 0 && i != y)
                    unavailable[grid.getCellValue(x,i)]++;
        //Search Columns
            for(int i=0; i<9; i++)
                if(grid.getCellValue(i, y) != 0 && i != x)
                    unavailable[grid.getCellValue(i,y)]++;
        //Search  Blocks
            SmallBox sb = grid.getSmallBox(x/3 + 3*(y/3));
            //for(int i=0; i<9; i++) //System.out.print(sb.getCellValue(i));
            for(int i=0; i<9; i++)
                if(sb.getCellValue(i) != 0 && sb.getCell(i)!=this)
                    unavailable[sb.getCellValue(i)]++;
        for(int i=1; i<10; i++)
            if(unavailable[i] < 1)
                candidates.add(i);
    }

    public ArrayList<Integer> findCandidatesSimple(int dest){
        ArrayList<Integer> scandidates = new ArrayList<Integer>();
        scandidates.clear();
        if(getValue()!=0){
            scandidates.add(value);
            return scandidates;
        }
        int[] unavailable = new int[10];
        for(int i=0; i<10; i++) unavailable[i]=0;
        //Search Rows
        if(dest == SudokuGrid.ROW){
            for(int i=0; i<9; i++)
                if(grid.getCellValue(x, i) != 0 && i != y)
                    unavailable[grid.getCellValue(x,i)]++;
        }
        //Search Columns
        else if(dest == SudokuGrid.COLUMN){
            for(int i=0; i<9; i++)
                if(grid.getCellValue(i, y) != 0 && i != x)
                    unavailable[grid.getCellValue(i,y)]++;
        }
        //Search  Blocks
        else if(dest == SudokuGrid.SMALLBOX){
            SmallBox sb = grid.getSmallBox(x/3 + 3*(y/3));
            //for(int i=0; i<9; i++) //System.out.print(sb.getCellValue(i));
            for(int i=0; i<9; i++)
                if(sb.getCellValue(i) != 0 && sb.getCell(i)!=this)
                    unavailable[sb.getCellValue(i)]++;
        }
        for(int i=1; i<10; i++)
            if(unavailable[i] < 1)
                scandidates.add(i);
        return scandidates;
    }

    public ArrayList<Integer> getCandidates(){
        return candidates;
    }

    public boolean removeCandidate(int x){
        boolean removed = false;
        Iterator<Integer> li = candidates.listIterator();
        while (li.hasNext()){
            Integer element = li.next();
            if(element.intValue()==x){
                li.remove();
                removed = true;
            }
        }
        if(removed){
            //System.out.print(x+": ");
            print_candidates();
        }
        return removed;
    }

    public void updateCGConstraints(){
        if (value==0) return;
        HashSet<Cell> neighbors = new HashSet<Cell>();
        neighbors.addAll(Arrays.asList(grid.getRow(y).toArray()));
        neighbors.addAll(Arrays.asList(grid.getColumn(x).toArray()));
        neighbors.addAll(Arrays.asList(grid.getSmallBox((y/3)*3+x/3).toArray()));
        neighbors.remove(this);
        for(Cell c: neighbors)
            c.removeCandidate(value);
    }

    public ArrayList<Integer> findAndGetCandidates(){
        findCandidates();
        return candidates;
    }

    public void print_candidates(){
        //System.out.print("Candidates for ("+x+","+y+"): ");
        //for(Integer i: candidates)
            //System.out.print(i.intValue());
        //System.out.println();    
    }

    public void addCandidates(Collection<Integer> coll){
        candidates.addAll(coll);
    }

}