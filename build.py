#!/usr/bin/env python3
"""
Build script for krsHubSwap
Compiles Java files and creates JAR archive without using jar command
"""

import os
import sys
import subprocess
import zipfile
from pathlib import Path

def main():
    print("Building krsHubSwap...")
    
    # Define paths
    src_dir = Path("src/main/java")
    build_dir = Path("build")
    classes_dir = build_dir / "classes"
    libs_dir = build_dir / "libs"
    
    # Java files to compile
    java_files = [
        "ru/krosovok/krshubswap/client/manager/RangeConfig.java",
        "ru/krosovok/krshubswap/client/manager/AliasConfig.java",
        "ru/krosovok/krshubswap/client/manager/TeleportManager.java",
        "ru/krosovok/krshubswap/client/listener/ChatListener.java",
        "ru/krosovok/krshubswap/client/command/SwapCommand.java",
        "ru/krosovok/krshubswap/client/KrshubswapClient.java",
        "ru/krosovok/krshubswap/KrsHubSwap.java",
    ]
    
    # Create directories
    print("\nCreating directories...")
    classes_dir.mkdir(parents=True, exist_ok=True)
    libs_dir.mkdir(parents=True, exist_ok=True)
    
    # Compile Java files
    print("\nCompiling Java files...")
    java_file_paths = [str(src_dir / f) for f in java_files]
    
    javac_cmd = [
        "javac",
        "-d", str(classes_dir),
        "-sourcepath", str(src_dir),
    ] + java_file_paths
    
    try:
        result = subprocess.run(javac_cmd, check=True, capture_output=True, text=True)
        if result.stdout:
            print(result.stdout)
        print("✓ Compilation successful")
    except FileNotFoundError:
        print("ERROR: javac not found!")
        print("Please install Java JDK from: https://www.oracle.com/java/technologies/downloads/")
        return 1
    except subprocess.CalledProcessError as e:
        print("ERROR: Compilation failed!")
        print(e.stderr)
        return 1
    
    # Create JAR file using Python zipfile (no jar command needed)
    print("\nCreating JAR file...")
    jar_file = libs_dir / "krshubswap-1.0.0.jar"
    
    try:
        with zipfile.ZipFile(jar_file, 'w', zipfile.ZIP_DEFLATED) as jar:
            # Walk through classes directory and add all .class files
            for root, dirs, files in os.walk(classes_dir):
                for file in files:
                    if file.endswith('.class'):
                        file_path = Path(root) / file
                        # Calculate the archive name (relative path from classes_dir)
                        arcname = file_path.relative_to(classes_dir)
                        jar.write(file_path, arcname)
                        print(f"  adding {arcname}")
        
        print("✓ JAR creation successful")
    except Exception as e:
        print("ERROR: JAR creation failed!")
        print(str(e))
        return 1
    
    # Success message
    print("\n" + "="*50)
    print("Build completed successfully!")
    print(f"JAR file: {jar_file}")
    print("="*50)
    
    return 0

if __name__ == "__main__":
    sys.exit(main())
