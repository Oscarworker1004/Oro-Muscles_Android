/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.methority.btbrowser2;

import java.awt.Font;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File; 
import java.util.Scanner;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

/**
 *
 * @author manfred
 */



public class LabelJFrame extends javax.swing.JFrame {

    
    public class Segment
    {
        int count;
        public double [] left;
        public double [] right;
        public int [] state;
        public String [] cycle;
        public String [] files;
        public String dataDir;
        public String annot;
        
        
        Segment() {
            count = 0;
            left = new double[512];
            right = new double[512];
            state = new int[512];
            cycle = new String[512];
            files = new String[512];
            //m_disp = new String[512];
            dataDir = new String();
            annot = new String();
        }
    }

    
    
    private String m_annot;
    
    private Segment [] m_segments;
    
    private String m_meta;
    
    private Cycles m_cycles = null;
    
    
    public void setCycles(Cycles c) {
        m_cycles = c;
    }
    
    public void SetMeta(String s) {
        m_meta = s + ".persist.txt";
        Clear();
 
        try {
            File file = 
                new File(m_meta); 
            Scanner sc = new Scanner(file); 

            int i, j;
   
            int k = 0;

            int n = 0;
            while (sc.hasNextLine()) {
                String line = sc.nextLine();

                if (line.equals("</channel>")) {
                    k++;
                    continue;
                }

                String[] split = line.split("\\s+");
                if (line.equals("<channel>")) {
                    line = sc.nextLine();
                    split = line.split("\\s+");
                    n = Integer.parseInt(split[1]);
                    m_segments[k].count = n;
                    continue;
                }
                
                for (i=0; i<n; i++) {
                    //String loc = sc.nextLine();
                    String loc = new String();
                    if (i == 0)
                        loc = line;
                    else
                        loc = sc.nextLine();
                    String[] ss = loc.split("\\s+");
                    m_segments[k].left[i] = Float.parseFloat(ss[1]); 
                    loc = sc.nextLine();
                    ss = loc.split("\\s+");
                    m_segments[k].right[i] = Float.parseFloat(ss[1]); 
                    loc = sc.nextLine();
                    ss = loc.split("\\s+");
                    m_segments[k].state[i] = Integer.parseInt(ss[1]); 
                    loc = sc.nextLine();
                    ss = loc.split("\\s+");
                    m_segments[k].cycle[i] = ss[1];                     
                }
   
                int check = m_segments[k].files.length;
                for (i=0; i<m_segments[k].files.length; i++) {
                    String loc = sc.nextLine();
                    String[] ss = loc.split("\\s+");                    
                    if (loc.equals("</channel>")) {
                        k++;
                        break;
                    }
                    m_segments[k].files[i] = ss[1];
                    String xy = m_segments[k].files[i];
                }
            }            
        }
        catch(Exception e) {
            System.out.println("No meta file found.");  
        }

        System.out.println("Done loading!");  

    
    
    }
    
    /**
     * Creates new form LabelJFrame
     */
    public LabelJFrame() {
        
        m_meta = new String();
        
        String path = new File(".").getAbsolutePath();
        m_annot = path + "/annot.txt";
        initComponents();
        jTextField1.setText("");
        state = -1;
        m_count = 0;
    
        m_channel = 0;
        m_segments = new Segment[64];
        
        for (int i=0; i<m_segments.length; i++)
            m_segments[i] = new Segment();
        //jList1.getSelectedIndex();
        //String c[] = new String[1];
        //c[0] = new String();
        //c[0] = "left_frame, right_frame, state, cycle";

        //jList1.setListData(c);
        Clear();

        m_fileCount = 0;
        m_segCount = 0;
        
        m_left = new double[512];
        m_right = new double[512];
        m_state = new int[512];
        m_cycle = new String[512];
        m_files = new String[512];
        //m_disp = new String[512];
        m_dataDir = new String();
        
        
        // DISABLE for now
        BoxApplyAll.setEnabled(false);
    }

