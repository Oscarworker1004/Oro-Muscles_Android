/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.methority.btbrowser2;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/**
 *
 * @author manfred
 */
public class IntPanel extends JPanel {
    private int m_off = 1; // Offset
    private int m_pix = 4; // Size of a rect in image
    private int m_width;
    private int m_height;
    private int m_x = -1;
    private int m_y = -1;
    private int m_xSel = -1;
    private int m_ySel = -1;
    private int  m_dragX1 = -1;
    private int  m_dragX2 = -1;
    private int  m_dragY1 = -1;
    private int  m_dragY2 = -1;
    
    private Image m_image = null;
    private Image m_imageOrig;
    private HeatmapFrame m_frame = null;

    private Cycles m_cycles;

    private int m_scale = 100;
    
    public IntPanel() {
        m_image = null;
        m_width = 0;
        m_height = 0;
    }
    
    public void setFrame(HeatmapFrame f) {
        m_frame = f;
    }
    
    public void setCycles(Cycles c) {
        m_cycles = c;
    }
    
    public void resetDrag() {
        m_x = -1;
        m_y = -1;
        m_xSel = -1;
        m_ySel = -1;
        m_dragX1 = -1;
        m_dragX2 = -1;
        m_dragY1 = -1;
        m_dragY2 = -1;
    }
    
    private int scaled(int x) {
        //return (int)((double)(0.5+x*(double)m_scale/100.));
        return x*m_scale/100;
    }
    private int unscaled(int x) {
        //return (int)(0.5+(double)x*100/(double)m_scale);
        return x*100/m_scale;
    }
    
