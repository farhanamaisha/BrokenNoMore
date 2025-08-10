@echo off
echo 🔧 Compiling Java files...

REM Create output folder if it doesn't exist
if not exist out mkdir out

REM Compile all Java source files from src folder with libraries from lib folder
javac -cp "lib/*" -d out src\*.java

IF %ERRORLEVEL% NEQ 0 (
    echo ❌ Compile error. Check your code!
    pause
    exit /b
)

echo 📦 Creating BrokenNoMore.jar...

REM Package compiled classes AND resources folder into jar
jar cfm BrokenNoMore.jar manifest.txt -C out . -C resources .

echo ✅ Done! Your JAR is ready: BrokenNoMore.jar
pause
