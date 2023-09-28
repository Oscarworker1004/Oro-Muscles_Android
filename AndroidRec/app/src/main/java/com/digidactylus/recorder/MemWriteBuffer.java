/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digidactylus.recorder;
import java.io.*;
import java.util.Arrays;
import java.nio.ByteBuffer;
import java.nio.charset.*;

	
/**
 *
 * @author manfred
 * A generic buffer in memory that can be written to.
 * The buffer keeps track of how much memory was written to it.
 
 * This class is mirrored by MemReadBuffer, and changes here need to be
 * reflected there too.
 * 
 * NOTE: There are more elegant ways than using a byte array directly!
 */
public class MemWriteBuffer extends MemBuffer {
    
    // Pre-allocate some memory
    public MemWriteBuffer() {
        m_buffer = new byte[0x1000000];
    }
    
    // Write an integer (4 bytes)
    public void writeInt(int n) {       
        // Cut into bytes for little-endinaness  
        // Cut into bytes for little-endinaness
        byte a = (byte)n;
        //n /= 256;
        byte b = (byte)(n >>> 8);
        //n /= 256;
        byte c = (byte)(n >>> 16);
        //n /= 256;
        byte d = (byte)(n >>> 24);

        writeByte(a);
        writeByte(b);
        writeByte(c);
        writeByte(d);
    }

    // Initialize the buffer from a binary file
    public void readFromFile(String fileName) throws Exception {
        FileInputStream fs = new FileInputStream(fileName);
        DataInputStream is = new DataInputStream(fs);
        
        // Allocate
        m_buffer = new byte[0x1000000];
        int keep = m_ptr;
        
        // Read until EOF.
        while (is.available() > 0) {
            byte b = is.readByte();
            writeByte(b);
        }
        is.close();
        
        // For debug
        System.out.print("Bytes read: " + m_ptr);
        System.out.print("\n");

        // Make sure to allocate only as much
        // memory as is needed.
        compact();
        
   
    }
 
    // Write the contents to a binary file
    public void writeToFile(String fileName) throws Exception {
        FileOutputStream fs = new FileOutputStream(fileName);
        DataOutputStream os = new DataOutputStream(fs);
      
        for (int i=0; i<m_ptr; i++) {
            os.writeByte(m_buffer[i]);
        }
        os.close();
    }
 
    // Write a single byte - this is the single direct access to the buffer.
    public void writeByte(byte b) {
        int len = 16000;
        if (m_ptr == m_buffer.length) {
            byte [] tmp = new byte[m_buffer.length+len];
            System.arraycopy(m_buffer, 0, tmp, 0, m_buffer.length);
            m_buffer = tmp;
        }
                  
        m_buffer[m_ptr] = b;
        m_ptr++;
 
    }
    
    // After writing, we want to shrink the buffer to its
    // actual size.
    public void compact() {
        byte [] tmp = new byte[m_ptr];
        System.arraycopy(m_buffer, 0, tmp, 0, m_ptr);
        m_buffer = tmp;        
    }

    // Write 8-byte long
    public void writeLong(long l) {    
        int a = (int)l;
        int b = (int)(l/0x100000000L);
        writeInt(a);
        writeInt(b);
    }

    // Write 8-byte double
    public void writeDouble(double d) {
        // Convert to long, then write
        long tmp = Double.doubleToLongBits(d);
        writeLong(tmp);
    }

    // Write the length of the string, then the content itself
    public void writeString(String s) {
        int l = s.length();
        writeInt(l);
        ByteBuffer byteBuffer = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            byteBuffer = StandardCharsets.UTF_8.encode(s);
        } else {
            // DO SOMETHING????
        }
        for (int i=0; i<l; i++) {
            writeByte(byteBuffer.get(i));
        }
    
    }
    
}
