# Stop locally running Eureka, Config Server, microservices, frontend, and close their command windows.
# Kills processes on ports 8761, 8888, 8081-8087, 3000 (with process tree) then closes PowerShell runner windows.
# Does not stop Docker infrastructure (postgres, zookeeper, kafka, flyway).
# Usage: .\scripts\stop-local.ps1

$ErrorActionPreference = "SilentlyContinue"
$ports = @(8761, 8888, 8081, 8082, 8083, 8084, 8085, 8086, 8087, 3000)
$pidsToStop = @{}

# 1) Find processes listening on our ports (server processes)
foreach ($port in $ports) {
    $found = $false
    if (Get-Command Get-NetTCPConnection -ErrorAction SilentlyContinue) {
        $conn = Get-NetTCPConnection -LocalPort $port -State Listen -ErrorAction SilentlyContinue | Select-Object -First 1
        if ($conn) { $pidsToStop[$conn.OwningProcess] = $port; $found = $true }
    }
    if (-not $found) {
        $lines = netstat -ano | Select-String ":\s*$port\s+.*LISTENING"
        if ($lines) {
            $line = $lines | Select-Object -First 1
            if ($line -match "\s+(\d+)\s*$") { $pidsToStop[[int]$matches[1]] = $port }
        }
    }
}

# Kill process tree for each PID (stops the actual service and any child processes)
foreach ($pid in $pidsToStop.Keys) {
    $port = $pidsToStop[$pid]
    $proc = Get-Process -Id $pid -ErrorAction SilentlyContinue
    $name = if ($proc) { $proc.ProcessName } else { "PID $pid" }
    Write-Host "Stopping port $port ($name, PID $pid)..." -ForegroundColor Yellow
    & taskkill /PID $pid /F /T 2>$null
    if ($LASTEXITCODE -ne 0) { Stop-Process -Id $pid -Force -ErrorAction SilentlyContinue }
}

# 2) Close PowerShell windows that were started by run-local (mvn spring-boot:run or npm start)
$runnerPids = @()
try {
    $procs = Get-CimInstance Win32_Process -Filter "Name = 'powershell.exe'" -Property ProcessId, CommandLine -ErrorAction SilentlyContinue
    foreach ($p in $procs) {
        $cmd = $p.CommandLine
        if ($cmd -and ($cmd -match "spring-boot:run" -or ($cmd -match "npm start" -and $cmd -match "e-commerce-fe"))) {
            $runnerPids += $p.ProcessId
        }
    }
} catch { }

foreach ($pid in $runnerPids) {
    $proc = Get-Process -Id $pid -ErrorAction SilentlyContinue
    if ($proc) {
        Write-Host "Closing runner window (PID $pid)..." -ForegroundColor Yellow
        & taskkill /PID $pid /F /T 2>$null
        if ($LASTEXITCODE -ne 0) { Stop-Process -Id $pid -Force -ErrorAction SilentlyContinue }
    }
}

Write-Host "Done. Local services and runner windows stopped." -ForegroundColor Green
Write-Host "To restart: .\scripts\run-local.ps1" -ForegroundColor Cyan
