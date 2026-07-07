#!/usr/bin/env python3
"""
Build script for krsHubSwap Fabric Mod
Compiles Java files and creates JAR archive without using gradle
"""

import os
import sys
import subprocess
import zipfile
from pathlib import Path

def main():
    print("Building krsHubSwap Fabric Mod...")
    
    # Define paths
    src_dir = Path("src/main/java")
    resources_dir = Path("src/main/resources")
    build_dir = Path("build")
    classes_dir = build_dir / "classes"
    libs_dir = build_dir / "libs"
    
    # Java files to compile
    java_files = [
        "ru/krosovok/krshubswap/KrsHubSwap.java",
        "ru/krosovok/krshubswap/client/RangeConfig.java",
        "ru/krosovok/krshubswap/client/AliasConfig.java",
        "ru/krosovok/krshubswap/client/AnarchyRegistry.java",
        "ru/krosovok/krshubswap/client/TeleportManager.java",
        "ru/krosovok/krshubswap/client/SwapCommand.java",
        "ru/krosovok/krshubswap/client/KrshubswapClient.java",
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
        if result.stderr and "error" in result.stderr.lower():
            print("Compilation errors:")
            print(result.stderr)
            return 1
        print("✓ Compilation successful")
    except FileNotFoundError:
        print("ERROR: javac not found!")
        print("Please install Java JDK from: https://www.oracle.com/java/technologies/downloads/")
        return 1
    except subprocess.CalledProcessError as e:
        print("ERROR: Compilation failed!")
        print(e.stderr)
        return 1
    
    # Copy resources
    print("\nCopying resources...")
    if resources_dir.exists():
        for resource_file in resources_dir.rglob("*"):
            if resource_file.is_file():
                rel_path = resource_file.relative_to(resources_dir)
                dest_path = classes_dir / rel_path
                dest_path.parent.mkdir(parents=True, exist_ok=True)
                import shutil
                shutil.copy2(resource_file, dest_path)
                print(f"  copied {rel_path}")
    
    # Create JAR file using Python zipfile
    print("\nCreating JAR file...")
    jar_file = libs_dir / "krshubswap-1.0.0.jar"
    
    try:
        with zipfile.ZipFile(jar_file, 'w', zipfile.ZIP_DEFLATED) as jar:
            # Walk through classes directory and add all files
            for root, dirs, files in os.walk(classes_dir):
                for file in files:
                    file_path = Path(root) / file
                    # Calculate the archive name (relative path from classes_dir)
                    arcname = file_path.relative_to(classes_dir)
                    jar.write(file_path, arcname)
                    if file.endswith(('.class', '.json')):
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
    print("\nTo use this mod:")
    print("1. Copy the JAR to your Fabric mods folder")
    print("2. Make sure you have Fabric Loader installed")
    print("3. Make sure you have Fabric API mod installed")
    
    return 0

if __name__ == "__main__":
    sys.exit(main())
