#!/bin/bash
# Warung Madura - Build & Run Script
# Requirements: Java JDK 11+ (no external libraries needed)

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

if ! command -v javac &> /dev/null; then
    echo "ERROR: javac tidak ditemukan."
    echo "Install JDK: sudo apt install openjdk-21-jdk"
    exit 1
fi

echo "[1/2] Compiling..."
mkdir -p out
find src -name "*.java" > sources.txt
javac -d out @sources.txt
if [ $? -ne 0 ]; then
    echo "Kompilasi gagal!"
    exit 1
fi

echo "[2/2] Menjalankan Warung Madura..."
java -cp out warungmadura.Main
