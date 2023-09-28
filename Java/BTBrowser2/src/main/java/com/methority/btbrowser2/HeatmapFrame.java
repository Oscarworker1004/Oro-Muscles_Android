/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.methority.btbrowser2;

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author manfred
 */
public class HeatmapFrame extends javax.swing.JFrame {
    private BufferedImage m_image;
    //private JLabel imageLabel;
    private IntPanel intPanel;
    private int m_xStart = -1;
    private int m_yStart = -1;
    private LongDraw m_draw;
    
    private Cycles m_cycles;

    private String m_annotFile;
    
    /**
     * Creates new form HeatmapFrame
     */
    public HeatmapFrame() {
        //loadImage("../../out.heat.jpg");
       // imageLabel = new JLabel();
        
        //imageLabel.setIcon(new ImageIcon(m_image));
        
        m_cycles = new Cycles();
        
        intPanel = new IntPanel();
        intPanel.setFrame(this);
        
        initComponents();

        intPanel.setCycles(m_cycles);
        
        //intPanel.setImage(m_image, m_image.getWidth(), m_image.getHeight());
        
        String [] listData = new String[1];
        listData[0] = "<empty cycles>";
        
        cycList.setListData(listData);
        listData[0] = "<empty sets>";
        setList.setListData(listData);
        jSlider1.setMinimum(10);
        jSlider1.setMaximum(500);
        jSlider1.setValue(100);
        m_sLabel.setText("100");
        
        m_annotFile = new String();
    }

    public Cycles cycles() {
        return m_cycles;
    }
    
    public void clearSelection() {
        intPanel.clearSelection();
    }

    public boolean hasSelection() {
        return intPanel.hasSelection();
    }
    
    public void addBlock(String name) {
        if (intPanel.getBlockIdx1() < 0 || intPanel.getBlockIdx1() >= m_cycles.count())
            return;
        if (intPanel.getBlockIdx2() < 0 || intPanel.getBlockIdx2() >= m_cycles.count())
            return;
        
        long start = m_cycles.start(intPanel.getBlockIdx1());
        long stop = m_cycles.stop(intPanel.getBlockIdx2());
        if (stop < start) {
            long tmp = stop;
            stop = start;
            start = tmp;
        }
        m_cycles.checkAddBlock(start, stop, name);
        m_cycles.syncBlocks();
        intPanel.updateUI();
    }
    
    private String Fill(String in, int len) {
        while (in.length() < len)
            in += " ";
        return in;
    }
    
    public void loadImage(String fileName) {
        try {
            m_image = ImageIO.read(new File(fileName));
            intPanel.setImage(m_image, m_image.getWidth(), m_image.getHeight());
            
            //intPanel.updateUI();
            m_xStart = -1;
            m_yStart = -1;
            
            
        } catch (Exception e) {
            System.out.println("WARNING: could not read file: " + fileName);
        }
    }

    public void setLong(LongDraw d) {
        m_draw = d;
        d.setHeatmap(this);
    }
    
    public void setSelection(int idx) {
        cycList.setSelectedIndex(idx);
    }
    
    public void updateLists() {
        String [] c = new String[m_cycles.count()];
        int i;        
        for (i=0; i<m_cycles.count(); i++) {
            String fr = new String();
            fr += m_cycles.start(i);
            String to = new String();
            to += m_cycles.stop(i);
            String name = new String();
            name += m_cycles.name(i);
        
            fr = Fill(fr, 9);
            to = Fill(to, 9);
            name = Fill(name, 18);
            
            String all = name + fr + to;
            c[i] = all;
        }
        
        cycList.setListData(c);
        cycList.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        
        c = new String[m_cycles.countBlock()];
        for (i=0; i<m_cycles.countBlock(); i++) {
            String fr = new String();
            fr += m_cycles.startBlock(i);
            String to = new String();
            to += m_cycles.stopBlock(i);
            String name = new String();
            name += m_cycles.nameBlock(i);
        
            fr = Fill(fr, 9);
            to = Fill(to, 9);
            name = Fill(name, 18);
            
            String all = name + fr + to;
            c[i] = all;
        }
        setList.setListData(c);
        setList.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        
        
    }

