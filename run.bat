@echo off
echo Compiling Algorithm Visualizer...
javac -encoding UTF-8 -cp "lib/*" --release 8 -d out src\Main.java src\ui\*.java src\algorithms\*.java src\db\*.java
if %errorlevel% neq 0 (
    echo COMPILATION FAILED!
    pause
    exit /b 1
)
echo Compilation successful! Launching app...
java -cp "out;lib/*" Main
