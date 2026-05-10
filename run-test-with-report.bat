@echo off
echo ========================================
echo   Chay test va tao HTML report
echo ========================================
echo.
echo Dang chay test bang Maven...
echo.

mvn clean test

echo.
echo ========================================
echo   Kiem tra bao cao
echo ========================================
echo.

if exist "target\test-output\index.html" (
    echo [SUCCESS] Tim thay bao cao HTML!
    echo.
    echo Vi tri: target\test-output\index.html
    echo.
    echo Ban co muon mo bao cao ngay khong? (Y/N)
    set /p choice=
    if /i "%choice%"=="Y" (
        start "" "target\test-output\index.html"
        echo.
        echo Bao cao da duoc mo trong trinh duyet!
    )
) else (
    echo [ERROR] Khong tim thay bao cao HTML!
    echo.
    echo Co the test chua chay xong hoac co loi.
    echo Vui long kiem tra lai.
)

echo.
pause

