/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.methority.btbrowser2;
// package com.digidactylus.display2;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 *
 * @author manfred
 */
public class Cycles {
    private int m_count;
    private long [] m_start;
    private long [] m_stop;
    private String [] m_name;

    private int m_countHint;
    private long [] m_startHint;
    private long [] m_stopHint;
    private String [] m_nameHint;

    private int m_countBlock;
    private long [] m_startBlock;
    private long [] m_stopBlock;
    private int [] m_startBlockIndex;
    private int [] m_stopBlockIndex;
    private String [] m_nameBlock;
    
    public Cycles() {
        m_count = 0;
        
        m_start = new long[65000];
        m_stop = new long[65000];
        m_name = new String[65000];
        
        
        m_countHint = 0;
        m_startHint = new long[512];
        m_stopHint = new long[512];
        m_nameHint = new String[512];

        m_countBlock = 0;
        m_startBlock = new long[512];
        m_stopBlock = new long[512];
        m_startBlockIndex = new int[512];
        m_stopBlockIndex = new int[512];
        m_nameBlock = new String[512];
        
    }
    
    private void checkAdd(long from, long to, String name) {
        for (int i=0; i<m_countHint; i++) {
            if (m_nameHint[i].equals(name) && 
                    m_startHint[i] == from &&
                    m_stopHint[i] == to)
                return;
        }
        
        m_nameHint[m_countHint] = name;
        m_startHint[m_countHint] = from;
        m_stopHint[m_countHint] = to;
        m_countHint++;
    
    }

    public void syncBlocks() {
        int i, j;
        
        for (i=0; i<m_countBlock; i++) {
            m_startBlockIndex[i] = -1;
            if (m_startBlockIndex[i] < 0)
                m_startBlockIndex[i] = m_count-1;
            for (j=0; j<m_count; j++) {
                if (m_start[j] >= m_startBlock[i]) {
                    m_startBlockIndex[i] = j;
                    break;
                }
            }
            m_stopBlockIndex[i] = -1;
            for (j=0; j<m_count; j++) {
                if (m_stop[j] <= m_stopBlock[i]) {
                    m_stopBlockIndex[i] = j;
                }
            }
            
            System.out.println("SYNC START: " + i + " " + m_startBlockIndex[i] + " -> " + m_startBlock[i]);
            System.out.println("SYNC STOP:  " + i + " " + m_stopBlockIndex[i]);
            
            if (m_stopBlockIndex[i] < 0)
                m_stopBlockIndex[i] = m_count-1;
 
        }
    }
    
    public void checkAddBlock(long from, long to, String name) {
        for (int i=0; i<m_countBlock; i++) {
            if (m_nameBlock[i].equals(name) && 
                    m_startBlock[i] == from &&
                    m_stopBlock[i] == to)
                return;
        }
        
        m_nameBlock[m_countBlock] = name;
        m_startBlock[m_countBlock] = from;
        m_stopBlock[m_countBlock] = to;

        m_startBlockIndex[m_countBlock] = -1;
        m_stopBlockIndex[m_countBlock] = -1;
        m_countBlock++;
    
    }
    
    public void deleteBlock(int idx) {
        if (idx < 0)
            return;
        for (int i=idx; i<m_countBlock-1; i++) {
            m_nameBlock[i] = m_nameBlock[i+1];
            m_startBlock[i] = m_startBlock[i+1];
            m_stopBlock[i] = m_stopBlock[i+1];
            m_startBlockIndex[i] = m_startBlockIndex[i+1];
            m_stopBlockIndex[i] = m_stopBlockIndex[i+1];
        } 
        
        m_countBlock--;
        //syncBlocks();
    }
    
    
    public void clear() {
        m_countHint = 0;
        m_count = 0;
    }
    
    public void read(String fileName)  {
                    
        File file = new File(fileName); 
            
        m_count = 0;
        m_countHint = 0;
        m_countBlock = 0;
        
        Scanner sc;
        try {
            sc = new Scanner(file); 
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + fileName);
            return;
        }

        while (sc.hasNextLine()) {
            String str = sc.nextLine();
            if (str.equals(""))
                break;
                

            String[] split = str.split("\\s+");
            if (split.length == 0)
                continue;
            
            if (split[0].contains("#HINT")) {
                checkAdd(Integer.parseInt(split[2]), Integer.parseInt(split[3]), split[1]);
                continue;
            }
            
            if (split[0].contains("#BLOCK")) {
                checkAddBlock(Integer.parseInt(split[2]), Integer.parseInt(split[3]), split[1]);
                continue;
            }

            
            if (split[0].charAt(0) == '#') {
                continue;
            }
            
            String name = split[0];
            long from = Integer.parseInt(split[1]);
            long to = Integer.parseInt(split[2]);
            
            m_start[m_count] = from;
            m_stop[m_count] = to;
            m_name[m_count] = name;
            
            m_count++;

        }
        syncBlocks();
    }
    
    public int count() {
        return m_count;
    } 
    public long start(int i) {
        return m_start[i];
    } 
    public long stop(int i) {
        return m_stop[i];
    } 
    public String name(int i) {
        return m_name[i];
    } 

    public void addHint(long from, long to, String name) {
        m_startHint[m_countHint] = from;
        m_stopHint[m_countHint] = to;
        m_nameHint[m_countHint] = name;
        m_countHint++;
    }

    public void removeHint(int index) {
        for (int i=index; i<m_countHint-1; i++) {
            m_startHint[i] = m_startHint[i+1];
            m_stopHint[i] = m_stopHint[i+1];
            m_nameHint[i] = m_nameHint[i+1];
        }
        m_countHint--;
    }
    
    public int countHint() {
        return m_countHint;
    } 
    public long startHint(int i) {
        return m_startHint[i];
    } 
    public long stopHint(int i) {
        return m_stopHint[i];
    } 
    public String nameHint(int i) {
        return m_nameHint[i];
    } 


    public int countBlock() {
        return m_countBlock;
    } 
    public long startBlock(int i) {
        return m_startBlock[i];
    } 
    public long stopBlock(int i) {
        return m_stopBlock[i];
    } 
    public int startBlockIndex(int i) {
        return m_startBlockIndex[i];
    } 
    public int stopBlockIndex(int i) {
        return m_stopBlockIndex[i];
    } 
    public String nameBlock(int i) {
        return m_nameBlock[i];
    } 

}
