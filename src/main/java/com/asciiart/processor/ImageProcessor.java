package com.asciiart.processor;

import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Size;
import static org.bytedeco.opencv.global.opencv_imgproc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Processes images for ASCII conversion
 */
public class ImageProcessor {
    private static final Logger logger = LoggerFactory.getLogger(ImageProcessor.class);
    
    private int targetWidth = 80;  // Default terminal width
    private int targetHeight = 24; // Default terminal height
    private double contrast = 1.0;
    private int brightness = 0;
    
    public ImageProcessor() {
        logger.debug("ImageProcessor initialized with default settings");
    }
    
    /**
     * Convert color image to grayscale
     */
    public Mat toGrayscale(Mat colorImage) {
        if (colorImage == null) {
            return null;
        }
        
        Mat grayImage = new Mat();
        
        // Check if image is already grayscale
        if (colorImage.channels() == 1) {
            return colorImage;
        }
        
        // Convert to grayscale
        cvtColor(colorImage, grayImage, COLOR_BGR2GRAY);
        return grayImage;
    }
    
    /**
     * Resize image to target dimensions for terminal display
     */
    public Mat resize(Mat image, int width, int height) {
        if (image == null) {
            return null;
        }
        
        Mat resized = new Mat();
        Size size = new Size(width, height);
        // Use fully qualified OpenCV resize function to avoid naming conflict
        org.bytedeco.opencv.global.opencv_imgproc.resize(image, resized, size, 0, 0, INTER_LINEAR);
        
        logger.debug("Image resized to {}x{}", width, height);
        return resized;
    }
    
    /**
     * Resize image to fit terminal maintaining aspect ratio
     */
    public Mat resizeForTerminal(Mat image) {
        if (image == null) {
            return null;
        }
        
        // Calculate scaling to maintain aspect ratio
        // Account for terminal characters being taller than wide (roughly 2:1)
        double imageAspect = (double) image.cols() / image.rows();
        double terminalAspect = (double) targetWidth / (targetHeight * 2.0);
        
        int finalWidth, finalHeight;
        
        if (imageAspect > terminalAspect) {
            // Image is wider - fit to width
            finalWidth = targetWidth;
            finalHeight = (int) (targetWidth / imageAspect / 2.0);
        } else {
            // Image is taller - fit to height
            finalHeight = targetHeight;
            finalWidth = (int) (targetHeight * imageAspect * 2.0);
        }
        
        return resize(image, finalWidth, finalHeight);
    }
    
    /**
     * Apply contrast and brightness adjustments
     */
    public Mat adjustContrastBrightness(Mat image) {
        if (image == null) {
            return null;
        }
        
        Mat adjusted = new Mat();
        image.convertTo(adjusted, -1, contrast, brightness);
        return adjusted;
    }
    
    /**
     * Convert Mat to 2D array of grayscale values
     */
    public int[][] matToGrayscaleArray(Mat grayMat) {
        if (grayMat == null || grayMat.channels() != 1) {
            return null;
        }
        
        int rows = grayMat.rows();
        int cols = grayMat.cols();
        int[][] result = new int[rows][cols];
        
        byte[] data = new byte[rows * cols];
        grayMat.data().get(data);
        
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                // Convert byte to unsigned int (0-255)
                result[y][x] = data[y * cols + x] & 0xFF;
            }
        }
        
        return result;
    }
    
    /**
     * Process complete pipeline: color -> grayscale -> resize -> adjust -> array
     */
    public int[][] processFrame(Mat frame) {
        Mat processed = toGrayscale(frame);
        processed = resizeForTerminal(processed);
        processed = adjustContrastBrightness(processed);
        return matToGrayscaleArray(processed);
    }
    
    // Getters and setters
    public void setTargetDimensions(int width, int height) {
        this.targetWidth = width;
        this.targetHeight = height;
    }
    
    public void adjustContrast(double delta) {
        this.contrast = Math.max(0.5, Math.min(3.0, contrast + delta));
        logger.info("Contrast adjusted to {}", contrast);
    }
    
    public void adjustBrightness(int delta) {
        this.brightness = Math.max(-100, Math.min(100, brightness + delta));
        logger.info("Brightness adjusted to {}", brightness);
    }
}