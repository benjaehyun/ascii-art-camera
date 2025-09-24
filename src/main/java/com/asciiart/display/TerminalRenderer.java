package com.asciiart.display;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles terminal rendering and display
 */
public class TerminalRenderer {
    private static final Logger logger = LoggerFactory.getLogger(TerminalRenderer.class);
    
    // ANSI escape codes
    private static final String CLEAR_SCREEN = "\033[H\033[2J";
    private static final String CURSOR_HOME = "\033[H";
    private static final String HIDE_CURSOR = "\033[?25l";
    private static final String SHOW_CURSOR = "\033[?25h";
    private static final String RESET = "\033[0m";
    
    private boolean useAnsiCodes = true;
    private int frameCount = 0;
    
    public TerminalRenderer() {
        // Check if terminal supports ANSI codes
        String term = System.getenv("TERM");
        useAnsiCodes = term != null && !term.equals("dumb");
        
        if (useAnsiCodes) {
            initialize();
        }
    }
    
    /**
     * Initialize terminal for rendering
     */
    private void initialize() {
        System.out.print(HIDE_CURSOR);
        System.out.print(CLEAR_SCREEN);
        logger.debug("Terminal initialized with ANSI support");
    }
    
    /**
     * Clear the terminal screen
     */
    public void clear() {
        if (useAnsiCodes) {
            // Clear screen and scrollback buffer
            System.out.print("\033[2J");     // Clear screen
            System.out.print("\033[3J");     // Clear scrollback
            System.out.print("\033[H");      // Move cursor home
            System.out.flush();
        } else {
            // Fallback for non-ANSI terminals
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }
    
    /**
     * Render ASCII art to terminal
     */
    public void render(String asciiArt) {
        if (asciiArt == null || asciiArt.isEmpty()) {
            return;
        }
        
        if (useAnsiCodes) {
            // Clear entire screen first for clean rendering
            System.out.print("\033[2J");   // Clear screen
            System.out.print("\033[H");    // Move cursor home
        }
        
        System.out.print(asciiArt);
        System.out.flush();
        
        frameCount++;
    }
    
    /**
     * Display status line at bottom of screen
     */
    public void renderStatus(String status) {
        System.out.println("\n" + status);
        System.out.flush();
    }
    
    /**
     * Get terminal dimensions (simplified for now)
     */
    public int[] getTerminalSize() {
        // Default size - will be improved with JNA integration later
        return new int[]{80, 24};
    }
    
    /**
     * Cleanup terminal on exit
     */
    public void cleanup() {
        if (useAnsiCodes) {
            System.out.print(SHOW_CURSOR);
            System.out.print(RESET);
            System.out.print(CLEAR_SCREEN);
            System.out.flush();
        }
        logger.debug("Terminal cleanup complete. Rendered {} frames", frameCount);
    }
    
    public int getFrameCount() {
        return frameCount;
    }
}