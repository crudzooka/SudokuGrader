/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * SudokuGraderGUI.java
 *
 * Created on Nov 28, 2008, 5:51:16 PM
 */

package sudoku;
import java.awt.Cursor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import sudoku.Solvers.*;
/**
 *
 * @author Jay
 */
public class SudokuGraderGUI extends javax.swing.JFrame{
    private boolean running = false;
    private boolean debug = false;

    private class BruteRunner extends SwingWorker<int[][],Void>{
        private SudokuGrid grid;
        private SudokuGraderGUI gui;
        private JProgressBar bar;

        public BruteRunner(SudokuGrid g,  SudokuGraderGUI gu, JProgressBar b){
            grid = g;
            gui = gu;
            bar = b;
        }


        public int[][] doInBackground(){
          return BruteForceSolver.solve(grid.toIntArray());
        }

        @Override
        public void done(){
            try {
                int[][] solved = get();
                if(solved[0][0] == -1){
                    JOptionPane.showMessageDialog(gui, "This puzzle cannot be solved because it has multiple solutions.");
                    gui.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    bar.setIndeterminate(false);
                    bar.setStringPainted(false);
                }
                else if(solved[0][0] == 0){
                    JOptionPane.showMessageDialog(gui, "This puzzle cannot be solved because it has no solutions.");
                    gui.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    bar.setIndeterminate(false);
                    bar.setStringPainted(false);
                }
                else{
                    AlgorithmRunner al = new AlgorithmRunner(grid, gui, solved, bar);
                    al.execute();
                }
            } catch (ExecutionException ex) {
                gui.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                JOptionPane.showMessageDialog(gui, "Please wait until results are gathered before grading again.");
            } catch (InterruptedException ex){
                gui.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                JOptionPane.showMessageDialog(gui, "Please wait until results are gathered before grading again.");
            }
            finally{
                gui.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }

        }
    }

    private class AlgorithmRunner extends SwingWorker<Map<String,Solver>,Void>{
        private SudokuGrid grid;
        private SudokuGraderGUI gui;
        private int[][] solution;
        private JProgressBar bar;

        public AlgorithmRunner(SudokuGrid g,  SudokuGraderGUI gu, int[][] sol, JProgressBar b){
            grid = g;
            gui = gu;
            solution = sol;
            bar = b;
        }


        public Map<String,Solver> doInBackground(){
          Map<String, Solver> solvers = new HashMap<String, Solver>();
                solvers.put("Simple Single", new SimpleSingle(grid));
                solvers.put("Slice and Slot", new SliceAndSlot(grid));
                solvers.put("Single", new Single(grid));
                solvers.put("Hidden Single", new HiddenSingle(grid));
                solvers.put("Locked Candidate", new LockedCandidate1(grid));
                solvers.put("Locked Candidate2", new LockedCandidate2(grid));
                solvers.put("Naked Pair",new NakedSubset(grid,2));
                solvers.put("Naked Triple", new NakedSubset(grid,3));
                solvers.put("Naked Quad", new NakedSubset(grid,4));
                solvers.put("Hidden Pair", new HiddenSubset(grid,2));
                solvers.put("Hidden Triple",new HiddenSubset(grid,3));
                solvers.put("Hidden Quad",new HiddenSubset(grid,4));
                solvers.put("X-Wing", new XWing(grid));
                solvers.put("Swordfish", new Swordfish(grid));
                int q=0;
                grid.updateAllCandidates();
                while(q<100 && grid.numEmptyCells() != 0){
                    if(solvers.get("Simple Single").solve() ==1);
                    else if(solvers.get("Slice and Slot").solve() ==1);
                    else if(solvers.get("Single").solve() ==1);
                    else if(solvers.get("Hidden Single").solve() ==1);
                    else if(solvers.get("Locked Candidate").solve() ==1);
                    else if(solvers.get("Locked Candidate2").solve() ==1);
                    else if(solvers.get("Naked Pair").solve() ==1);
                    else if(solvers.get("Naked Triple").solve() ==1);
                    else if(solvers.get("Naked Quad").solve() ==1);
                    else if(solvers.get("Hidden Pair").solve() ==1);
                    else if(solvers.get("Hidden Triple").solve() ==1);
                    else if(solvers.get("Hidden Quad").solve() ==1);
                    else if(solvers.get("X-Wing").solve() ==1);
                    else if(solvers.get("Swordfish").solve() ==1);
                    else break;
                    q++;
            }
            return solvers;
        }

