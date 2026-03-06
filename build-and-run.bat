@echo off
REM ─────────────────────────────────────────────────────
REM  Premier League App - Build & Run Script (Windows)
REM ─────────────────────────────────────────────────────

echo ==> Cleaning old build...
if exist out  rmdir /s /q out
if exist dist rmdir /s /q dist
mkdir out
mkdir dist

echo ==> Compiling...
javac -d out src\main\java\com\pl\Main.java src\main\java\com\pl\model\Team.java src\main\java\com\pl\model\Fixture.java src\main\java\com\pl\model\MatchResult.java src\main\java\com\pl\service\LeagueService.java src\main\java\com\pl\service\DataStore.java src\main\java\com\pl\api\ApiHandler.java src\main\java\com\pl\api\StaticHandler.java
if errorlevel 1 (
    echo [ERROR] Compilation failed.
    exit /b 1
)
echo     Compiled OK.

echo ==> Copying static resources into out\static ...
xcopy /E /I /Y src\main\resources\static out\static > nul

echo ==> Creating JAR (all classes + static folder)...
jar --create --file dist\premier-league.jar --main-class com.pl.Main -C out .
if errorlevel 1 (
    echo [ERROR] JAR creation failed.
    exit /b 1
)

echo.
echo  BUILD COMPLETE:  dist\premier-league.jar
echo.
echo ==> Starting server on http://localhost:8080 ...
java -jar dist\premier-league.jar
