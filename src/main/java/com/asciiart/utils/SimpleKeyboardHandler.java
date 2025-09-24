package com.asciiart.utils;

import com.asciiart.processor.ImageProcessor;
import com.asciiart.processor.ASCIIConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Simple keyboard handler using a separate input thread
 */
public class SimpleKeyboardHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(SimpleKeyboardHandler.class);
    
    private final ImageProcessor imageProcessor;
    private final ASCIIConverter asciiConverter;
    private final AtomicBoolean running;
    private final LinkedBlockingQueue<String> commandQueue;
    private Thread inputThread;
    private volatile String lastMessage = "";
    private volatile long messageTime = 0;
    private boolean saveNextFrame = false;
    
    public SimpleKeyboardHandler(ImageProcessor imageProcessor, 
                                ASCIIConverter asciiConverter,
                                AtomicBoolean running) {
        this.imageProcessor = imageProcessor;
        this.asciiConverter = asciiConverter;
        this.running = running;
        this.commandQueue = new LinkedBlockingQueue<>();
    }
    
    /**
     * Start the keyboard input thread
     */
    public void start() {
        inputThread = new Thread(this, "Keyboard-Input");
        inputThread.setDaemon(true);
        inputThread.start();
        logger.info("Keyboard handler started");
    }
    
    /**
     * Stop the keyboard input thread
     */
    public void stop() {
        if (inputThread != null) {
            inputThread.interrupt();
        }
    }
    
    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        
        // Start a separate thread to process commands
        Thread processor = new Thread(this::processCommands);
        processor.setDaemon(true);
        processor.start();
        
        // Read input in this thread
        while (running.get()) {
            try {
                if (scanner.hasNextLine()) {
                    String input = scanner.nextLine().trim();
                    if (!input.isEmpty()) {
                        commandQueue.offer(input);
                    }
                }
            } catch (Exception e) {
                // Ignore exceptions and continue
            }
        }
        scanner.close();
    }
    
    /**
     * Process commands from the queue
     */
    private void processCommands() {
        while (running.get()) {
            try {
                String command = commandQueue.poll();
                if (command != null && !command.isEmpty()) {
                    handleCommand(command.charAt(0));
                }
                Thread.sleep(50);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
    
    /**
     * Process keyboard command
     */
    private void handleCommand(char key) {
        String message = "";
        
        switch (Character.toLowerCase(key)) {
            case '+':
                imageProcessor.adjustContrast(0.2);
                message = "Contrast increased";
                break;
            case '-':
                imageProcessor.adjustContrast(-0.2);
                message = "Contrast decreased";
                break;
            case '[':
                imageProcessor.adjustBrightness(-20);
                message = "Brightness decreased";
                break;
            case ']':
                imageProcessor.adjustBrightness(20);
                message = "Brightness increased";
                break;
            case 'c':
                asciiConverter.cycleCharset();
                message = "Character set changed";
                break;
            case '1':
                clearInputBuffer();
                imageProcessor.setTargetDimensions(40, 15);
                message = "Low resolution (40x15)";
                break;
            case '2':
                clearInputBuffer();
                imageProcessor.setTargetDimensions(80, 24);
                message = "Medium resolution (80x24)";
                break;
            case '3':
                clearInputBuffer();
                imageProcessor.setTargetDimensions(120, 40);
                message = "High resolution (120x40)";
                break;
            case '4':
                clearInputBuffer();
                imageProcessor.setTargetDimensions(160, 50);
                message = "Ultra resolution (160x50)";
                break;
            case 's':
                saveNextFrame = true;
                message = "Saving next frame...";
                break;
            case 'r':
                resetSettings();
                message = "Settings reset";
                break;
            case 'h':
            case '?':
                printHelp();
                break;
            case 'q':
                logger.info("Quit command received");
                running.set(false);
                break;
            default:
                message = "Unknown command: " + key;
                break;
        }
        
        if (!message.isEmpty()) {
            setMessage(message);
        }
    }
    
    /**
     * Clear the input buffer to prevent command overlap
     */
    private void clearInputBuffer() {
        commandQueue.clear();
        try {
            while (System.in.available() > 0) {
                System.in.read();
            }
        } catch (IOException e) {
            // Ignore
        }
    }
    
    /**
     * Reset settings to default
     */
    private void resetSettings() {
        imageProcessor.setTargetDimensions(80, 24);
        // Reset contrast to 1.0
        double currentContrast = imageProcessor.getContrast();
        imageProcessor.adjustContrast(1.0 - currentContrast);
        // Reset brightness to 0
        int currentBrightness = imageProcessor.getBrightness();
        imageProcessor.adjustBrightness(0 - currentBrightness);
        asciiConverter.setCharset(ASCIIConverter.SIMPLE_CHARSET);
    }
    
    /**
     * Print help to console (will be visible after quit)
     */
    private void printHelp() {
        setMessage("Controls: +/- contrast, [/] brightness, c charset, 1-4 resolution, s save, r reset, q quit");
    }
    
    /**
     * Set a message to display
     */
    private void setMessage(String msg) {
        lastMessage = msg;
        messageTime = System.currentTimeMillis();
    }
    
    /**
     * Get current status message
     */
    public String getStatusMessage() {
        // Show message for 3 seconds
        if (System.currentTimeMillis() - messageTime < 3000) {
            return lastMessage;
        }
        return "";
    }
    
    /**
     * Check if should save frame
     */
    public boolean shouldSaveFrame() {
        if (saveNextFrame) {
            saveNextFrame = false;
            return true;
        }
        return false;
    }
}