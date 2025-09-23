package com.asciiart.camera;

import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;

/**
 * Manages camera operations and frame capture
 */
public class CameraManager {
    private static final Logger logger = LoggerFactory.getLogger(CameraManager.class);
    
    private FrameGrabber grabber;
    private OpenCVFrameConverter.ToMat converterToMat;
    private int deviceId = 0; // Default camera
    private int frameWidth = 640;
    private int frameHeight = 480;
    private double fps = 30.0;
    
    public CameraManager() {
        this.converterToMat = new OpenCVFrameConverter.ToMat();
    }
    
    /**
     * Initialize camera with default settings
     */
    public boolean initialize() {
        return initialize(deviceId, frameWidth, frameHeight, fps);
    }
    
    /**
     * Initialize camera with custom settings
     */
    public boolean initialize(int deviceId, int width, int height, double fps) {
        try {
            logger.info("Initializing camera {} with resolution {}x{} @ {}fps", 
                       deviceId, width, height, fps);
            
            // Create grabber for the specified camera
            grabber = new OpenCVFrameGrabber(deviceId);
            grabber.setImageWidth(width);
            grabber.setImageHeight(height);
            grabber.setFrameRate(fps);
            
            // Start the grabber
            grabber.start();
            
            // Test capture to ensure camera is working
            Frame testFrame = grabber.grab();
            if (testFrame == null) {
                logger.error("Failed to capture test frame");
                return false;
            }
            
            logger.info("Camera initialized successfully");
            return true;
            
        } catch (FrameGrabber.Exception e) {
            logger.error("Failed to initialize camera", e);
            return false;
        }
    }
    
    /**
     * Capture a single frame from the camera
     */
    public Mat captureFrame() {
        try {
            Frame frame = grabber.grab();
            if (frame == null) {
                logger.warn("Captured null frame");
                return null;
            }
            
            // Convert Frame to OpenCV Mat
            Mat mat = converterToMat.convert(frame);
            return mat;
            
        } catch (FrameGrabber.Exception e) {
            logger.error("Failed to capture frame", e);
            return null;
        }
    }
    
    /**
     * Get current frame dimensions
     */
    public int getFrameWidth() {
        return grabber != null ? grabber.getImageWidth() : 0;
    }
    
    public int getFrameHeight() {
        return grabber != null ? grabber.getImageHeight() : 0;
    }
    
    /**
     * List available camera devices
     */
    public static int detectCameras() {
        int cameraCount = 0;
        logger.info("Detecting available cameras...");
        
        // Try to open cameras 0-4 to detect available devices
        for (int i = 0; i < 5; i++) {
            try (FrameGrabber testGrabber = new OpenCVFrameGrabber(i)) {
                testGrabber.start();
                logger.info("Camera {} detected", i);
                cameraCount++;
                testGrabber.stop();
            } catch (Exception e) {
                // Camera not available at this index
                break;
            }
        }
        
        logger.info("Found {} camera(s)", cameraCount);
        return cameraCount;
    }
    
    /**
     * Release camera resources
     */
    public void release() {
        if (grabber != null) {
            try {
                grabber.stop();
                grabber.release();
                logger.info("Camera released");
            } catch (FrameGrabber.Exception e) {
                logger.error("Error releasing camera", e);
            }
        }
    }
}