    public void readAnnot(String file) {
        m_cycles.read(file);    
        updateLists();
        String [] ss = file.split("\\.annot");
        m_annotFile = ss[0];
        ss = m_annotFile.split("\\.processed\\.txt");
        m_annotFile = ss[0];
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        testScroll = new HScroll(intPanel);
        jScrollPane2 = new javax.swing.JScrollPane();
        cycList = new javax.swing.JList<>();
        jSlider1 = new javax.swing.JSlider();
        m_sLabel = new javax.swing.JLabel();
        jButtonSplit = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        setList = new javax.swing.JList<>();
        jButtonDeleteSet = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        testScroll.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                testScrollMouseDragged(evt);
            }
        });
        testScroll.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                testScrollMouseWheelMoved(evt);
            }
        });
        testScroll.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                testScrollMouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                testScrollMouseReleased(evt);
            }
        });

        cycList.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        cycList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                cycListValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(cycList);

        jSlider1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSlider1StateChanged(evt);
            }
        });

        m_sLabel.setText("jLabel1");

        jButtonSplit.setText("Split by Set");
        jButtonSplit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSplitActionPerformed(evt);
            }
        });

        setList.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(setList);

        jButtonDeleteSet.setText("Delete");
        jButtonDeleteSet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteSetActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 331, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonDeleteSet, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(testScroll)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_sLabel)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonSplit, javax.swing.GroupLayout.DEFAULT_SIZE, 147, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 377, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButtonDeleteSet))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(testScroll, javax.swing.GroupLayout.DEFAULT_SIZE, 422, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSlider1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(m_sLabel)
                                    .addComponent(jButtonSplit))
                                .addGap(2, 2, 2)))))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void testScrollMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_testScrollMouseClicked
        if (evt.getButton() == 1) {
            int x = evt.getX();
            int y = evt.getY();
            int idx = intPanel.setCoordsMouse(x, y);
            intPanel.updateUI();
            this.setSelection(idx);
        }
    }//GEN-LAST:event_testScrollMouseClicked

    private void testScrollMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_testScrollMouseReleased
        m_xStart = -1;
        m_yStart = -1;

        int x = evt.getX();
        int y = evt.getY();
        //int idx = intPanel.setCoordsMouse(x, y);
        int idx = intPanel.setCoordsMouseSelect(x, y);
        intPanel.updateUI();
        m_draw.blockSelected();
        //this.setSelection(idx);
        
        //=============================================================
        
    }//GEN-LAST:event_testScrollMouseReleased

    private void testScrollMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_testScrollMouseDragged
        int x = evt.getX();
        int y = evt.getY();
        x = intPanel.quantX(x);
        y = intPanel.quantY(x);
        if (m_xStart < 0) {
            m_xStart = x;
            m_yStart = y;
        }
        intPanel.setCoordsDrag(m_xStart, m_yStart, x, y);
    }//GEN-LAST:event_testScrollMouseDragged

    private void cycListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_cycListValueChanged
        
        int idx = cycList.getSelectedIndex();
        int [] idxes = cycList.getSelectedIndices();
        if (idx >= 0) {
            int ms = (int)m_cycles.start(idx);
            m_draw.setScrollPos(ms);
            intPanel.setCoords(idx, idx);
            intPanel.updateUI();
        }
        
        if (idxes.length > 1) {
            int idx1 = idxes[0];
            int idx2 = idxes[idxes.length-1];
            intPanel.setCoordsMult(idx1, 
                                   idx2,
                                   m_cycles.count());

            intPanel.updateUI();

            m_draw.blockSelected();
        }


    }//GEN-LAST:event_cycListValueChanged

    private void jSlider1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlider1StateChanged
        // TODO add your handling code here:
        String text = new String();
        text += jSlider1.getValue();
        m_sLabel.setText(text);
        intPanel.setScale(jSlider1.getValue());
    }//GEN-LAST:event_jSlider1StateChanged

    private void testScrollMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_testScrollMouseWheelMoved
        // TODO add your handling code here:
        //System.out.println("Mouse wheel... ");
        //int x = evt.getScrollAmount();
        //int y = evt.getScrollType();
        //int z = evt.getWheelRotation();
        int w = evt.getUnitsToScroll();
        //System.out.println("SCROLL: " + x + " " + y + " " + z + " " + w);
     
        int val = jSlider1.getValue();
        val -= w;
        String text = new String();
        text += val;
        jSlider1.setValue(val);
        m_sLabel.setText(text);
        intPanel.setScale(jSlider1.getValue());
       
    }//GEN-LAST:event_testScrollMouseWheelMoved

    private void jButtonSplitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSplitActionPerformed

        //======================================================================
        String meta = m_annotFile;
        meta += "_split.mult";
        
        File mf = new File(meta);
        if (mf.exists())
            mf.delete();
        
        try {
            FileWriter fw = new FileWriter(mf);
        
            fw.write("Files Labels\n");

           //m_draw.
            int i;
            for (i=0; i<m_cycles.countBlock(); i++) {
                String out = m_annotFile;
                out += ".split_";
                out += i;
                out += ".csv";
                String label = m_cycles.nameBlock(i);
                label += "_";
                label += i;
                double from = m_cycles.startBlock(i);
                double to = m_cycles.stopBlock(i);
                
                double d = 1000.;
                from /= d;
                to /= d;

                
                BTData.WriteCrop(m_annotFile, out, from, to);

                String [] ss = out.split(File.separator);
                String lf = ss[ss.length-1];

                fw.write(lf + " " + label + "\n");
            //m_draw.
            //m_annotFile 
                    
            }
            
            fw.close();
            
            JOptionPane.showMessageDialog(this,
                "File has been split, load mult: " + meta,
                "Splitting into sets",
                JOptionPane.INFORMATION_MESSAGE);
            
        } catch (Exception e) {
        
        }
        
        //======================================================================
    }//GEN-LAST:event_jButtonSplitActionPerformed

    private void jButtonDeleteSetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteSetActionPerformed
        int idx = setList.getSelectedIndex();
        m_cycles.deleteBlock(idx);
       
        updateLists();
        m_draw.updateHintFile();
        
        intPanel.updateUI();

        
        //============================================================
    }//GEN-LAST:event_jButtonDeleteSetActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(LongDraw d) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(HeatmapFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(HeatmapFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(HeatmapFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(HeatmapFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                HeatmapFrame ff = new HeatmapFrame();
                ff.setLong(d);
                ff.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList<String> cycList;
    private javax.swing.JButton jButtonDeleteSet;
    private javax.swing.JButton jButtonSplit;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSlider jSlider1;
    private javax.swing.JLabel m_sLabel;
    private javax.swing.JList<String> setList;
    private javax.swing.JScrollPane testScroll;
    // End of variables declaration//GEN-END:variables
}
