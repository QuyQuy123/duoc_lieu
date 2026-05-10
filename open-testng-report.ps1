# Script PowerShell để mở TestNG Report
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Mở báo cáo TestNG Report" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$reportDir = "target\test-output"
$reportFile = "$reportDir\index.html"

if (Test-Path $reportFile) {
    Write-Host "Tìm thấy báo cáo tại: $reportFile" -ForegroundColor Green
    Write-Host "Đang mở báo cáo trong trình duyệt..." -ForegroundColor Yellow
    Start-Process $reportFile
    Write-Host ""
    Write-Host "Báo cáo đã được mở trong trình duyệt!" -ForegroundColor Green
} else {
    Write-Host ""
    Write-Host "[ERROR] Không tìm thấy báo cáo!" -ForegroundColor Red
    Write-Host ""
    Write-Host "Báo cáo chỉ được tạo sau khi chạy test." -ForegroundColor Yellow
    Write-Host "Vui lòng chạy test trước bằng cách:" -ForegroundColor Yellow
    Write-Host "  1. Trong IntelliJ: Click phải vào test class -> Run" -ForegroundColor White
    Write-Host "  2. Hoặc chạy: mvn clean test" -ForegroundColor White
    Write-Host ""
    Write-Host "Sau đó chạy lại script này." -ForegroundColor Yellow
}

Write-Host ""
Read-Host "Nhấn Enter để thoát"

