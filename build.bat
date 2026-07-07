@echo off
setlocal enabledelayedexpansion

echo Building krsHubSwap...

REM Find Java path
for /f "tokens=*" %%i in ('where java 2^>nul') do set "JAVA_PATH=%%i"

if "!JAVA_PATH!"=="" (
    REM Try common Java paths
    if exist "C:\Program Files\Java\jdk*\bin\java.exe" (
        for /d %%d in ("C:\Program Files\Java\jdk*") do set "JAVA_HOME=%%d"
    ) else if exist "C:\Program Files (x86)\Java\jdk*\bin\java.exe" (
        for /d %%d in ("C:\Program Files (x86)\Java\jdk*") do set "JAVA_HOME=%%d"
    )
)

if not defined JAVA_HOME (
    if not "!JAVA_PATH!"=="" (
        for %%i in (!JAVA_PATH!) do set "JAVA_HOME=%%~dpi.."
    )
)

echo Java path: !JAVA_HOME!

if not defined JAVA_HOME (
    echo.
    echo ERROR: Java not found!
    echo Please install Java from: https://www.java.com/download
    echo.
    pause
    exit /b 1
)

set "JAR_CMD=!JAVA_HOME!\bin\jar.exe"
set "JAVAC_CMD=!JAVA_HOME!\bin\javac.exe"

REM Create directories
if not exist build mkdir build
if not exist build\classes mkdir build\classes
if not exist build\libs mkdir build\libs

REM Compile Java files
echo.
echo Compiling Java files...
!JAVAC_CMD! -d build\classes -sourcepath src\main\java ^
  src\main\java\ru\krosovok\krshubswap\client\manager\RangeConfig.java ^
  src\main\java\ru\krosovok\krshubswap\client\manager\AliasConfig.java ^
  src\main\java\ru\krosovok\krshubswap\client\manager\TeleportManager.java ^
  src\main\java\ru\krosovok\krshubswap\client\listener\ChatListener.java ^
  src\main\java\ru\krosovok\krshubswap\client\command\SwapCommand.java ^
  src\main\java\ru\krosovok\krshubswap\client\KrshubswapClient.java ^
  src\main\java\ru\krosovok\krshubswap\KrsHubSwap.java

if errorlevel 1 (
    echo.
    echo ERROR: Compilation failed!
    pause
    exit /b 1
)

REM Create JAR
echo.
echo Creating JAR file...
pushd build\classes
!JAR_CMD! cvf ..\libs\krshubswap-1.0.0.jar ru\
popd

if errorlevel 1 (
    echo.
    echo ERROR: JAR creation failed!
    pause
    exit /b 1
)

echo.
echo ========================================
echo Build completed successfully!
echo JAR file: build\libs\krshubswap-1.0.0.jar
echo ========================================
pause
