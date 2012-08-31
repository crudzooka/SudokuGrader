/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sudoku;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 *
 * @author Jay
 */
public class SWindowListener implements WindowListener {

    private SudokuGraderGUI theGUI;

    public SWindowListener(SudokuGraderGUI gui){
        theGUI = gui;
    }

    public void windowOpened(WindowEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public void windowClosing(WindowEvent e) {
       // throw new UnsupportedOperationException("Not supported yet. Closing");
    }

    public void windowClosed(WindowEvent e) {
        theGUI.exit();
    }

    public void windowIconified(WindowEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public void windowDeiconified(WindowEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public void windowActivated(WindowEvent e) {
       // throw new UnsupportedOperationException("Not supported yet.");
    }

    public void windowDeactivated(WindowEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }


}
