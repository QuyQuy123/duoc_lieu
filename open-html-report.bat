@echo off
echo ========================================
echo   Mo HTML Report
echo ========================================
echo.

set REPORT_DIR=test-output
set REPORT_FILE=%REPORT_DIR%\Login Test Suite\Login Selenium Tests.html

if exist "%REPORT_FILE%" (
    echo Tim thay bao cao tai: %REPORT_FILE%
    echo Dang mo bao cao trong trinh duyet...
    start "" "%REPORT_FILE%"
    echo.
    echo Bao cao da duoc mo!
) else (
    echo.
    echo [ERROR] Khong tim thay bao cao!
    echo.
    echo Dang tim kiem trong test-output...
    if exist "%REPORT_DIR%" (
        echo Tim thay thu muc test-output:
        dir /b "%REPORT_DIR%"
    ) else (
        echo Khong tim thay thu muc test-output.
        echo Vui long chay test truoc.
    )
)

echo.
pause

