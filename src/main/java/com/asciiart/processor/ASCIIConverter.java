package com.asciiart.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Converts grayscale values to ASCII characters
 */
public class ASCIIConverter {
    private static final Logger logger = LoggerFactory.getLogger(ASCIIConverter.class);
    
    // Default ASCII character sets ordered from darkest to brightest
    public static final String SIMPLE_CHARSET = " .:-=+*#%@";
    public static final String EXTENDED_CHARSET = " .'`^\",:;Il!i><~+_-?][}{1)(|/tfjrxnuvczXYUJCLQ0OZmwqpdbkhao*#MW&8%B@$";
    public static final String BLOCK_CHARSET = " ░▒▓█";
    
    private String currentCharset;
    private char[] charArray;
    
    public ASCIIConverter() {
        this(SIMPLE_CHARSET);
    }
    
    public ASCIIConverter(String charset) {
        setCharset(charset);
    }
    
    /**
     * Set the character set to use for conversion
     */
    public void setCharset(String charset) {
        this.currentCharset = charset;
        this.charArray = charset.toCharArray();
        logger.debug("Charset updated: {} characters", charArray.length);
    }
    
    /**
     * Convert a single grayscale value (0-255) to ASCII character
     */
    public char grayscaleToChar(int grayValue) {
        // Ensure gray value is in valid range
        grayValue = Math.max(0, Math.min(255, grayValue));
        
        // Map gray value to character index
        int index = (grayValue * (charArray.length - 1)) / 255;
        return charArray[index];
    }
    
    /**
     * Convert 2D array of grayscale values to ASCII string
     */
    public String convertToAscii(int[][] grayValues) {
        StringBuilder result = new StringBuilder();
        
        for (int y = 0; y < grayValues.length; y++) {
            for (int x = 0; x < grayValues[y].length; x++) {
                result.append(grayscaleToChar(grayValues[y][x]));
            }
            result.append('\n');
        }
        
        return result.toString();
    }
    
    /**
     * Get current character set
     */
    public String getCurrentCharset() {
        return currentCharset;
    }
    
    /**
     * Cycle through available character sets
     */
    public void cycleCharset() {
        if (currentCharset.equals(SIMPLE_CHARSET)) {
            setCharset(EXTENDED_CHARSET);
            logger.info("Switched to extended charset");
        } else if (currentCharset.equals(EXTENDED_CHARSET)) {
            setCharset(BLOCK_CHARSET);
            logger.info("Switched to block charset");
        } else {
            setCharset(SIMPLE_CHARSET);
            logger.info("Switched to simple charset");
        }
    }
}