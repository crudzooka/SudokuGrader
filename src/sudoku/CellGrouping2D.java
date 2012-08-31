package sudoku;

public abstract class CellGrouping2D
{
    private Cell[][] cells;
    
    public CellGrouping2D()
    {
        cells = new Cell[9][9];
    }
    
    public CellGrouping2D(Cell[][] c)
    {
        cells = c;
    }
    
    public Cell getCell(int x, int y)
    {
        return cells[x][y];
    }
    
    public int getCellValue(int x, int y)
    {
        return cells[x][y].getValue();
    }
    
    public void setCell(Cell c, int x, int y)
    {
        cells[x][y].setValue(c.getValue());
    }
    
    public void setCellValue(int a, int x, int y)
    {
        cells[x][y].setValue(a);
    }
}