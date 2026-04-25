@echo off
cd /d "%~dp0"
echo [1/2] Compiling...
if not exist out mkdir out
dir /s /b src\*.java > sources.txt
javac -d out @sources.txt
if errorlevel 1 (
    echo Kompilasi gagal!
    pause
    exit /b 1
)
echo [2/2] Menjalankan Warung Madura...
java -cp out warungmadura.Main
pause
