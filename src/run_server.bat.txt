@echo off
cd /d %~dp0

echo 📦 Compiling PhishingDetectorServer.java...
javac -cp ".;json-20210307.jar" PhishingDetectorServer.java

if errorlevel 1 (
    echo ❌ Compilation failed!
    pause
    exit /b
)

echo 🚀 Launching Phishing Detector Server...
java -cp ".;json-20210307.jar" PhishingDetectorServer

pause
