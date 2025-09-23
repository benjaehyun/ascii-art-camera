package com.asciiart;

import com.asciiart.camera.CameraManager;
import com.asciiart.processor.ImageProcessor;
import com.asciiart.processor.ASCIIConverter;
import com.asciiart.display.TerminalRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;

/**
 * Main entry point for ASCII Art Camera application
 */
public class ASCIIArtApp {
    private static final Logger logger = LoggerFactory.getLogger(ASCIIArtApp.class);
    
    private CameraManager cameraManager;
    private ImageProcessor imageProcessor;
    private ASCIIConverter asciiConverter;
    private TerminalRenderer terminalRenderer;
    private volatile boolean running = false;
    
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
            
            logger.info("Successfully initialized all components");
        } catch (Exception e) {
            logger.error("Failed to initialize application", e);
            System.exit(1);
        }
    }
    
    public void start() {
        logger.info("Starting ASCII Art Camera...");
        running = true;
        
        // Add shutdown hook for cleanup
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
        
        // Clear screen initially
        terminalRenderer.clear();
        
        // Main processing loop
        long frameTime = 1000 / 15; // Target 15 FPS
        int frameCount = 0;
        long startTime = System.currentTimeMillis();
        
        while (running) {
            try {
                long frameStart = System.currentTimeMillis();
                
                // Capture frame from camera
                org.bytedeco.opencv.opencv_core.Mat frame = cameraManager.captureFrame();
                
                if (frame != null && !frame.empty()) {
                    // Process the frame: resize, grayscale, adjust
                    int[][] grayValues = imageProcessor.processFrame(frame);
                    
                    if (grayValues != null) {
                        // Convert to ASCII
                        String asciiArt = asciiConverter.convertToAscii(grayValues);
                        
                        // Render to terminal
                        terminalRenderer.render(asciiArt);
                        
                        // Add status line
                        frameCount++;
                        if (frameCount % 30 == 0) { // Update status every 30 frames
                            double fps = frameCount / ((System.currentTimeMillis() - startTime) / 1000.0);
                            String status = String.format("FPS: %.1f | Frame: %d | Press Ctrl+C to exit", fps, frameCount);
                            terminalRenderer.renderStatus(status);
                        }
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
        running = false;
        
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