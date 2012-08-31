/*
 * Rewritten to take advantage of time savings from
 * int arrays only...
 */

package sudoku.Solvers;

/**
 *
 * @author Jay
 */
public class BruteForceSolver{
    
    public static int[][] solve(int[][] grid){
        int[] x = new int[1];
        int[][] retGrid = new int[9][9];
        solve(0,grid,retGrid,x);
        //retGrid contains either
        //  1. The solved grid
        //  2. -1 at [0][0] if multiple solutions
        //  3. 0 at [0][0] if no solution
        return retGrid;
    }

    private static void solve(int n, int[][] grid, int[][] rgrid, int[] x){
        //If we've found a second solution, we are done for the purposes of this solver
        if(x[0] > 1){
            rgrid[0][0] = -1;
            return;
        }
        if(n>80){
            for(int i=0; i<81; i++)
                rgrid[i%9][i/9] = grid[i%9][i/9];
            x[0]++;
            return;
        }
        if (grid[n%9][n/9]==0){
            for (int i=1; i<=9; i++){
                grid[n%9][n/9] = (i);
                if(isValidAddition(grid,n)){
                    solve(n+1,grid,rgrid,x);
                }
                grid[n%9][n/9]=(0);
            }
        }
        else{
            solve(n + 1,grid,rgrid,x);
        }
    }

    private static boolean isValidAddition(int[][] sud, int n){
        //Only checks additions. WAY more efficient.
        //Check Columns
                int occurrences = 0;
                int i = sud[n%9][n/9];
                for(int q=0; q<9 && occurrences < 2; q++){
                    if(sud[n%9][q]==i)
                        occurrences++;
                }
                if(occurrences>1) return false;
                //Check Rows
                occurrences = 0;
                for(int q=0; q<9 && occurrences < 2; q++){
                    if(sud[q][n/9]==i)
                        occurrences++;
                }
                if(occurrences>1) return false;
                //Check Boxes
                occurrences = 0;
                int xStart = ((n%9)/3)*3;
                int yStart = ((n/9)/3)*3;
                for(int q=0; q<9 && occurrences < 2; q++){
                    if(sud[xStart + q%3][yStart+q/3]==i)
                        occurrences++;
                }
                if(occurrences>1) return false;
                return true;
    }

    private static boolean isValidSudoku(int[][] sud){
        //check each digit in each row, column, and block
        for(int i=1; i<=9; i++){
            for(int j=0; j<9; j++){
                //Check Columns
                int occurrences = 0;
                for(int q=0; q<9 && occurrences < 2; q++){
                    if(sud[j][q]==i)
                        occurrences++;
                }
                if(occurrences>1) return false;
                //Check Rows
                occurrences = 0;
                for(int q=0; q<9 && occurrences < 2; q++){
                    if(sud[q][j]==i)
                        occurrences++;
                }
                if(occurrences>1) return false;
                //Check Boxes
                occurrences = 0;
                int xStart = (j%3)*3;
                int yStart = (j/3)*3;
                for(int q=0; q<9 && occurrences < 2; q++){
                    if(sud[xStart + q%3][yStart+q/3]==i)
                        occurrences++;
                }
                if(occurrences>1) return false;
            }
        }
        return true;
    }
    



}
