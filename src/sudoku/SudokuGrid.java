package sudoku;

import java.util.ArrayList;

public class SudokuGrid
{
    private Cell[][] theGrid;
    private Row[] theRows;
    private Column[] theColumns;
    private SmallBox[] theSmallBoxes;
    private ArrayList<Cell> constraints;
    private ArrayList<Cell> availabilities;
    public static final int ROW = 11;
    public static final int COLUMN = 12;
    public static final int SMALLBOX = 13;


    public SudokuGrid()
    {
        theGrid = new Cell[9][9];
        for(int i=0; i<9; i++)
            for(int j=0; j<9; j++)
                theGrid[i][j] = new Cell(this,0,i,j);
        constraints = new ArrayList<Cell>(81);
        availabilities = new ArrayList<Cell>(81);
        setGroupings();
        updateConstraintsBrute();
    }


    public void updateConstraintsBrute(){
        for(int i=0; i<81; i++)
            if(theGrid[i/9][i%9].getValue() != 0){
                constraints.add(theGrid[i/9][i%9]);
            }
            else{
                availabilities.add(theGrid[i/9][i%9]);
            }    
    }

    public void updateAllCandidates(){
        for(int i=0; i<81; i++)
            theGrid[i/9][i%9].findCandidates();
    }

    public SudokuGrid(Cell[][] c)
    {
        theGrid = c;
        constraints = new ArrayList<Cell>(81);
        availabilities = new ArrayList<Cell>(81);
        setGroupings();
        updateConstraintsBrute();
    }

    public SudokuGrid(int[][] c){
        theGrid = new Cell[9][9];
        for(int i=0; i<9; i++)
            for(int j=0; j<9; j++){
                    theGrid[i][j] = new Cell(this,c[i][j],i,j);
            }
        availabilities = new ArrayList<Cell>(81);
        constraints = new ArrayList<Cell>(81);
        setGroupings();
        updateConstraintsBrute();
    }

    public Cell getCell(int x, int y)
    {
        return theGrid[x][y];
    }
    
    public int getCellValue(int x, int y)
    {
        return theGrid[x][y].getValue();
    }
    
    public void setCell(Cell c, int x, int y)
    {
        theGrid[x][y].setValue(c.getValue());
    }
    
    public void setCellValue(int a, int x, int y)
    {
        theGrid[x][y].setValue(a);
    }

    public Row getRow(int n){
        return theRows[n];
    }

    public Column getColumn(int n){
        return theColumns[n];
    }

    public SmallBox getSmallBox(int n){
        return theSmallBoxes[n];
    }
    
    public Row getRowOld(int n)
    {
        Cell[] theRow = new Cell[9];
        for(int i=0; i<9; i++)
            theRow[i] = theGrid[i][n];
        return new Row(theRow);
    }
    
    public SmallBox getSmallBoxOld(int n)
    {
        Cell[] theSB = new Cell[9];
        int xStart = (n%3)*3;
        int yStart = (n/3)*3;        
        for(int i=0, pos=0; i<3; i++)
            for(int j=0; j<3; j++)
            {
                theSB[pos] = theGrid[xStart+j][yStart+i];
                pos++;
            }
        return new SmallBox(theSB);
    }
    
    public Column getColumnOld(int n)
    {
        Cell[] theColumn = new Cell[9];
        for(int i=0; i<9; i++)
            theColumn[i] = theGrid[n][i];
        return new Column(theColumn);
    }
    
    public ColumnBlock getColumnBlock(int n)
    {
        Cell[][] theCB = new Cell[3][9];
        for(int i=0; i<9; i++)
            theCB[0][i] = theGrid[3*n][i];
        for(int i=0; i<9; i++)
            theCB[1][i] = theGrid[3*n+1][i];
        for(int i=0; i<9; i++)
            theCB[2][i] = theGrid[3*n+2][i];
        return new ColumnBlock(theCB);
    }

    public int constraintSize(){
        return constraints.size();
    }

    public void addConstraint(Cell c){
        availabilities.remove(c);
        constraints.add(c);
    }

    public void removeConstraint(Cell c){
        constraints.remove(c);
        availabilities.add(c);
    }

    public Cell getEmptyCell(int x){
        return availabilities.get(x);
    }

    public int numEmptyCells(){
        return availabilities.size();
    }

    public RowBlock  getRowBlock(int n)
    {
        Cell[][] theRB= new Cell[3][9];
        for(int i=0; i<9; i++)
            theRB[0][i] = theGrid[i][3*n];
        for(int i=0; i<9; i++)
            theRB[1][i] = theGrid[i][3*n+1];
        for(int i=0; i<9; i++)
            theRB[2][i] = theGrid[i][3*n+2];
        return new RowBlock(theRB);
    }

    public boolean isValid(){
        //Check rows
        for(int q=0; q<9; q++){
            Row rb = getRow(q);
            for(int i=1; i<=9; i++){
                int occurrences = 0;
                for(int j=0; j<9; j++)
                    if(rb.getCellValue(j) == i)
                        occurrences++;
                if(occurrences > 1)
                    return false;
            }
        }
        //Check columns
        for(int q=0; q<9; q++){
            Column cb = getColumn(q);
            for(int i=1; i<=9; i++){
                int occurrences = 0;
                for(int j=0; j<9; j++)
                    if(cb.getCellValue(j) == i)
                        occurrences++;
                if(occurrences > 1)
                    return false;
            }
        }
        //Check SmallBoxes
        for(int q=0; q<9; q++){
            SmallBox sb = getSmallBox(q);
            for(int i=1; i<=9; i++){
                int occurrences = 0;
                for(int j=0; j<9; j++)
                    if(sb.getCellValue(j) == i)
                        occurrences++;
                if(occurrences > 1)
                    return false;
            }
        }
        return true;
    }

    public int[][] toIntArray(){
        int[][] grid = new int[9][9];
         for(int i=0; i<9; i++)
            for(int j=0; j<9; j++)
                grid[i][j] = getCell(i,j).getValue();
         return grid;
    }

    public void setGroupings(){
        theColumns = new Column[9];
        theRows = new Row[9];
        theSmallBoxes = new SmallBox[9];
        for(int i=0; i<9; i++){
            theColumns[i] = getColumnOld(i);
            theRows[i] = getRowOld(i);
            theSmallBoxes[i] = getSmallBoxOld(i);
        }
    }
}
