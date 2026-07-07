#!/usr/bin/env python3
"""
Build script for krsHubSwap Fabric Mod
Compiles Java files and creates JAR archive
"""

import os
import sys
import subprocess
import zipfile
from pathlib import Path

def main():
    print("Building KrsHubSwap Fabric Mod...")
    
    # Check if gradle exists
    if sys.platform == "win32":
        gradle_cmd = "gradlew.bat"
    else:
        gradle_cmd = "./gradlew"
    
    # Download gradlew if not exists
    if not os.path.exists(gradle_cmd):
        print("Downloading gradle wrapper...")
        os.system("gradle wrapper --gradle-version 7.3")
    
    # Build using gradle
    print("\nBuilding with Gradle...")
    try:
        result = subprocess.run([gradle_cmd, "build"], check=True)
        if result.returncode == 0:
            print("\n" + "="*50)
            print("Build completed successfully!")
            print("JAR file: build/libs/krshubswap-1.0.0.jar")
            print("="*50)
            return 0
    except FileNotFoundError:
        print("ERROR: Gradle not found!")
        print("Please install Gradle or use: gradle wrapper --gradle-version 7.3")
        return 1
    except subprocess.CalledProcessError as e:
        print(f"ERROR: Build failed with code {e.returncode}")
        return 1
    
    return 0

if __name__ == "__main__":
    sys.exit(main())
