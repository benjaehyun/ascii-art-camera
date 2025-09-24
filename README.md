# ASCII Art Camera

Real-time ASCII art generator from live webcam feed in Java.

## Overview

Initial project using camera data and translating it into a visual representation in text. 
Primary goal and use for this project is to capture webcam video and converts it to ASCII characters displayed in terminal with minimal input delay and reasonable fps.

## Tech Stack

- **Java 11**
- **OpenCV/JavaCV** - Camera capture and image processing
- **JNA** - Terminal control
- **Maven** - Build management

## Setup

### Prerequisites
- JDK 11+
- Maven
- Git

### Installation

```bash
# Clone repo
git clone https://github.com/benjaehyun/ascii-art-camera.git
cd ascii-art-camera

# Install dependencies
mvn clean install

# Run
java -jar target/ascii-art-camera-0.1.0-SNAPSHOT-jar-with-dependencies.jar
```

## Camera API Selection

Chose OpenCV/JavaCV for:
- Cross-platform compatibility
- Native performance
- Robust image processing capabilities
- Active Java bindings support

## Core Concept

1. Capture frame from webcam
2. Convert to grayscale
3. Map brightness values (0-255) to ASCII characters
4. Render in terminal at target FPS

## Project Status

- [x] Project structure setup
- [x] Dependency configuration
- [x] Camera integration
- [x] ASCII conversion algorithm
- [x] Terminal rendering
- [x] Keyboard controls
- [x] Clean up terminal instructions and resolution updates
- [ ] Additional adjustments and feature sets for terminal display (color, saves, alternative character sets)