        @Override
        public void done(){
            try{
            Map<String, Solver> solvers = get();
                 displayGrid(grid);
                if(grid.numEmptyCells() != 0){
                    displayGrid(new SudokuGrid(solution));
                    bar.setIndeterminate(false);
                    bar.setStringPainted(false);
                    JOptionPane.showMessageDialog(gui, "This puzzle has a strategic difficulty of 7/7 \nand a procedural difficulty of 3/3 \nfor a combined difficulty of 10/10.");
                }
                else{
                    int sdifficulty = 0;
                    int odifficulty = 0;
                    int sumruns = 0;
                    Map<String,Integer> runs = new HashMap<String,Integer>();
                    for(String s : solvers.keySet()){
                        runs.put(s, solvers.get(s).getRuns());
                        sumruns += solvers.get(s).getRuns();
                        if(debug)
                            System.out.println(s+": "+solvers.get(s).getRuns());
                    }
                    int sumnonelim = sumruns - runs.get("Single") - runs.get("Hidden Single") - runs.get("Simple Single") - runs.get("Slice and Slot");
                    if(debug)
                            System.out.println("Runs: "+sumruns+", Non-elim: "+sumnonelim);
                    if(runs.get("X-Wing")>0 || runs.get("Swordfish") > 0)
                        sdifficulty = 6;
                    else if(runs.get("Hidden Quad") > 0)
                        sdifficulty = 5;
                    else if(runs.get("Naked Quad") >0 || runs.get("Hidden Triple") > 0)
                        sdifficulty = 4;
                    else if(runs.get("Hidden Pair") > 0)
                        sdifficulty = 4;
                    else if(runs.get("Naked Pair") > 0 || runs.get("Naked Triple") > 0)
                        sdifficulty = 3;
                    else if(runs.get("Locked Candidate") > 0 || runs.get("Locked Candidate2") > 0)
                        sdifficulty = 2;
                    else if(runs.get("Hidden Single") > 0 || runs.get("Single") > 0)
                        sdifficulty = 1;
                    if(sumnonelim >= 4)
                        odifficulty++;
                    if(sumnonelim >= 8)
                        odifficulty++;
                    if(sumruns - sumnonelim > 55)
                        odifficulty++;
                    bar.setIndeterminate(false);
                    bar.setStringPainted(false);
                    JOptionPane.showMessageDialog(gui, "This puzzle has a strategic difficulty of "+sdifficulty+"/7 \nand a procedural difficulty of "+odifficulty+"/3 \nfor a combined difficulty of "+(odifficulty + sdifficulty)+"/10.");
                }
            } catch (ExecutionException ex) {
                JOptionPane.showMessageDialog(gui, "Please wait until results are gathered before grading again.");
                gui.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            } catch (InterruptedException ex){
                JOptionPane.showMessageDialog(gui, "Please wait until results are gathered before grading again.");
                gui.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
            finally{
                gui.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        }


    }


    /** Creates new form SudokuGraderGUI */
    public SudokuGraderGUI() {
        initComponents();
        addListeners();
        addWindowListener(new SWindowListener(this));
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        openFC = new javax.swing.JFileChooser();
        saveFC = new javax.swing.JFileChooser();
        gridPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        grid00 = new javax.swing.JButton();
        grid10 = new javax.swing.JButton();
        grid20 = new javax.swing.JButton();
        grid01 = new javax.swing.JButton();
        grid11 = new javax.swing.JButton();
        grid21 = new javax.swing.JButton();
        grid02 = new javax.swing.JButton();
        grid12 = new javax.swing.JButton();
        grid22 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        grid30 = new javax.swing.JButton();
        grid40 = new javax.swing.JButton();
        grid50 = new javax.swing.JButton();
        grid31 = new javax.swing.JButton();
        grid41 = new javax.swing.JButton();
        grid51 = new javax.swing.JButton();
        grid32 = new javax.swing.JButton();
        grid42 = new javax.swing.JButton();
        grid52 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        grid60 = new javax.swing.JButton();
        grid70 = new javax.swing.JButton();
        grid80 = new javax.swing.JButton();
        grid61 = new javax.swing.JButton();
        grid71 = new javax.swing.JButton();
        grid81 = new javax.swing.JButton();
        grid62 = new javax.swing.JButton();
        grid72 = new javax.swing.JButton();
        grid82 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        grid03 = new javax.swing.JButton();
        grid13 = new javax.swing.JButton();
        grid23 = new javax.swing.JButton();
        grid04 = new javax.swing.JButton();
        grid14 = new javax.swing.JButton();
        grid24 = new javax.swing.JButton();
        grid05 = new javax.swing.JButton();
        grid15 = new javax.swing.JButton();
        grid25 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        grid33 = new javax.swing.JButton();
        grid43 = new javax.swing.JButton();
        grid53 = new javax.swing.JButton();
        grid34 = new javax.swing.JButton();
        grid44 = new javax.swing.JButton();
        grid54 = new javax.swing.JButton();
        grid35 = new javax.swing.JButton();
        grid45 = new javax.swing.JButton();
        grid55 = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        grid63 = new javax.swing.JButton();
        grid73 = new javax.swing.JButton();
        grid83 = new javax.swing.JButton();
        grid64 = new javax.swing.JButton();
        grid74 = new javax.swing.JButton();
        grid84 = new javax.swing.JButton();
        grid65 = new javax.swing.JButton();
        grid75 = new javax.swing.JButton();
        grid85 = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        grid06 = new javax.swing.JButton();
        grid16 = new javax.swing.JButton();
        grid26 = new javax.swing.JButton();
        grid07 = new javax.swing.JButton();
        grid17 = new javax.swing.JButton();
        grid27 = new javax.swing.JButton();
        grid08 = new javax.swing.JButton();
        grid18 = new javax.swing.JButton();
        grid28 = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        grid36 = new javax.swing.JButton();
        grid46 = new javax.swing.JButton();
        grid56 = new javax.swing.JButton();
        grid37 = new javax.swing.JButton();
        grid47 = new javax.swing.JButton();
        grid57 = new javax.swing.JButton();
        grid38 = new javax.swing.JButton();
        grid48 = new javax.swing.JButton();
        grid58 = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();
        grid66 = new javax.swing.JButton();
        grid76 = new javax.swing.JButton();
        grid86 = new javax.swing.JButton();
        grid67 = new javax.swing.JButton();
        grid77 = new javax.swing.JButton();
        grid87 = new javax.swing.JButton();
        grid68 = new javax.swing.JButton();
        grid78 = new javax.swing.JButton();
        grid88 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        gradeProgressBar = new javax.swing.JProgressBar();
        mainMenu = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        openMenuButton = new javax.swing.JMenuItem();
        saveMenuButton = new javax.swing.JMenuItem();
        clearMenuButton = new javax.swing.JMenuItem();
        exitMenuButton = new javax.swing.JMenuItem();
        gradeMenu = new javax.swing.JMenu();
        gradeMenuButton = new javax.swing.JMenuItem();
        prefMenu = new javax.swing.JMenu();
        soldispCB = new javax.swing.JCheckBoxMenuItem();

        openFC.setAcceptAllFileFilterUsed(false);
        openFC.setFileFilter(new SudokuFileFilter());

        saveFC.setAcceptAllFileFilterUsed(false);
        saveFC.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);
        saveFC.setFileFilter(new SudokuFileFilter());

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Sudoku Grader by Jay Hines");
        setBounds(new java.awt.Rectangle(0, 0, 0, 0));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setResizable(false);

        gridPanel.setPreferredSize(new java.awt.Dimension(400, 400));
        gridPanel.setLayout(new java.awt.GridLayout(3, 3));

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        jPanel1.setLayout(new java.awt.GridLayout(3, 3));

        grid00.setBackground(new java.awt.Color(255, 255, 255));
        grid00.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        grid00.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(grid00);

        grid10.setBackground(new java.awt.Color(255, 255, 255));
        grid10.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        grid10.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(grid10);

        grid20.setBackground(new java.awt.Color(255, 255, 255));
        grid20.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        grid20.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(grid20);

        grid01.setBackground(new java.awt.Color(255, 255, 255));
        grid01.setFont(new java.awt.Font("Tahoma", 1, 24));
        grid01.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(grid01);

        grid11.setBackground(new java.awt.Color(255, 255, 255));
        grid11.setFont(new java.awt.Font("Tahoma", 1, 24));
        grid11.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(grid11);

        grid21.setBackground(new java.awt.Color(255, 255, 255));
        grid21.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        grid21.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(grid21);

        grid02.setBackground(new java.awt.Color(255, 255, 255));
        grid02.setFont(new java.awt.Font("Tahoma", 1, 24));
        grid02.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(grid02);

        grid12.setBackground(new java.awt.Color(255, 255, 255));
        grid12.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        grid12.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(grid12);

        grid22.setBackground(new java.awt.Color(255, 255, 255));
        grid22.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        grid22.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(grid22);

        gridPanel.add(jPanel1);

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        jPanel2.setLayout(new java.awt.GridLayout(3, 3));

        grid30.setBackground(new java.awt.Color(255, 255, 255));
        grid30.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        grid30.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.add(grid30);

        grid40.setBackground(new java.awt.Color(255, 255, 255));
        grid40.setFont(new java.awt.Font("Tahoma", 1, 24));
        grid40.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.add(grid40);

        grid50.setBackground(new java.awt.Color(255, 255, 255));
        grid50.setFont(new java.awt.Font("Tahoma", 1, 24));
        grid50.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.add(grid50);

        grid31.setBackground(new java.awt.Color(255, 255, 255));
        grid31.setFont(new java.awt.Font("Tahoma", 1, 24));
        grid31.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.add(grid31);

        grid41.setBackground(new java.awt.Color(255, 255, 255));
        grid41.setFont(new java.awt.Font("Tahoma", 1, 24));
        grid41.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.add(grid41);

        grid51.setBackground(new java.awt.Color(255, 255, 255));
        grid51.setFont(new java.awt.Font("Tahoma", 1, 24));
        grid51.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.add(grid51);

        grid32.setBackground(new java.awt.Color(255, 255, 255));
        grid32.setFont(new java.awt.Font("Tahoma", 1, 24));
        grid32.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.add(grid32);

        grid42.setBackground(new java.awt.Color(255, 255, 255));
        grid42.setFont(new java.awt.Font("Tahoma", 1, 24));
        grid42.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.add(grid42);

        grid52.setBackground(new java.awt.Color(255, 255, 255));
        grid52.setFont(new java.awt.Font("Tahoma", 1, 24));
        grid52.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.add(grid52);

        gridPanel.add(jPanel2);

        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        jPanel3.setLayout(new java.awt.GridLayout(3, 3));

        grid60.setBackground(new java.awt.Color(255, 255, 255));
        grid60.setFont(new java.awt.Font("Tahoma", 1, 24));
        grid60.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel3.add(grid60);

        grid70.setBackground(new java.awt.Color(255, 255, 255));
        grid70.setFont(new java.awt.Font("Tahoma", 1, 24));
        grid70.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel3.add(grid70);

        grid80.setBackground(new java.awt.Color(255, 255, 255));
        grid80.setFont(new java.awt.Font("Tahoma", 1, 24));
        grid80.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel3.add(grid80);

        grid61.setBackground(new java.awt.Color(255, 255, 255));
        grid61.setFont(new java.awt.Font("Tahoma", 1, 24));
        grid61.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel3.add(grid61);

        grid71.setBackground(new java.awt.Color(255, 255, 255));
        grid71.setFont(new java.awt.Font("Tahoma", 1, 24));
        grid71.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel3.add(grid71);

        grid81.setBackground(new java.awt.Color(255, 255, 255));
        grid81.setFont(new java.awt.Font("Tahoma", 1, 24));
        grid81.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel3.add(grid81);

        grid62.setBackground(new java.awt.Color(255, 255, 255));
        grid62.setFont(new java.awt.Font("Tahoma", 1, 24));
        grid62.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel3.add(grid62);

        grid72.setBackground(new java.awt.Color(255, 255, 255));
        grid72.setFont(new java.awt.Font("Tahoma", 1, 24));
        grid72.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel3.add(grid72);

        grid82.setBackground(new java.awt.Color(255, 255, 255));
        grid82.setFont(new java.awt.Font("Tahoma", 1, 24));
        grid82.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel3.add(grid82);

        gridPanel.add(jPanel3);

        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        jPanel4.setLayout(new java.awt.GridLayout(3, 3));

        grid03.setBackground(new java.awt.Color(255, 255, 255));
        grid03.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        grid03.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel4.add(grid03);

        grid13.setBackground(new java.awt.Color(255, 255, 255));
        grid13.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        grid13.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel4.add(grid13);

        grid23.setBackground(new java.awt.Color(255, 255, 255));
        grid23.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        grid23.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel4.add(grid23);

        grid04.setBackground(new java.awt.Color(255, 255, 255));
        grid04.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        grid04.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel4.add(grid04);

        grid14.setBackground(new java.awt.Color(255, 255, 255));
        grid14.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        grid14.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel4.add(grid14);

        grid24.setBackground(new java.awt.Color(255, 255, 255));
        grid24.setFont(new java.awt.Font("Tahoma", 1, 24));
        grid24.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel4.add(grid24);

        grid05.setBackground(new java.awt.Color(255, 255, 255));
        grid05.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        grid05.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel4.add(grid05);

        grid15.setBackground(new java.awt.Color(255, 255, 255));
        grid15.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        grid15.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel4.add(grid15);

        grid25.setBackground(new java.awt.Color(255, 255, 255));
        grid25.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        grid25.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel4.add(grid25);

        gridPanel.add(jPanel4);

        jPanel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        jPanel5.setLayout(new java.awt.GridLayout(3, 3));

        grid33.setBackground(new java.awt.Color(255, 255, 255));
        grid33.setFont(new java.awt.Font("Tahoma", 1, 24));
        grid33.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel5.add(grid33);

        grid43.setBackground(new java.awt.Color(255, 255, 255));
        grid43.setFont(new java.awt.Font("Tahoma", 1, 24));
        grid43.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel5.add(grid43);

        grid53.setBackground(new java.awt.Color(255, 255, 255));
        grid53.setFont(new java.awt.Font("Tahoma", 1, 24));
        grid53.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel5.add(grid53);

        grid34.setBackground(new java.awt.Color(255, 255, 255));
        grid34.setFont(new java.awt.Font("Tahoma", 1, 24));
        grid34.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel5.add(grid34);

        grid44.setBackground(new java.awt.Color(255, 255, 255));
        grid44.setFont(new java.awt.Font("Tahoma", 1, 24));
        grid44.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel5.add(grid44);

        grid54.setBackground(new java.awt.Color(255, 255, 255));
        grid54.setFont(new java.awt.Font("Tahoma", 1, 24));
        grid54.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel5.add(grid54);

        grid35.setBackground(new java.awt.Color(255, 255, 255));
        grid35.setFont(new java.awt.Font("Tahoma", 1, 24));
        grid35.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel5.add(grid35);

        grid45.setBackground(new java.awt.Color(255, 255, 255));
        grid45.setFont(new java.awt.Font("Tahoma", 1, 24));
        grid45.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel5.add(grid45);

        grid55.setBackground(new java.awt.Color(255, 255, 255));
        grid55.setFont(new java.awt.Font("Tahoma", 1, 24));
        grid55.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel5.add(grid55);

        gridPanel.add(jPanel5);

        jPanel6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        jPanel6.setLayout(new java.awt.GridLayout(3, 3));

        grid63.setBackground(new java.awt.Color(255, 255, 255));
        grid63.setFont(new java.awt.Font("Tahoma", 1, 24));
        grid63.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel6.add(grid63);

        grid73.setBackground(new java.awt.Color(255, 255, 255));
        grid73.setFont(new java.awt.Font("Tahoma", 1, 24));
        grid73.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel6.add(grid73);

        grid83.setBackground(new java.awt.Color(255, 255, 255));
        grid83.setFont(new java.awt.Font("Tahoma", 1, 24));
        grid83.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel6.add(grid83);

        grid64.setBackground(new java.awt.Color(255, 255, 255));
        grid64.setFont(new java.awt.Font("Tahoma", 1, 24));
        grid64.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel6.add(grid64);

        grid74.setBackground(new java.awt.Color(255, 255, 255));
        grid74.setFont(new java.awt.Font("Tahoma", 1, 24));
        grid74.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel6.add(grid74);

        grid84.setBackground(new java.awt.Color(255, 255, 255));
        grid84.setFont(new java.awt.Font("Tahoma", 1, 24));
        grid84.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel6.add(grid84);

        grid65.setBackground(new java.awt.Color(255, 255, 255));
        grid65.setFont(new java.awt.Font("Tahoma", 1, 24));
        grid65.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel6.add(grid65);

        grid75.setBackground(new java.awt.Color(255, 255, 255));
        grid75.setFont(new java.awt.Font("Tahoma", 1, 24));
        grid75.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel6.add(grid75);

        grid85.setBackground(new java.awt.Color(255, 255, 255));
        grid85.setFont(new java.awt.Font("Tahoma", 1, 24));
        grid85.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel6.add(grid85);

        gridPanel.add(jPanel6);

        jPanel7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        jPanel7.setLayout(new java.awt.GridLayout(3, 3));

        grid06.setBackground(new java.awt.Color(255, 255, 255));
        grid06.setFont(new java.awt.Font("Tahoma", 1, 24));
        grid06.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel7.add(grid06);

        grid16.setBackground(new java.awt.Color(255, 255, 255));
        grid16.setFont(new java.awt.Font("Tahoma", 1, 24));
        grid16.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel7.add(grid16);

        grid26.setBackground(new java.awt.Color(255, 255, 255));
        grid26.setFont(new java.awt.Font("Tahoma", 1, 24));
        grid26.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel7.add(grid26);

        grid07.setBackground(new java.awt.Color(255, 255, 255));
        grid07.setFont(new java.awt.Font("Tahoma", 1, 24));
        grid07.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel7.add(grid07);

        grid17.setBackground(new java.awt.Color(255, 255, 255));
        grid17.setFont(new java.awt.Font("Tahoma", 1, 24));
        grid17.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel7.add(grid17);

        grid27.setBackground(new java.awt.Color(255, 255, 255));
        grid27.setFont(new java.awt.Font("Tahoma", 1, 24));
        grid27.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel7.add(grid27);

        grid08.setBackground(new java.awt.Color(255, 255, 255));
        grid08.setFont(new java.awt.Font("Tahoma", 1, 24));
        grid08.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel7.add(grid08);

        grid18.setBackground(new java.awt.Color(255, 255, 255));
        grid18.setFont(new java.awt.Font("Tahoma", 1, 24));
        grid18.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel7.add(grid18);

        grid28.setBackground(new java.awt.Color(255, 255, 255));
        grid28.setFont(new java.awt.Font("Tahoma", 1, 24));
        grid28.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel7.add(grid28);

        gridPanel.add(jPanel7);

        jPanel8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        jPanel8.setLayout(new java.awt.GridLayout(3, 3));

        grid36.setBackground(new java.awt.Color(255, 255, 255));
        grid36.setFont(new java.awt.Font("Tahoma", 1, 24));
        grid36.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel8.add(grid36);

        grid46.setBackground(new java.awt.Color(255, 255, 255));
        grid46.setFont(new java.awt.Font("Tahoma", 1, 24));
        grid46.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel8.add(grid46);

        grid56.setBackground(new java.awt.Color(255, 255, 255));
        grid56.setFont(new java.awt.Font("Tahoma", 1, 24));
        grid56.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel8.add(grid56);

        grid37.setBackground(new java.awt.Color(255, 255, 255));
        grid37.setFont(new java.awt.Font("Tahoma", 1, 24));
        grid37.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel8.add(grid37);

        grid47.setBackground(new java.awt.Color(255, 255, 255));
        grid47.setFont(new java.awt.Font("Tahoma", 1, 24));
        grid47.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel8.add(grid47);

        grid57.setBackground(new java.awt.Color(255, 255, 255));
        grid57.setFont(new java.awt.Font("Tahoma", 1, 24));
        grid57.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel8.add(grid57);

        grid38.setBackground(new java.awt.Color(255, 255, 255));
        grid38.setFont(new java.awt.Font("Tahoma", 1, 24));
        grid38.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel8.add(grid38);

        grid48.setBackground(new java.awt.Color(255, 255, 255));
        grid48.setFont(new java.awt.Font("Tahoma", 1, 24));
        grid48.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel8.add(grid48);

        grid58.setBackground(new java.awt.Color(255, 255, 255));
        grid58.setFont(new java.awt.Font("Tahoma", 1, 24));
        grid58.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel8.add(grid58);

        gridPanel.add(jPanel8);

        jPanel9.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        jPanel9.setLayout(new java.awt.GridLayout(3, 3));

        grid66.setBackground(new java.awt.Color(255, 255, 255));
        grid66.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        grid66.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel9.add(grid66);

        grid76.setBackground(new java.awt.Color(255, 255, 255));
        grid76.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        grid76.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel9.add(grid76);

        grid86.setBackground(new java.awt.Color(255, 255, 255));
        grid86.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        grid86.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel9.add(grid86);

        grid67.setBackground(new java.awt.Color(255, 255, 255));
        grid67.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        grid67.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel9.add(grid67);

        grid77.setBackground(new java.awt.Color(255, 255, 255));
        grid77.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        grid77.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel9.add(grid77);

        grid87.setBackground(new java.awt.Color(255, 255, 255));
        grid87.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        grid87.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel9.add(grid87);

        grid68.setBackground(new java.awt.Color(255, 255, 255));
        grid68.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        grid68.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel9.add(grid68);

        grid78.setBackground(new java.awt.Color(255, 255, 255));
        grid78.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        grid78.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel9.add(grid78);

        grid88.setBackground(new java.awt.Color(255, 255, 255));
        grid88.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        grid88.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel9.add(grid88);

        gridPanel.add(jPanel9);

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Enter Puzzle Below");

        gradeProgressBar.setString("Grading...");

        fileMenu.setText("File");

        openMenuButton.setText("Open Puzzle");
        openMenuButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openMenuButtonActionPerformed(evt);
            }
        });
        fileMenu.add(openMenuButton);

        saveMenuButton.setText("Save Puzzle");
        saveMenuButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveMenuButtonActionPerformed(evt);
            }
        });
        fileMenu.add(saveMenuButton);

        clearMenuButton.setText("Clear Board");
        clearMenuButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearMenuButtonActionPerformed(evt);
            }
        });
        fileMenu.add(clearMenuButton);

        exitMenuButton.setText("Exit");
        exitMenuButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuButtonActionPerformed(evt);
            }
        });
        fileMenu.add(exitMenuButton);

        mainMenu.add(fileMenu);

        gradeMenu.setText("Grade");
        gradeMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gradeMenuActionPerformed(evt);
            }
        });

        gradeMenuButton.setText("Grade Puzzle");
        gradeMenuButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gradeMenuActionPerformed(evt);
            }
        });
        gradeMenu.add(gradeMenuButton);

        mainMenu.add(gradeMenu);

        prefMenu.setText("Preferences");
        prefMenu.setBorderPainted(true);

        soldispCB.setText("Display Solution");
        prefMenu.add(soldispCB);

        mainMenu.add(prefMenu);

        setJMenuBar(mainMenu);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
                    .addComponent(gridPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
                    .addComponent(gradeProgressBar, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
                .addComponent(gridPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(gradeProgressBar, javax.swing.GroupLayout.DEFAULT_SIZE, 21, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    public void gridActionPerformed(java.awt.event.ActionEvent evt){
        ((javax.swing.JButton)evt.getSource()).setText(promptValue());
    }
    private String promptValue() {
        String[] choices = { "","1", "2", "3", "4", "5", "6", "7","8","9" };
        String input = (String) javax.swing.JOptionPane.showInputDialog(this, "Choose value...",
        "Value Input", javax.swing.JOptionPane.QUESTION_MESSAGE, null,
        choices, // Array of choices
        choices[0]); // Initial choice
        return input;
    }
    private void saveMenuButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveMenuButtonActionPerformed
        String s = "";
        int[][] theGrid = gridToArray();
        for(int i=0; i<9; i++){
            for(int j=0; j<9; j++){
                s+=theGrid[j][i];
            }
            if(i!=8) s+="\n";
        }
        if(saveFC.showSaveDialog(this) ==  javax.swing.JFileChooser.APPROVE_OPTION){
            File file = saveFC.getSelectedFile();
            String fileName = file.getName();
            if(fileName.indexOf(".sdg") == -1){
                fileName+=".sdg";
                file = new File(file.getParentFile(), fileName);
            }
            try {
                //javax.swing.JOptionPane.showMessageDialog(null, file);
                Writer output = new BufferedWriter(new FileWriter(file));
                try {
                    //FileWriter always assumes default encoding is OK!
                    output.write(s);
                }
                catch(Exception e){
                    javax.swing.JOptionPane.showMessageDialog(this, "There was a problem saving the file:\n "+e);
                }
                finally {
                    output.close();
                }
            } catch (IOException ex) {
                javax.swing.JOptionPane.showMessageDialog(this, "There was a problem saving the file:\n "+ex);
            }

        }
    }//GEN-LAST:event_saveMenuButtonActionPerformed

    private void arrayToGrid(String[][] tG){
        //replace zeroes with empty strings
        for(int i=0; i<9; i++)
            for(int j=0; j<9; j++)
                if(tG[i][j].equals("0") || tG[i][j].equals("."))
                    tG[i][j] = "";
        grid00.setText(tG[0][0]);
        grid01.setText(tG[0][1]);
        grid02.setText(tG[0][2]);
        grid03.setText(tG[0][3]);
        grid04.setText(tG[0][4]);
        grid05.setText(tG[0][5]);
        grid06.setText(tG[0][6]);
        grid07.setText(tG[0][7]);
        grid08.setText(tG[0][8]);
        grid10.setText(tG[1][0]);
        grid11.setText(tG[1][1]);
        grid12.setText(tG[1][2]);
        grid13.setText(tG[1][3]);
        grid14.setText(tG[1][4]);
        grid15.setText(tG[1][5]);
        grid16.setText(tG[1][6]);
        grid17.setText(tG[1][7]);
        grid18.setText(tG[1][8]);
        grid20.setText(tG[2][0]);
        grid21.setText(tG[2][1]);
        grid22.setText(tG[2][2]);
        grid23.setText(tG[2][3]);
        grid24.setText(tG[2][4]);
        grid25.setText(tG[2][5]);
        grid26.setText(tG[2][6]);
        grid27.setText(tG[2][7]);
        grid28.setText(tG[2][8]);
        grid30.setText(tG[3][0]);
        grid31.setText(tG[3][1]);
        grid32.setText(tG[3][2]);
        grid33.setText(tG[3][3]);
        grid34.setText(tG[3][4]);
        grid35.setText(tG[3][5]);
        grid36.setText(tG[3][6]);
        grid37.setText(tG[3][7]);
        grid38.setText(tG[3][8]);
        grid40.setText(tG[4][0]);
        grid41.setText(tG[4][1]);
        grid42.setText(tG[4][2]);
        grid43.setText(tG[4][3]);
        grid44.setText(tG[4][4]);
        grid45.setText(tG[4][5]);
        grid46.setText(tG[4][6]);
        grid47.setText(tG[4][7]);
        grid48.setText(tG[4][8]);
        grid50.setText(tG[5][0]);
        grid51.setText(tG[5][1]);
        grid52.setText(tG[5][2]);
        grid53.setText(tG[5][3]);
        grid54.setText(tG[5][4]);
        grid55.setText(tG[5][5]);
        grid56.setText(tG[5][6]);
        grid57.setText(tG[5][7]);
        grid58.setText(tG[5][8]);
        grid60.setText(tG[6][0]);
        grid61.setText(tG[6][1]);
        grid62.setText(tG[6][2]);
        grid63.setText(tG[6][3]);
        grid64.setText(tG[6][4]);
        grid65.setText(tG[6][5]);
        grid66.setText(tG[6][6]);
        grid67.setText(tG[6][7]);
        grid68.setText(tG[6][8]);
        grid70.setText(tG[7][0]);
        grid71.setText(tG[7][1]);
        grid72.setText(tG[7][2]);
        grid73.setText(tG[7][3]);
        grid74.setText(tG[7][4]);
        grid75.setText(tG[7][5]);
        grid76.setText(tG[7][6]);
        grid77.setText(tG[7][7]);
        grid78.setText(tG[7][8]);
        grid80.setText(tG[8][0]);
        grid81.setText(tG[8][1]);
        grid82.setText(tG[8][2]);
        grid83.setText(tG[8][3]);
        grid84.setText(tG[8][4]);
        grid85.setText(tG[8][5]);
        grid86.setText(tG[8][6]);
        grid87.setText(tG[8][7]);
        grid88.setText(tG[8][8]);
    }

    private void openMenuButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openMenuButtonActionPerformed
        if(openFC.showOpenDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION)
        {
            File file = openFC.getSelectedFile();
            String str = "";
            try{
                Scanner s = new Scanner(file);
                try{
                    while(s.hasNextLine())
                        str+=s.nextLine();
                    if(str.length() != 81)
                        throw new IOException("Puzzle Not 9x9");
                    String[][] theGrid = new String[9][9];
                    for(int i=0; i<81; i++){
                        if(parseInt(""+str.charAt(i)) > 9 || parseInt(""+str.charAt(i)) < 0)
                                throw new IOException("Values in the Puzzle Are Too Big");
                        theGrid[i%9][i/9] = "" + str.charAt(i);
                    }
                    arrayToGrid(theGrid);
                }
                catch(Exception e){
                    javax.swing.JOptionPane.showMessageDialog(this, "There was a problem with the file:\n"+e);
                }
                finally{
                    s.close();
                }
            } catch(Exception e){
                javax.swing.JOptionPane.showMessageDialog(this, "The file could not be found:\n"+e);
            }
        }



}//GEN-LAST:event_openMenuButtonActionPerformed

    private void exitMenuButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuButtonActionPerformed
        exit();
    }//GEN-LAST:event_exitMenuButtonActionPerformed

    private void gradeMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gradeMenuActionPerformed
        SudokuGrid grid = new SudokuGrid(gridToArray());
        int[][] ag = gridToArray();
        /*for(int i=0; i<9; i++){
        for(int j=0; j<9; j++){
        //System.out.print(ag[j][i]);
        }
        //System.out.print("  ");
        for(int j=0; j<9; j++){
        //System.out.print(grid.getCell(j, i).getValue());
        }
        //System.out.println();
        }*/
        if(gradeProgressBar.isStringPainted())
            JOptionPane.showMessageDialog(this, "Please wait until grading is complete before doing so again.");
        else if(grid.isValid()){
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            gradeProgressBar.setIndeterminate(true);
            gradeProgressBar.setString("Grading...");
            gradeProgressBar.setStringPainted(true);
            BruteRunner br = new BruteRunner(grid, this, gradeProgressBar);
            br.execute();
            

            /*int[][] solved = BruteForceSolver.solve(grid.toIntArray());
            if(solved[0][0] == -1)
            JOptionPane.showMessageDialog(this, "This puzzle cannot be solved because it has multiple solutions.");
            else if(solved[0][0] == 0)
            JOptionPane.showMessageDialog(this, "This puzzle cannot be solved because it has no solutions.");
            else{
            ArrayList<Solver> solvers = new ArrayList<Solver>();
            solvers.add(new SimpleSingle(grid));
            solvers.add(new Single(grid));
            solvers.add(new HiddenSingle(grid));
            solvers.add(new LockedCandidate1(grid));
            solvers.add(new LockedCandidate2(grid));
            solvers.add(new NakedSubset(grid,2));
            solvers.add(new NakedSubset(grid,3));
            solvers.add(new NakedSubset(grid,4));
            solvers.add(new HiddenSubset(grid,2));
            solvers.add(new HiddenSubset(grid,3));
            solvers.add(new HiddenSubset(grid,4));
            int q=0;
            grid.updateAllCandidates();
            while(q<100 && grid.numEmptyCells() != 0){
            for(Solver s: solvers){
            if(s.solve()==1) break;
            }
            displayGrid(grid);
            q++;
            }
            displayGrid(grid);
            if(grid.numEmptyCells() != 0){
            //System.out.println("Cannot Solve. Reverting to Brute Force Selection.");
            displayGrid(new SudokuGrid(solved));
            }
            for(Solver s: solvers)
            //System.out.println(s.getClass().getName() + " "+s.getRuns());
            }
            gradeProgressBar.setIndeterminate(false);
            gradeProgressBar.setStringPainted(false);*/
        }
        else
            javax.swing.JOptionPane.showMessageDialog(this, "You entered an invalid sudoku puzzle.");

    }//GEN-LAST:event_gradeMenuActionPerformed

    private void clearMenuButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearMenuButtonActionPerformed
        String[][] tg = new String[9][9];
        for(int i=0; i<81; i++)
            tg[i/9][i%9] = "0";
        arrayToGrid(tg);
}//GEN-LAST:event_clearMenuButtonActionPerformed

    private void addListeners(){
        grid00.addActionListener(new GridButtonListener(this));
        grid01.addActionListener(new GridButtonListener(this));
        grid02.addActionListener(new GridButtonListener(this));
        grid03.addActionListener(new GridButtonListener(this));
        grid04.addActionListener(new GridButtonListener(this));
        grid05.addActionListener(new GridButtonListener(this));
        grid06.addActionListener(new GridButtonListener(this));
        grid07.addActionListener(new GridButtonListener(this));
        grid08.addActionListener(new GridButtonListener(this));
        grid10.addActionListener(new GridButtonListener(this));
        grid11.addActionListener(new GridButtonListener(this));
        grid12.addActionListener(new GridButtonListener(this));
        grid13.addActionListener(new GridButtonListener(this));
        grid14.addActionListener(new GridButtonListener(this));
        grid15.addActionListener(new GridButtonListener(this));
        grid16.addActionListener(new GridButtonListener(this));
        grid17.addActionListener(new GridButtonListener(this));
        grid18.addActionListener(new GridButtonListener(this));
        grid20.addActionListener(new GridButtonListener(this));
        grid21.addActionListener(new GridButtonListener(this));
        grid22.addActionListener(new GridButtonListener(this));
        grid23.addActionListener(new GridButtonListener(this));
        grid24.addActionListener(new GridButtonListener(this));
        grid25.addActionListener(new GridButtonListener(this));
        grid26.addActionListener(new GridButtonListener(this));
        grid27.addActionListener(new GridButtonListener(this));
        grid28.addActionListener(new GridButtonListener(this));
        grid30.addActionListener(new GridButtonListener(this));
        grid31.addActionListener(new GridButtonListener(this));
        grid32.addActionListener(new GridButtonListener(this));
        grid33.addActionListener(new GridButtonListener(this));
        grid34.addActionListener(new GridButtonListener(this));
        grid35.addActionListener(new GridButtonListener(this));
        grid36.addActionListener(new GridButtonListener(this));
        grid37.addActionListener(new GridButtonListener(this));
        grid38.addActionListener(new GridButtonListener(this));
        grid40.addActionListener(new GridButtonListener(this));
        grid41.addActionListener(new GridButtonListener(this));
        grid42.addActionListener(new GridButtonListener(this));
        grid43.addActionListener(new GridButtonListener(this));
        grid44.addActionListener(new GridButtonListener(this));
        grid45.addActionListener(new GridButtonListener(this));
        grid46.addActionListener(new GridButtonListener(this));
        grid47.addActionListener(new GridButtonListener(this));
        grid48.addActionListener(new GridButtonListener(this));
        grid50.addActionListener(new GridButtonListener(this));
        grid51.addActionListener(new GridButtonListener(this));
        grid52.addActionListener(new GridButtonListener(this));
        grid53.addActionListener(new GridButtonListener(this));
        grid54.addActionListener(new GridButtonListener(this));
        grid55.addActionListener(new GridButtonListener(this));
        grid56.addActionListener(new GridButtonListener(this));
        grid57.addActionListener(new GridButtonListener(this));
        grid58.addActionListener(new GridButtonListener(this));
        grid60.addActionListener(new GridButtonListener(this));
        grid61.addActionListener(new GridButtonListener(this));
        grid62.addActionListener(new GridButtonListener(this));
        grid63.addActionListener(new GridButtonListener(this));
        grid64.addActionListener(new GridButtonListener(this));
        grid65.addActionListener(new GridButtonListener(this));
        grid66.addActionListener(new GridButtonListener(this));
        grid67.addActionListener(new GridButtonListener(this));
        grid68.addActionListener(new GridButtonListener(this));
        grid70.addActionListener(new GridButtonListener(this));
        grid71.addActionListener(new GridButtonListener(this));
        grid72.addActionListener(new GridButtonListener(this));
        grid73.addActionListener(new GridButtonListener(this));
        grid74.addActionListener(new GridButtonListener(this));
        grid75.addActionListener(new GridButtonListener(this));
        grid76.addActionListener(new GridButtonListener(this));
        grid77.addActionListener(new GridButtonListener(this));
        grid78.addActionListener(new GridButtonListener(this));
        grid80.addActionListener(new GridButtonListener(this));
        grid81.addActionListener(new GridButtonListener(this));
        grid82.addActionListener(new GridButtonListener(this));
        grid83.addActionListener(new GridButtonListener(this));
        grid84.addActionListener(new GridButtonListener(this));
        grid85.addActionListener(new GridButtonListener(this));
        grid86.addActionListener(new GridButtonListener(this));
        grid87.addActionListener(new GridButtonListener(this));
        grid88.addActionListener(new GridButtonListener(this));
    }

    public void exit(){
        System.exit(0);
    }

    private int parseInt(String s) {
        if(s == null || s.length() == 0)
            return 0;
        else{
            char c = s.charAt(0);
            if(c=='1')
                return 1;
            else if(c=='2')
                return 2;
            else if(c=='3')
                return 3;
            else if(c=='4')
                return 4;
            else if(c=='5')
                return 5;
            else if(c=='6')
                return 6;
            else if(c=='7')
                return 7;
            else if(c=='8')
                return 8;
            else//(c=='9')
                return 9;
        }
        
    }

    private void displayGrid(SudokuGrid grid){
        if(!soldispCB.isSelected()) return;
        String[][] tg = new String[9][9];
            for(int i=0; i<81; i++)
                tg[i%9][i/9] = "" + grid.getCellValue(i%9, i/9);
        arrayToGrid(tg);
    }
    private int[][] gridToArray() {
        int[][] tG= new int[9][9];
        tG[0][0] = parseInt(grid00.getText());
        tG[0][1] = parseInt(grid01.getText());
        tG[0][2] = parseInt(grid02.getText());
        tG[0][3] = parseInt(grid03.getText());
        tG[0][4] = parseInt(grid04.getText());
        tG[0][5] = parseInt(grid05.getText());
        tG[0][6] = parseInt(grid06.getText());
        tG[0][7] = parseInt(grid07.getText());
        tG[0][8] = parseInt(grid08.getText());
        tG[1][0] = parseInt(grid10.getText());
        tG[1][1] = parseInt(grid11.getText());
        tG[1][2] = parseInt(grid12.getText());
        tG[1][3] = parseInt(grid13.getText());
        tG[1][4] = parseInt(grid14.getText());
        tG[1][5] = parseInt(grid15.getText());
        tG[1][6] = parseInt(grid16.getText());
        tG[1][7] = parseInt(grid17.getText());
        tG[1][8] = parseInt(grid18.getText());
        tG[2][0] = parseInt(grid20.getText());
        tG[2][1] = parseInt(grid21.getText());
        tG[2][2] = parseInt(grid22.getText());
        tG[2][3] = parseInt(grid23.getText());
        tG[2][4] = parseInt(grid24.getText());
        tG[2][5] = parseInt(grid25.getText());
        tG[2][6] = parseInt(grid26.getText());
        tG[2][7] = parseInt(grid27.getText());
        tG[2][8] = parseInt(grid28.getText());
        tG[3][0] = parseInt(grid30.getText());
        tG[3][1] = parseInt(grid31.getText());
        tG[3][2] = parseInt(grid32.getText());
        tG[3][3] = parseInt(grid33.getText());
        tG[3][4] = parseInt(grid34.getText());
        tG[3][5] = parseInt(grid35.getText());
        tG[3][6] = parseInt(grid36.getText());
        tG[3][7] = parseInt(grid37.getText());
        tG[3][8] = parseInt(grid38.getText());
        tG[4][0] = parseInt(grid40.getText());
        tG[4][1] = parseInt(grid41.getText());
        tG[4][2] = parseInt(grid42.getText());
        tG[4][3] = parseInt(grid43.getText());
        tG[4][4] = parseInt(grid44.getText());
        tG[4][5] = parseInt(grid45.getText());
        tG[4][6] = parseInt(grid46.getText());
        tG[4][7] = parseInt(grid47.getText());
        tG[4][8] = parseInt(grid48.getText());
        tG[5][0] = parseInt(grid50.getText());
        tG[5][1] = parseInt(grid51.getText());
        tG[5][2] = parseInt(grid52.getText());
        tG[5][3] = parseInt(grid53.getText());
        tG[5][4] = parseInt(grid54.getText());
        tG[5][5] = parseInt(grid55.getText());
        tG[5][6] = parseInt(grid56.getText());
        tG[5][7] = parseInt(grid57.getText());
        tG[5][8] = parseInt(grid58.getText());
        tG[6][0] = parseInt(grid60.getText());
        tG[6][1] = parseInt(grid61.getText());
        tG[6][2] = parseInt(grid62.getText());
        tG[6][3] = parseInt(grid63.getText());
        tG[6][4] = parseInt(grid64.getText());
        tG[6][5] = parseInt(grid65.getText());
        tG[6][6] = parseInt(grid66.getText());
        tG[6][7] = parseInt(grid67.getText());
        tG[6][8] = parseInt(grid68.getText());
        tG[7][0] = parseInt(grid70.getText());
        tG[7][1] = parseInt(grid71.getText());
        tG[7][2] = parseInt(grid72.getText());
        tG[7][3] = parseInt(grid73.getText());
        tG[7][4] = parseInt(grid74.getText());
        tG[7][5] = parseInt(grid75.getText());
        tG[7][6] = parseInt(grid76.getText());
        tG[7][7] = parseInt(grid77.getText());
        tG[7][8] = parseInt(grid78.getText());
        tG[8][0] = parseInt(grid80.getText());
        tG[8][1] = parseInt(grid81.getText());
        tG[8][2] = parseInt(grid82.getText());
        tG[8][3] = parseInt(grid83.getText());
        tG[8][4] = parseInt(grid84.getText());
        tG[8][5] = parseInt(grid85.getText());
        tG[8][6] = parseInt(grid86.getText());
        tG[8][7] = parseInt(grid87.getText());
        tG[8][8] = parseInt(grid88.getText());
        return tG;
    }
    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SudokuGraderGUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem clearMenuButton;
    private javax.swing.JMenuItem exitMenuButton;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu gradeMenu;
    private javax.swing.JMenuItem gradeMenuButton;
    private javax.swing.JProgressBar gradeProgressBar;
    private javax.swing.JButton grid00;
    private javax.swing.JButton grid01;
    private javax.swing.JButton grid02;
    private javax.swing.JButton grid03;
    private javax.swing.JButton grid04;
    private javax.swing.JButton grid05;
    private javax.swing.JButton grid06;
    private javax.swing.JButton grid07;
    private javax.swing.JButton grid08;
    private javax.swing.JButton grid10;
    private javax.swing.JButton grid11;
    private javax.swing.JButton grid12;
    private javax.swing.JButton grid13;
    private javax.swing.JButton grid14;
    private javax.swing.JButton grid15;
    private javax.swing.JButton grid16;
    private javax.swing.JButton grid17;
    private javax.swing.JButton grid18;
    private javax.swing.JButton grid20;
    private javax.swing.JButton grid21;
    private javax.swing.JButton grid22;
    private javax.swing.JButton grid23;
    private javax.swing.JButton grid24;
    private javax.swing.JButton grid25;
    private javax.swing.JButton grid26;
    private javax.swing.JButton grid27;
    private javax.swing.JButton grid28;
    private javax.swing.JButton grid30;
    private javax.swing.JButton grid31;
    private javax.swing.JButton grid32;
    private javax.swing.JButton grid33;
    private javax.swing.JButton grid34;
    private javax.swing.JButton grid35;
    private javax.swing.JButton grid36;
    private javax.swing.JButton grid37;
    private javax.swing.JButton grid38;
    private javax.swing.JButton grid40;
    private javax.swing.JButton grid41;
    private javax.swing.JButton grid42;
    private javax.swing.JButton grid43;
    private javax.swing.JButton grid44;
    private javax.swing.JButton grid45;
    private javax.swing.JButton grid46;
    private javax.swing.JButton grid47;
    private javax.swing.JButton grid48;
    private javax.swing.JButton grid50;
    private javax.swing.JButton grid51;
    private javax.swing.JButton grid52;
    private javax.swing.JButton grid53;
    private javax.swing.JButton grid54;
    private javax.swing.JButton grid55;
    private javax.swing.JButton grid56;
    private javax.swing.JButton grid57;
    private javax.swing.JButton grid58;
    private javax.swing.JButton grid60;
    private javax.swing.JButton grid61;
    private javax.swing.JButton grid62;
    private javax.swing.JButton grid63;
    private javax.swing.JButton grid64;
    private javax.swing.JButton grid65;
    private javax.swing.JButton grid66;
    private javax.swing.JButton grid67;
    private javax.swing.JButton grid68;
    private javax.swing.JButton grid70;
    private javax.swing.JButton grid71;
    private javax.swing.JButton grid72;
    private javax.swing.JButton grid73;
    private javax.swing.JButton grid74;
    private javax.swing.JButton grid75;
    private javax.swing.JButton grid76;
    private javax.swing.JButton grid77;
    private javax.swing.JButton grid78;
    private javax.swing.JButton grid80;
    private javax.swing.JButton grid81;
    private javax.swing.JButton grid82;
    private javax.swing.JButton grid83;
    private javax.swing.JButton grid84;
    private javax.swing.JButton grid85;
    private javax.swing.JButton grid86;
    private javax.swing.JButton grid87;
    private javax.swing.JButton grid88;
    private javax.swing.JPanel gridPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JMenuBar mainMenu;
    private javax.swing.JFileChooser openFC;
    private javax.swing.JMenuItem openMenuButton;
    private javax.swing.JMenu prefMenu;
    private javax.swing.JFileChooser saveFC;
    private javax.swing.JMenuItem saveMenuButton;
    private javax.swing.JCheckBoxMenuItem soldispCB;
    // End of variables declaration//GEN-END:variables


}
