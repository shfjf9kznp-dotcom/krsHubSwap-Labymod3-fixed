@echo off
setlocal enabledelayedexpansion

echo Building krsHubSwap...

REM Create directories
if not exist build mkdir build
if not exist build\classes mkdir build\classes
if not exist build\libs mkdir build\libs

REM Compile Java files
echo Compiling Java files...
javac -d build\classes -sourcepath src\main\java ^
  src\main\java\ru\krosovok\krshubswap\client\manager\RangeConfig.java ^
  src\main\java\ru\krosovok\krshubswap\client\manager\AliasConfig.java ^
  src\main\java\ru\krosovok\krshubswap\client\manager\TeleportManager.java ^
  src\main\java\ru\krosovok\krshubswap\client\listener\ChatListener.java ^
  src\main\java\ru\krosovok\krshubswap\client\command\SwapCommand.java ^
  src\main\java\ru\krosovok\krshubswap\client\KrshubswapClient.java ^
  src\main\java\ru\krosovok\krshubswap\KrsHubSwap.java

if errorlevel 1 (
    echo Compilation failed!
    pause
    exit /b 1
)

REM Create JAR
echo Creating JAR file...
pushd build\classes
jar cvf ..\libs\krshubswap-1.0.0.jar ru\
popd

if errorlevel 1 (
    echo JAR creation failed!
    pause
    exit /b 1
)

echo.
echo ========================================
echo Build completed successfully!
echo JAR file: build\libs\krshubswap-1.0.0.jar
echo ========================================
pause
