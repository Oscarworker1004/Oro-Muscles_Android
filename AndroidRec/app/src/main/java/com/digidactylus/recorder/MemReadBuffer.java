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
 * Class to read data from a memory stream.
 * 
 * This class is mirrored by MemWriteBuffer, and changes here need to be
 * reflected there too.
 *
 */

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;


public class MemReadBuffer extends MemBuffer {

    // Nothing to do here
    public MemReadBuffer() {
    }
    
    
    // Fill the buffer from a byte array
    public void setBuffer(byte [] data) {
        int i;
        m_buffer = new byte[data.length];
        for (i=0; i<m_buffer.length; i++) {
            m_buffer[i] = data[i];
        }
        m_ptr = 0;
        m_bufSize = m_buffer.length;
    }
    
    // Read from a binary file
    // Note that this code is copied in MemWriteBuffer, and that this
    // impementation could move to the parent MemBuffer.
    public void readFromFile(String fileName) throws Exception {
        FileInputStream fs = new FileInputStream(fileName);
        DataInputStream is = new DataInputStream(fs);
        m_buffer = new byte[0x1000000];
        int keep = m_ptr;
        
        while (is.available() > 0) {
            byte b = is.readByte();
            writeByte(b);
            m_bufSize++;
        }
        is.close();
        m_ptr = keep;
    }
   
    // Write to a binary file. Comment: see above.
    public void writeToFile(String fileName) throws Exception {
        FileOutputStream fs = new FileOutputStream(fileName);
        DataOutputStream os = new DataOutputStream(fs);
      
        for (int i=0; i<m_ptr; i++) {
            os.writeByte(m_buffer[i]);
        }
        os.close();
    }
   
    // Read a 4 byte integer
    public int readInt() {
        byte a = readByte();
        byte b = readByte();
        byte c = readByte();
        byte d = readByte();
        int i = super.toInt(a) + 0x100*super.toInt(b) + 0x10000*super.toInt(c) + 0x1000000*super.toInt(d);
        return i;
    }

    // Read a single byte: main direct access to the buffer
    public byte readByte() {
        byte c = m_buffer[m_ptr];
        m_ptr++;
        return c;
    }

    // Read 8 byte long
    public long readLong() {    
        int a = readInt();
        int b = readInt();
        return super.toLong(b) * 0x100000000L + super.toLong(a);
    }

    // Read double
    public double readDouble() {
        long l = readLong();
        double d = Double.longBitsToDouble(l);
        return d;
    }

    // Is there more data left?
    public boolean hasData() {
        if (m_ptr >= m_bufSize)
            return true;
        else
            return false;
    }
    
    // Need this for initializing the buffer
    void writeByte(byte b) {
        int len = 0x1000000;
        if (m_ptr == m_buffer.length) {
            byte [] tmp = new byte[m_buffer.length+len];
            System.arraycopy(m_buffer, 0, tmp, 0, m_buffer.length);
            m_buffer = tmp;
        }
                  
        m_buffer[m_ptr] = b;
        m_ptr++;
    }

    // Read lentgh of the string and then its content
    public String readString() {
        String s = new String();
        int l = readInt();
        for (int i=0; i<l; i++) {
            byte b = readByte();
            char c = (char)b;
            s += c;
        }
        return s;
    }
    
    
    
    
    
    int m_bufSize;
}
