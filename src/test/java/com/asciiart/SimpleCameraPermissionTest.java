package com.asciiart;

import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameGrabber;

/**
 * Minimal test to trigger camera permission on macOS
 */
public class SimpleCameraPermissionTest {
    public static void main(String[] args) {
        System.out.println("Camera Permission Test");
        System.out.println("======================\n");
        System.out.println("This should trigger a camera permission request on macOS.\n");
        
        try {
            System.out.println("Attempting to access camera...");
            
            // This should trigger the permission dialog on macOS
            FrameGrabber grabber = new OpenCVFrameGrabber(0);
            grabber.start();
            
            System.out.println("✓ Camera access granted!");
            System.out.println("✓ Camera opened successfully!");
            
            // Clean up
            grabber.stop();
            grabber.release();
            
            System.out.println("✓ Camera released successfully!");
            System.out.println("\nYou can now run the full application.");
            
        } catch (Exception e) {
            System.err.println("✗ Failed to access camera!");
            System.err.println("Error: " + e.getMessage());
            System.err.println("\nPossible issues:");
            System.err.println("1. Camera permissions denied");
            System.err.println("2. No camera available");
            System.err.println("3. Camera in use by another application");
            
            System.err.println("\nTo fix on macOS:");
            System.err.println("1. Go to System Settings > Privacy & Security > Camera");
            System.err.println("2. Enable camera access for Terminal");
            System.err.println("3. You may need to restart Terminal");
            
            e.printStackTrace();
        }
    }
}
