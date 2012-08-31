/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sudoku;
import javax.swing.filechooser.FileFilter;
import java.io.File;
/**
 *
 * @author Jay
 */
public class SudokuFileFilter extends FileFilter{

    public SudokuFileFilter(){}

    public boolean accept(File f) {
      if ( f != null )
         {
         if ( f.isDirectory() )
            {
            return true;
            }
         String name = f.getName().toLowerCase();
         return (name.endsWith( ".sdg" )|| name.endsWith( ".sdk" ));
         }
      else
         {
         return false;
         }
      }

    //The description of this filter
    public String getDescription() {
        return "Sudoku Puzzles (.sdg)";
    }


}
