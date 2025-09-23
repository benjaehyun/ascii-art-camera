package com.asciiart;

import com.asciiart.camera.CameraManager;

/**
 * Simple test to verify camera detection
 */
public class CameraTest {
    public static void main(String[] args) {
        System.out.println("Camera Detection Test");
        System.out.println("====================\n");
        
        // Detect available cameras
        int cameraCount = CameraManager.detectCameras();
        
        if (cameraCount == 0) {
            System.out.println("No cameras detected!");
            System.out.println("\nTroubleshooting:");
            System.out.println("1. Check if your camera is connected");
            System.out.println("2. On Mac, ensure terminal has camera permissions");
            System.out.println("   (System Preferences > Security & Privacy > Camera)");
            System.exit(1);
        }
        
        System.out.println("Found " + cameraCount + " camera(s)\n");
        
        // Try to initialize first camera
        System.out.println("Attempting to initialize camera 0...");
        CameraManager camera = new CameraManager();
        
        if (camera.initialize()) {
            System.out.println("✓ Camera initialized successfully!");
            System.out.println("  Resolution: " + camera.getFrameWidth() + "x" + camera.getFrameHeight());
            
            // Clean up
            camera.release();
            System.out.println("✓ Camera released successfully!");
        } else {
            System.out.println("✗ Failed to initialize camera");
        }
    }
}