    @Override
    protected void paintComponent(Graphics gg) {
        super.paintComponent(gg);
        int i;

        Graphics2D g = (Graphics2D) gg;

        if (m_image != null) {
            g.drawImage(m_image, 0, 0, null);
        }
        if (m_x > 0 && m_y > 0) {
            // DO stuff HERE
            int x = scaled(m_x);
            int y = scaled(m_y);
            y = m_height - x;
            
            g.setColor(Color.green);        
            g.fillRect(scaled(x-m_pix), scaled(y), scaled(m_pix), scaled(m_pix));
            
            
            //g.drawLine(x-m_pix/2, y, m_width-1, y);
            //g.drawLine(x-m_pix/2, y, x-m_pix/2, 0);
            g.drawLine(0, y, m_width-1, y);
            g.drawLine(x, 0, x, m_height);
        }
        
        for (i=0; i<m_cycles.countBlock(); i++) {
            //g.setColor(Color.green);  
            g.setColor(Color.yellow);
            int x1 = scaled(m_cycles.startBlockIndex(i) * m_pix);
            int x2 = scaled(m_cycles.stopBlockIndex(i) * m_pix);
            g.drawRect(x1, m_height-x1-(x2-x1)-m_pix/2, x2-x1, x2-x1);
            

            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                               RenderingHints.VALUE_ANTIALIAS_ON);
            g.drawString(m_cycles.nameBlock(i), x1+2, m_height-x1-(x2-x1)+11); 

            //g.setFont(font);
            //m_cycles.nameBlock(i);
        
        }
        
        
        if (m_dragX1 > 0 && m_dragY1 > 0) {
            g.setColor(Color.red);  
            if (m_dragX2 < m_dragX1) {
                int tmp = m_dragX2;
                m_dragX2 = m_dragX1;
                m_dragX1 = tmp;
            }
            if (m_dragY2 < m_dragY1) {
                int tmp = m_dragY2;
                m_dragY2 = m_dragY1;
                m_dragY1 = tmp;
            }
            g.drawRect(scaled(m_dragX1), scaled(m_dragY1), scaled(m_dragX2)-scaled(m_dragX1), scaled(m_dragY2)-scaled(m_dragY1));                        
        }      
                       
    }

    private void scaleImage() {
        int x = m_imageOrig.getWidth(this)*m_scale/100;
        int y = m_imageOrig.getWidth(this)*m_scale/100;
        m_image = m_imageOrig.getScaledInstance(x, y, 0);

        m_width = m_image.getWidth(this);
        m_height = m_image.getHeight(this);
        this.setPreferredSize(new Dimension(x, y));    
    }
    
    public int getScale() {
        return m_scale;
    }
    
    public void setScale(int s) {
        m_scale = s;
        if (m_imageOrig != null) {
            scaleImage();
            repaint();
        }
    }
    
    public void setImage(Image img, int x1, int y1) {
        m_imageOrig = img;
        
        /*int x = m_imageOrig.getWidth(this)*m_scale/100;
        int y = m_imageOrig.getWidth(this)*m_scale/100;
        m_image = m_imageOrig.getScaledInstance(xx, yy, 0);

        m_width = m_image.getWidth(this);
        m_height = m_image.getHeight(this);*/
        
        scaleImage();
        
        resetDrag();
        //this.setPreferredSize(new Dimension(x, y));
    } 

    
    
    public void setCoords(int x, int y) {
        m_x = m_off + x*m_pix;
        m_y = m_off + y*m_pix;
    
    }

    public void setCoordsMult(int x, int y, int max) {
        m_x = m_off + x*m_pix;
        m_y = m_off + y*m_pix;
        
        m_dragX1 = m_off + x*m_pix;
        m_dragX2 = m_off + y*m_pix;
        m_dragY1 = m_off + (max-x)*m_pix;
        m_dragY2 = m_off + (max-y)*m_pix;

        System.out.println("MULT:    " + m_dragX1 + " " + m_dragY2 + " x=" + x + " y=" + y
        + " m_pix=" + m_pix + " m_off=" + m_off + " m_height=" + m_height);
        System.out.println("MULT(2): " + scaled(m_off+m_pix*x) + " " + unscaled(m_off+m_pix*x));
            
        updateUI();

    }

    public void incCoords(int x, int y) {
        m_x += x*m_pix;
        m_y += y*m_pix;
        updateUI();
    
    }

    public void setCoordsMS(int x, int y) {
        //setCoords()
    }

       
    public void clearSelection() {
        m_dragX1 = -1;
        m_dragX2 = -1;
        m_dragY1 = -1;
        m_dragY2 = -1;
        updateUI();
    }
    
    public boolean hasSelection() {
        if (m_dragX1 >=0 && m_dragX2 >= 0)
            return true;
        else
            return false;
    }
    
    public void setCoordsDrag(int x1, int y1, int x2, int y2) {
        m_dragX1 = unscaled(x1);
        m_dragX2 = unscaled(x2);
        m_dragY1 = unscaled(y1);
        m_dragY2 = unscaled(y2);
        //m_dragX1 = x1;
        //m_dragX2 = x2;
        //m_dragY1 = y1;
        //m_dragY2 = y2;
        System.out.println("DRAG: " + m_dragX1 + " " + m_dragY1);
        updateUI();
    }
    
    public int quantX(int x) {
        return m_pix*(int)((double)(x+m_pix/2)/(double)m_pix);
    } 
    public int quantY(int x) {
        return m_height - m_pix/2 - m_pix*(int)((double)(x+m_pix/2)/(double)m_pix);
    } 
    
    public int setCoordsMouse(int x, int y) {
        m_x = m_pix * (unscaled(x) / m_pix);
        m_y = m_pix * (unscaled(y) / m_pix);
        
        if (m_x >= m_width)
            m_x = m_width-1;
        int idx = (m_x - m_off)/m_pix;
        return idx;
    }

    public int getBlockIdx1() {
        return m_dragX1/m_pix;
    }
    
    public int getBlockIdx2() {
        return m_dragX2/m_pix;
    }
    
    public int setCoordsMouseSelect(int x, int y) {
        m_xSel = quantX(unscaled(x));
        m_ySel = quantY(unscaled(x));
        
        
        if (m_xSel >= m_width)
            m_xSel = m_width-1;
        int idx = (m_xSel - m_off)/m_pix;
        //m_dragX1 = -1;
        //m_dragY1 = -1;
        
        return idx;
    }
    
}
