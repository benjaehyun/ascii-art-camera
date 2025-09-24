package com.asciiart.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for file operations
 */
public class FileUtils {
    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    
    /**
     * Save ASCII art to a text file
     */
    public static boolean saveAsciiArt(String asciiArt) {
        String timestamp = LocalDateTime.now().format(formatter);
        String filename = "ascii_art_" + timestamp + ".txt";
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write("ASCII Art Camera - Captured Frame\n");
            writer.write("Timestamp: " + LocalDateTime.now() + "\n");
            writer.write("=====================================\n\n");
            writer.write(asciiArt);
            writer.write("\n=====================================\n");
            
            logger.info("ASCII art saved to {}", filename);
            System.out.println("\n>>> Frame saved to: " + filename);
            return true;
            
        } catch (IOException e) {
            logger.error("Failed to save ASCII art", e);
            System.err.println(">>> Error saving frame: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Save ASCII art with metadata
     */
    public static boolean saveAsciiArtWithMetadata(String asciiArt, String charset, 
                                                  double contrast, int brightness,
                                                  int width, int height) {
        String timestamp = LocalDateTime.now().format(formatter);
        String filename = "ascii_art_" + timestamp + ".txt";
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write("ASCII Art Camera - Captured Frame\n");
            writer.write("=====================================\n");
            writer.write("Timestamp: " + LocalDateTime.now() + "\n");
            writer.write("Resolution: " + width + "x" + height + "\n");
            writer.write("Character Set: " + charset + "\n");
            writer.write("Contrast: " + contrast + "\n");
            writer.write("Brightness: " + brightness + "\n");
            writer.write("=====================================\n\n");
            writer.write(asciiArt);
            writer.write("\n=====================================\n");
            
            logger.info("ASCII art with metadata saved to {}", filename);
            System.out.println("\n>>> Frame saved to: " + filename);
            return true;
            
        } catch (IOException e) {
            logger.error("Failed to save ASCII art with metadata", e);
            System.err.println(">>> Error saving frame: " + e.getMessage());
            return false;
        }
    }
}