    // Lists
    int m_count;
    double m_left[];
    double m_right[];
    int m_state[];
    String m_cycle[];
    //String m_disp[];
    String m_files[];
    int m_fileCount;
    
    private int state;
    private double left;
    private double right;
    
    private int m_channel;

    private String m_dataDir;
    private int m_segCount;
    
    private String m_currCSV;
    
    void SetDataDir(String s) {
        m_dataDir = s;
    }
    
    public void SetBaseCSV(String base) {
        m_currCSV = base;
    }
    
    public void Set(double left, double right) {
        this.left = left;
        this.right = right;
    }

    public void UpdateAllChannels() {
        for (int i=0; i<m_segments.length; i++) {
            m_segments[i].left = m_left;
            m_segments[i].right = m_right;
            m_segments[i].state = m_state;
            m_segments[i].cycle = m_cycle;
            m_segments[i].files = m_files;

            m_segments[i].count = m_count;
        }
        SetListDataAndWrite();
    }
    
    
    public void SetChannel(int n) {
        m_channel = n;
        
        m_left = m_segments[n].left;
        m_right = m_segments[n].right;
        m_state = m_segments[n].state;
        m_cycle = m_segments[n].cycle;
        m_files = m_segments[n].files;

        m_count = m_segments[n].count;
        m_fileCount = 0;
        
       
        for (int i=0; i<m_segments[n].files.length; i++) {
            if (m_segments[n].files[i] == null || m_segments[n].files[i].equals(""))
                continue;
            m_fileCount++;
        }
        SetListDataAndWrite();
        // FIX THAT@!!!!
        //m_dataDir = new String();
    }
    
