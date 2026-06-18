@echo off
echo.
echo ===================================================
echo  TimerTorch Build Script
echo ===================================================
echo.
echo Building Release APK...
call gradlew.bat assembleRelease
echo.
if exist app\build\outputs\apk\release\app-release-unsigned.apk (
    echo.
    echo ---------------------------------------------------
    echo  BUILD SUCCESSFUL!
    echo ---------------------------------------------------
    echo  Release APK generated at:
    echo  app\build\outputs\apk\release\app-release-unsigned.apk
    echo.
) else (
    echo.
    echo ---------------------------------------------------
    echo  BUILD FAILED!
    echo ---------------------------------------------------
    echo.
)
pause
