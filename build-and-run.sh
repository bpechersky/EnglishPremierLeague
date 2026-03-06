#!/usr/bin/env bash
# ─────────────────────────────────────────────────────
#  Premier League App – Build & Run Script (Unix/macOS)
# ─────────────────────────────────────────────────────
set -e

echo "==> Cleaning old build..."
rm -rf out dist
mkdir -p out dist

echo "==> Compiling Java sources..."
find src/main/java -name "*.java" > sources.txt
javac -d out @sources.txt
rm sources.txt
echo "    Compiled OK."

echo "==> Copying static resources into out/static..."
cp -r src/main/resources/static out/static

echo "==> Creating JAR (classes + static folder)..."
jar --create --file dist/premier-league.jar --main-class com.pl.Main -C out .

echo ""
echo "  BUILD COMPLETE: dist/premier-league.jar"
echo ""
echo "==> Starting server on http://localhost:8080 ..."
java -jar dist/premier-league.jar
