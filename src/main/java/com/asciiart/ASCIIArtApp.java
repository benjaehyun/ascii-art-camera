package com.asciiart;

import com.asciiart.camera.CameraManager;
import com.asciiart.processor.ImageProcessor;
import com.asciiart.processor.ASCIIConverter;
import com.asciiart.display.TerminalRenderer;
import com.asciiart.utils.SimpleKeyboardHandler;
import com.asciiart.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Main entry point for ASCII Art Camera application
 */
public class ASCIIArtApp {
    private static final Logger logger = LoggerFactory.getLogger(ASCIIArtApp.class);
    
    private CameraManager cameraManager;
    private ImageProcessor imageProcessor;
    private ASCIIConverter asciiConverter;
    private TerminalRenderer terminalRenderer;
    private SimpleKeyboardHandler keyboardHandler;
    private AtomicBoolean running = new AtomicBoolean(false);
    
    public ASCIIArtApp() {
        logger.info("Initializing ASCII Art Camera...");
    }
    
    public void initialize() {
        try {
            // Initialize components
            cameraManager = new CameraManager();
            imageProcessor = new ImageProcessor();
            asciiConverter = new ASCIIConverter();
            terminalRenderer = new TerminalRenderer();
            
            // Initialize camera
            if (!cameraManager.initialize()) {
                logger.error("Failed to initialize camera");
                System.exit(1);
            }
            
            // Initialize keyboard handler
            keyboardHandler = new SimpleKeyboardHandler(imageProcessor, asciiConverter, running);
            
            logger.info("Successfully initialized all components");
        } catch (Exception e) {
            logger.error("Failed to initialize application", e);
            System.exit(1);
        }
    }
    
    public void start() {
        logger.info("Starting ASCII Art Camera...");
        running.set(true);
        
        // Clear screen first
        terminalRenderer.clear();
        
        // Print instructions
        System.out.println("=== CONTROLS (type letter + Enter) ===");
        System.out.println("  +/- : Contrast     [/] : Brightness");
        System.out.println("  c   : Charset      1-4 : Resolution");
        System.out.println("  s   : Save frame   r   : Reset");
        System.out.println("  q   : Quit         h   : Help");
        System.out.println("=======================================");
        System.out.println("\nStarting in 2 seconds...\n");
        
        // Start keyboard handler
        keyboardHandler.start();
        
        // Add shutdown hook for cleanup
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
        
        // Wait for user to see instructions
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // Ignore
        }
        
        // Clear screen completely before starting rendering
        terminalRenderer.clear();
        
        // Main processing loop
        long frameTime = 1000 / 15; // Target 15 FPS
        int frameCount = 0;
        long startTime = System.currentTimeMillis();
        String currentAsciiArt = "";
        int lastWidth = 0;
        int lastHeight = 0;
        
        while (running.get()) {
            try {
                long frameStart = System.currentTimeMillis();
                
                // Capture frame from camera
                org.bytedeco.opencv.opencv_core.Mat frame = cameraManager.captureFrame();
                
                if (frame != null && !frame.empty()) {
                    // Process the frame: resize, grayscale, adjust
                    int[][] grayValues = imageProcessor.processFrame(frame);
                    
                    if (grayValues != null) {
                        // Check if resolution changed
                        boolean resolutionChanged = (grayValues[0].length != lastWidth || 
                                                    grayValues.length != lastHeight);
                        if (resolutionChanged) {
                            // Clear screen completely on resolution change
                            terminalRenderer.clear();
                            lastWidth = grayValues[0].length;
                            lastHeight = grayValues.length;
                        }
                        
                        // Convert to ASCII
                        currentAsciiArt = asciiConverter.convertToAscii(grayValues);
                        
                        // Check if we should save this frame
                        if (keyboardHandler.shouldSaveFrame()) {
                            FileUtils.saveAsciiArtWithMetadata(
                                currentAsciiArt,
                                asciiConverter.getCurrentCharset(),
                                imageProcessor.getContrast(),
                                imageProcessor.getBrightness(),
                                grayValues[0].length,
                                grayValues.length
                            );
                        }
                        
                        // Render to terminal
                        terminalRenderer.render(currentAsciiArt);
                        
                        // Add status line
                        frameCount++;
                        double fps = frameCount / ((System.currentTimeMillis() - startTime) / 1000.0);
                        
                        // Get any status message from keyboard handler
                        String statusMsg = keyboardHandler.getStatusMessage();
                        String status;
                        if (!statusMsg.isEmpty()) {
                            status = ">>> " + statusMsg + " | FPS: " + String.format("%.1f", fps);
                        } else {
                            status = String.format("FPS: %.1f | Commands: +/- [/] c 1-4 s r q h", fps);
                        }
                        terminalRenderer.renderStatus(status);
                    }
                    
                    // Clean up the frame
                    frame.release();
                }
                
                // Frame rate limiting
                long frameEnd = System.currentTimeMillis();
                long sleepTime = frameTime - (frameEnd - frameStart);
                if (sleepTime > 0) {
                    Thread.sleep(sleepTime);
                }
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                logger.error("Error in main loop", e);
                // Continue running despite errors
            }
        }
    }
    
    public void shutdown() {
        logger.info("Shutting down ASCII Art Camera...");
        running.set(false);
        
        if (keyboardHandler != null) {
            keyboardHandler.stop();
        }
        
        if (terminalRenderer != null) {
            terminalRenderer.cleanup();
        }
        
        if (cameraManager != null) {
            cameraManager.release();
        }
        
        logger.info("Shutdown complete");
    }
    
    public static void main(String[] args) {
        // Disable debug logging for cleaner display
        ch.qos.logback.classic.Logger rootLogger = 
            (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(ch.qos.logback.classic.Level.WARN);
        
        System.out.println("ASCII Art Camera v0.1.0");
        System.out.println("=======================");
        
        ASCIIArtApp app = new ASCIIArtApp();
        app.initialize();
        app.start();
    }
}