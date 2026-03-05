# Stop locally running Eureka, Config Server, microservices, and frontend (ports 8761, 8888, 8081-8087, 3000).
# Does not stop Docker infrastructure (postgres, zookeeper, kafka, flyway).
# Usage: .\scripts\stop-local.ps1

$ErrorActionPreference = "SilentlyContinue"
$ports = @(8761, 8888, 8081, 8082, 8083, 8084, 8085, 8086, 8087, 3000)
$pidsToStop = @{}

foreach ($port in $ports) {
    $found = $false
    if (Get-Command Get-NetTCPConnection -ErrorAction SilentlyContinue) {
        $conn = Get-NetTCPConnection -LocalPort $port -ErrorAction SilentlyContinue | Select-Object -First 1
        if ($conn) { $pidsToStop[$conn.OwningProcess] = $port; $found = $true }
    }
    if (-not $found) {
        $line = netstat -ano | Select-String ":\s*$port\s+" | Select-Object -First 1
        if ($line -match "\s+(\d+)\s*$") { $pidsToStop[[int]$matches[1]] = $port }
    }
}

foreach ($pid in $pidsToStop.Keys) {
    $port = $pidsToStop[$pid]
    $proc = Get-Process -Id $pid -ErrorAction SilentlyContinue
    $name = if ($proc) { $proc.ProcessName } else { "PID $pid" }
    Write-Host "Stopping port $port ($name)" -ForegroundColor Yellow
    Stop-Process -Id $pid -Force -ErrorAction SilentlyContinue
}
Write-Host "Done. Local microservices and frontend stopped." -ForegroundColor Green
Write-Host "To restart: .\scripts\run-local.ps1" -ForegroundColor Cyan
