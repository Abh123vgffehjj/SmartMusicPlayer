@echo off
REM ============================================================
REM  Smart Music Player — compile & run (Windows)
REM  Run this file from inside the SmartMusicPlayer\ folder
REM ============================================================

echo Checking folder layout...
if not exist "src\Main.java" (
    echo.
    echo ERROR: src\Main.java not found.
    echo Make sure you are running this script from INSIDE the
    echo SmartMusicPlayer\ folder, e.g.:
    echo   cd C:\...\SmartMusicPlayer
    echo   compile.bat
    pause
    exit /b 1
)

echo Creating output folder...
if not exist out mkdir out

echo Compiling...
javac -d out -sourcepath src ^
    src\Main.java ^
    src\model\Song.java ^
    src\model\Playlist.java ^
    src\model\PlaylistFactory.java ^
    src\model\RecommendationStrategy.java ^
    src\model\Observer.java ^
    src\model\MusicEngine.java ^
    src\model\MusicFacade.java ^
    src\controller\MusicController.java ^
    src\util\FileManager.java ^
    src\view\MusicPlayerView.java

if %errorlevel% neq 0 (
    echo.
    echo Compilation FAILED. See errors above.
    pause
    exit /b 1
)

echo.
echo Compilation SUCCESS!
echo Launching Smart Music Player...
echo.
java -cp out Main
pause
