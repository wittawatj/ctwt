package com.wittawat.wordseg.gui;

import javax.swing.JInternalFrame;
import org.apache.commons.lang.math.RandomUtils;

/**
 *
 * @author Wittawat Jitkrittum
 */
public class GUI extends javax.swing.JFrame {

    /** Creates new form WordSegFrame */
    public GUI() {
        initComponents();
        setSize(800,600);
        setLocationRelativeTo(null);
    }

    private void initFrame(JInternalFrame inFrame) {
        inFrame.setSize(500, 650);
        int shift = RandomUtils.nextInt() % 20;
        inFrame.setLocation(20 + shift, 20 + shift);
        desktopPane.add(inFrame);
        inFrame.toFront();
        inFrame.setVisible(true);

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        desktopPane = new javax.swing.JDesktopPane();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        itemWordSegDialog = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Classification-based Thai Word Tokenizer");
        getContentPane().add(desktopPane, java.awt.BorderLayout.CENTER);

        jMenu1.setText("Dialog");

        itemWordSegDialog.setText("Wordseg Dialog");
        itemWordSegDialog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemWordSegDialogActionPerformed(evt);
            }
        });
        jMenu1.add(itemWordSegDialog);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void itemWordSegDialogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemWordSegDialogActionPerformed
        JInternalFrame f = new WordSegFrame();
        initFrame(f);
    }//GEN-LAST:event_itemWordSegDialogActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new GUI().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JDesktopPane desktopPane;
    private javax.swing.JMenuItem itemWordSegDialog;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    // End of variables declaration//GEN-END:variables
}
