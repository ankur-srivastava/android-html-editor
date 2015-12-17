package com.zen.androidhtmleditor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharacterCodingException;
 
/**
    Test if char is ASCII or not
*/
 
 
public class TestAscii {
 
    public static String main (File file)
    throws Exception {
        // this String throws an Exception, it contains an accented letter
        //String test = "Réal";
    	
        // this String is OK
        //String test = "Real";
    	
    	StringBuffer fileData = new StringBuffer(1000);
        BufferedReader reader = new BufferedReader(
                new FileReader(file));
        char[] buf = new char[1024];
        int numRead=0;
        while((numRead=reader.read(buf)) != -1){
            fileData.append(buf, 0, numRead);
        }
        reader.close();
    	String test = fileData.toString();
        byte bytearray []  = test.getBytes();
        //System.out.println("Test string : " + test);
 
        CharsetDecoder d = Charset.forName("US-ASCII").newDecoder();
        try {
            CharBuffer r = d.decode(ByteBuffer.wrap(bytearray));
            r.toString();
        }
        catch(CharacterCodingException e) {
            System.out.println("only regular ASCII characters please!");
            return "BIN";
            // interrupt the processing
           
        }
        System.out.println("Ok, it's ASCII only!");
        return "ASCII";
    }
}