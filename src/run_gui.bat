@echo off
title 🖥️ Phishing Detector GUI Launcher

echo ≡📦 Compiling PhishingDetectorGUI.java...
javac -cp .;json-20210307.jar PhishingDetectorGUI.java

if %errorlevel% neq 0 (
    echo ❌ Compilation failed!
    pause
    exit /b
)

echo ≡🚀 Launching GUI...
java -cp .;json-20210307.jar PhishingDetectorGUI

pause
