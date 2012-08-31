/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sudoku;

/**
 *
 * @author Jay
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GridButtonListener implements ActionListener{
    private SudokuGraderGUI theGUI;

    public GridButtonListener(SudokuGraderGUI gui){
        theGUI = gui;
    }

    public void actionPerformed(ActionEvent e){
        theGUI.gridActionPerformed(e);
    }
}
