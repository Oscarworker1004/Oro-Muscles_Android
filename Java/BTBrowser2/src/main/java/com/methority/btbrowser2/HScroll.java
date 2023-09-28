/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.methority.btbrowser2;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 * @author manfred
 */
public class HScroll extends JScrollPane  {
    private IntPanel m_canvas;
    
    public HScroll() {}
    
    public HScroll(JLabel lab) {
        super(lab);
    }

    public HScroll(IntPanel pan) {
        super(pan);
        m_canvas = pan;
    }

    public void setImage(BufferedImage img, int x, int y) {
        m_canvas.setImage(img, x, y);
    }
    
    @Override
    protected void paintComponent(Graphics gg) {
        super.paintComponent(gg);
                       
    }
 
}
