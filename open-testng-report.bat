@echo off
echo ========================================
echo   Mo bao cao TestNG Report
echo ========================================
echo.

set REPORT_DIR=target\test-output
set REPORT_FILE=%REPORT_DIR%\index.html

if exist "%REPORT_FILE%" (
    echo Tim thay bao cao tai: %REPORT_FILE%
    echo Dang mo bao cao trong trinh duyet...
    start "" "%REPORT_FILE%"
    echo.
    echo Bao cao da duoc mo trong trinh duyet!
) else (
    echo.
    echo [ERROR] Khong tim thay bao cao!
    echo.
    echo Bao cao chi duoc tao sau khi chay test.
    echo Vui long chay test truoc bang cach:
    echo   1. Trong IntelliJ: Click phai vao test class -^> Run
    echo   2. Hoac chay: mvn clean test
    echo.
    echo Sau do chay lai script nay.
)

echo.
pause