    public void Clear() {
        try {
            FileWriter fw = new FileWriter("annot.txt");
           
            m_count = 0;   
            m_fileCount = 0;
            fw.write("left_frame right_frame state\n");


            m_segments = new Segment[64];
        
            for (int i=0; i<m_segments.length; i++)
                m_segments[i] = new Segment();
            
            
            //fw.write("\n");
            fw.close();

            String c[] = new String[1];
            c[0] = new String();
            c[0] = Pad("left_frame", 12);
            c[0] += Pad("right_frame", 12);
            c[0] += Pad("state", 12);
            c[0] += Pad("cycle", 12);

            jList1.setListData(c);
            jList1.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));


        }
        
        catch(IOException e) {
            System.out.println("EXCEPTION! Could not open annotation file!!");  

        }
    }
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jRadioButton3 = new javax.swing.JRadioButton();
        jRadioButton4 = new javax.swing.JRadioButton();
        jRadioButton5 = new javax.swing.JRadioButton();
        jButton1 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jRadioButton6 = new javax.swing.JRadioButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        jButton2 = new javax.swing.JButton();
        FileButton = new javax.swing.JButton();
        BoxApplyAll = new javax.swing.JCheckBox();
        jButtonCrop = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jRadioButton1.setText("0 Resting");
        jRadioButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton1ActionPerformed(evt);
            }
        });

        jRadioButton2.setText("1 Background");
        jRadioButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton2ActionPerformed(evt);
            }
        });

        jRadioButton3.setText("2 Light");
        jRadioButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton3ActionPerformed(evt);
            }
        });

        jRadioButton4.setText("3 Pre/Post");
        jRadioButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton4ActionPerformed(evt);
            }
        });

        jRadioButton5.setText("4 Peak");
        jRadioButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton5ActionPerformed(evt);
            }
        });

        jButton1.setText("OK");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jTextField1.setText("jTextField1");
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jLabel1.setText("Cycle name");

        jRadioButton6.setText("Artifact");
        jRadioButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton6ActionPerformed(evt);
            }
        });

        jList1.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jList1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jList1KeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(jList1);

        jButton2.setText("Remove");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        FileButton.setText("Load template DB");
        FileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FileButtonActionPerformed(evt);
            }
        });

        BoxApplyAll.setText("Apply to all");

        jButtonCrop.setText("Crop Recording");
        jButtonCrop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCropActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 346, Short.MAX_VALUE))
                        .addGap(41, 41, 41))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jRadioButton2)
                            .addComponent(jRadioButton1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jRadioButton3, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jRadioButton4, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jRadioButton5, javax.swing.GroupLayout.Alignment.LEADING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jRadioButton6)
                                .addGap(84, 84, 84))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(BoxApplyAll)
                                .addContainerGap())
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jButtonCrop, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(FileButton, javax.swing.GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE))
                                .addGap(27, 27, 27))))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRadioButton1)
                    .addComponent(FileButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRadioButton2)
                    .addComponent(BoxApplyAll))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jRadioButton3)
                    .addComponent(jButtonCrop))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButton4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRadioButton5)
                    .addComponent(jRadioButton6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addGap(43, 43, 43))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jRadioButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton1ActionPerformed
        // TODO add your handling code here:
        jRadioButton2.setSelected(false);
        jRadioButton3.setSelected(false);
        jRadioButton4.setSelected(false);
        jRadioButton5.setSelected(false);
        jRadioButton6.setSelected(false);
        jTextField1.setText("");
        state = 0;
    }//GEN-LAST:event_jRadioButton1ActionPerformed

    private void jRadioButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton2ActionPerformed
        // TODO add your handling code here:
        jRadioButton1.setSelected(false);
        jRadioButton3.setSelected(false);
        jRadioButton4.setSelected(false);
        jRadioButton5.setSelected(false);
        jRadioButton6.setSelected(false);
        jTextField1.setText("");
        state = 1;
    }//GEN-LAST:event_jRadioButton2ActionPerformed

    private void jRadioButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton3ActionPerformed
        // TODO add your handling code here:
        jRadioButton1.setSelected(false);
        jRadioButton2.setSelected(false);
        jRadioButton4.setSelected(false);
        jRadioButton5.setSelected(false);
        jRadioButton6.setSelected(false);
        jTextField1.setText("");
        state = 2;

    }//GEN-LAST:event_jRadioButton3ActionPerformed

    private void jRadioButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton4ActionPerformed
        // TODO add your handling code here:
        jRadioButton2.setSelected(false);
        jRadioButton3.setSelected(false);
        jRadioButton1.setSelected(false);
        jRadioButton5.setSelected(false);
        jRadioButton6.setSelected(false);
        jTextField1.setText("");
        state = 3;

    }//GEN-LAST:event_jRadioButton4ActionPerformed

    private void jRadioButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton5ActionPerformed
        // TODO add your handling code here:
        jRadioButton2.setSelected(false);
        jRadioButton3.setSelected(false);
        jRadioButton4.setSelected(false);
        jRadioButton1.setSelected(false);
        jRadioButton6.setSelected(false);
        jTextField1.setText("");
        state = 4;


    }//GEN-LAST:event_jRadioButton5ActionPerformed

    private String Pad(String in, int k) {
        //System.out.println("ENTER Pad " + k);
        String out = in;
        for (int i=in.length(); i<k; i++) {
            out += " ";
            //System.out.println("Padding: " + out + " " + i);
        }
        
        return out;
    }
    
    public void EnableButton(int n) {
        if (n == 1) {
            jButton1.setEnabled(true);
        } else {
            jButton1.setEnabled(false);        
        }
            
    }
    
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        setVisible(false);
  

        if (BoxApplyAll.isSelected()) {
            UpdateAllChannels();
        }

        
        SetListDataAndWrite();

    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
        jRadioButton5.setSelected(false);
        jRadioButton2.setSelected(false);
        jRadioButton3.setSelected(false);
        jRadioButton4.setSelected(false);
        jRadioButton1.setSelected(false);
        jRadioButton6.setSelected(false);

        String t = jTextField1.getText();
        if (t.equals("ARTIFACT"))
          jRadioButton6.setSelected(true);

    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jRadioButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton6ActionPerformed
        jRadioButton1.setSelected(false);
        jRadioButton2.setSelected(false);
        jRadioButton3.setSelected(false);
        jRadioButton4.setSelected(false);
        jRadioButton5.setSelected(false);
        jTextField1.setText("ARTIFACT");
        state = -1;

    }//GEN-LAST:event_jRadioButton6ActionPerformed

    private void jList1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jList1KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jList1KeyPressed


    private void UpdateSegments() {
        m_segments[m_channel].left = m_left;
        m_segments[m_channel].right = m_right;
        m_segments[m_channel].state = m_state;
        m_segments[m_channel].cycle = m_cycle;
        m_segments[m_channel].count = m_count;
        m_segments[m_channel].files = m_files;
    
        // TRY!!!!!!!!!!!!!!!!!!!!!!
        //for (int i=0; i<m_count; i++)
          //  m_segments[i].files = m_files;
            
    }
    
    public void SetListDataAndWrite() {
        
        for (int x = 0; x<m_cycles.countBlock(); x++) {
            System.out.println("BLOCK " + m_cycles.nameBlock(x));
        }
                
        try {
            FileWriter fw = new FileWriter("annot.txt");
            int i;
                                        
                    
            String cycle = jTextField1.getText();

            if (left > 0 && right > 0) {
                m_left[m_count] = left;
                m_right[m_count] = right;
                m_state[m_count] = state;
                m_cycle[m_count] = cycle;
            
                
                m_count++;
                UpdateSegments();
            }
            
            
            fw.write("left_frame right_frame state\n");

            //if (!cycle.equals(""))
              //  right += 0.01;
            String c[] = new String[m_count+1+m_fileCount];
            c[0] = new String();
            c[0] = Pad("left_frame", 12);
            c[0] += Pad("right_frame", 12);
            c[0] += Pad("state", 12);
            c[0] += Pad("cycle", 12);
                  
 
            for (i=0; i<m_count; i++) {  
                fw.write(m_left[i] + " " + m_right[i] + " " + m_state[i] + " " + m_cycle[i] + "\n");
                c[i+1] = new String();
                //c[i+1] = m_left[i] + " " + m_right[i] + " " + m_state[i] + " " + m_cycle[i];
                c[i+1] = Pad(m_left[i] + " ", 12);
                c[i+1] += Pad(m_right[i] + " ", 12);
                c[i+1] += Pad(m_state[i] + " ", 12);
                c[i+1] += Pad(m_cycle[i] + " ", 12);
            }
            for (int j=0; j<m_fileCount; j++) {
                c[i+1] = new String();
                c[i+1] = m_files[j];
                fw.write(m_files[j] + "\n");
              i++;
            }
            

            if (m_cycles.countBlock() > 0) {
                fw.write("left_block right_block name\n");
            
                for (i=0; i<m_cycles.countBlock(); i++) {  
                    fw.write(m_cycles.startBlock(i) + " " + m_cycles.stopBlock(i) + " " + m_cycles.nameBlock(i) + "\n");
                }
            }
            
            
            
            jList1.setListData(c);

            left = -1;
            right = -1;
            
            //fw.write("\n");
            fw.close();
        }
        
        catch(IOException e) {
        }
 
        WriteMetaInfo();
    
    }

    private void WriteMetaInfo() {
        
        if (m_meta == null || m_meta.equals(""))
            return;
        
        try {
            FileWriter fw = new FileWriter(m_meta);
            int i = 0;
            int k = 0;                            
                    
            for (k=0; k<m_segments.length; k++) {
                fw.write("<channel>\n");

                int n = m_segments[k].count;
                boolean b = false; 

                fw.write("count  " + n + "\n");
                for (i=0; i<n; i++) {
                    fw.write("left  " + m_segments[k].left[i] + "\n");
                    fw.write("right " + m_segments[k].right[i] + "\n");
                    fw.write("state " + m_segments[k].state[i] + "\n");
                    fw.write("cycle " + m_segments[k].cycle[i] + "\n");
                    b = true;
                }

                for (i=0; i<m_segments[k].files.length; i++) {
                    if (m_segments[k].files[i] == null || m_segments[k].files[i].equals(""))
                        break;
                    fw.write("file  " + m_segments[k].files[i] + "\n");
                    b = true;
                }           
            //   m_segments[i].left
                fw.write("</channel>\n");
            
                //if (!b)
                  //  break;
            }
            
            //fw.write("\n");
            fw.close();
        }
        
        catch(IOException e) {
        }
     }


    
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        
        // REMOVE Item from list
        
            int s = jList1.getSelectedIndex() - 1;
            if (s < 0)
                return;
            int i;
            
                        
            System.out.println("SELECTED Index: " + s);

            if (s < m_count) {
                for (i=s; i<m_count-1; i++) {
                    m_left[i] = m_left[i+1];
                    m_right[i] = m_right[i+1];
                    m_cycle[i] = m_cycle[i+1];
                    m_state[i] = m_state[i+1];
                }
                
                m_count--;
               
            } else {
                s -= m_count;
                for (i=s; i<m_fileCount-1; i++) {
                    m_files[i] = m_files[i+1];
                }
                m_fileCount--;
            } 
               

            UpdateSegments();

            SetListDataAndWrite();
            
  
        
        //JList1. 
    }//GEN-LAST:event_jButton2ActionPerformed

    
    private void FileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FileButtonActionPerformed
        
        JFileChooser chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(true);

        System.out.print("Current directory: " + m_dataDir + "\n");

        chooser.setCurrentDirectory(new File(m_dataDir));
        chooser.setDialogTitle("Open template DB");
        
        chooser.showOpenDialog(new JFrame());
        File[] files = chooser.getSelectedFiles();
        
        System.out.print("Files returned: " + files.length + "\n");
  
        if (files.length > 0) {
            m_dataDir = files[0].getPath();
            //m_input = m_dataDir;
        } else {
            return;
        }

        m_files[m_fileCount] = new String();
        m_files[m_fileCount] = files[0].getAbsolutePath();
        m_fileCount++;

        if (BoxApplyAll.isSelected()) {
            for (int i=0; i<m_segments.length; i++)
                m_segments[i].files = m_files;
            //UpdateAllChannels();
        }

        SetListDataAndWrite();
        
        
        
    }//GEN-LAST:event_FileButtonActionPerformed

    private void jButtonCropActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCropActionPerformed
        // TODO add your handling code here:
        
        if (m_currCSV == null || m_currCSV.equals(""))
            return;
        
        JFileChooser chooser2 = new JFileChooser();
        chooser2.setMultiSelectionEnabled(false);
        
        
              
        chooser2.setCurrentDirectory(new File(m_dataDir));
        chooser2.setDialogTitle("Write crop to:");

        int userSelection = chooser2.showSaveDialog(new JFrame());
        
        
        if (userSelection != JFileChooser.APPROVE_OPTION) {
            return;
        }
        
        double d = 500./64.;
        double from = left/d;
        double to = right/d;
        
        File fileToSave = chooser2.getSelectedFile();
        String out = fileToSave.getAbsolutePath();
        
        BTData.WriteCrop(m_currCSV, out, from, to);
        setVisible(false);
        
    }//GEN-LAST:event_jButtonCropActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
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
            java.util.logging.Logger.getLogger(LabelJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LabelJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LabelJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LabelJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LabelJFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox BoxApplyAll;
    private javax.swing.JButton FileButton;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButtonCrop;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JList<String> jList1;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JRadioButton jRadioButton4;
    private javax.swing.JRadioButton jRadioButton5;
    private javax.swing.JRadioButton jRadioButton6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
