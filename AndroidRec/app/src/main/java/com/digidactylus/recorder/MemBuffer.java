/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digidactylus.recorder;

/**
 *
 * @author manfred
 * 
 * Parent class for buffer communication. 
 */
public class MemBuffer {
    
    // Reset pointer to 0.
    public MemBuffer() {
        m_ptr = 0;
    }
    
    public int size() {return m_buffer.length;}
    
    // Reset
    public void close() {
        m_ptr = 0;
    }
    
    // At the end?
    public boolean isEnd() {
        return (m_ptr == size());
    } 
    
    // Returns the buffer
    public byte [] getBuffer() {return m_buffer;}
    
    // Pretends that c is an unsigned char
    public int toInt(byte c) {
        if (c >= 0) 
            return c;
        else
            return 0x100 + (int)c;
    }
    
    // Pretends that i is an unsigned int
    public long toLong(int i) {
        if (i >= 0) 
            return i;
        else
            return 0x100000000L + (long)i;
    
    }
    
    // Members: data buffer and read/write pointer
    byte [] m_buffer;
    int m_ptr;
}
