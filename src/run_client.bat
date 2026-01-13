@echo off
cd /d %~dp0

echo 📦 Compiling PhishingDetectorClient.java...
javac -cp ".;json-20210307.jar" PhishingDetectorClient.java

if errorlevel 1 (
    echo ❌ Compilation failed!
    pause
    exit /b
)

echo 🚀 Launching Phishing Detector Client...
java -cp ".;json-20210307.jar" PhishingDetectorClient

pause
