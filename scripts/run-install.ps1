# Common launcher: choose Docker or Podman, then Clean install / Reinstall (all or single service).
# Run from project root: .\scripts\run-install.ps1

$ErrorActionPreference = "Stop"
$ScriptsDir = $PSScriptRoot   # scripts folder
$ProjectRoot = Split-Path -Parent $ScriptsDir

function Show-Menu($title, $options) {
    Write-Host ""
    Write-Host "=== $title ===" -ForegroundColor Cyan
    $i = 1
    foreach ($o in $options) {
        Write-Host "  $i) $($o.Label)"
        $i++
    }
    Write-Host "  0) Exit"
    $choice = Read-Host "Select (0-$($options.Count))"
    $idx = [int]$choice
    if ($idx -eq 0) { exit 0 }
    if ($idx -lt 1 -or $idx -gt $options.Count) {
        Write-Host "Invalid option." -ForegroundColor Red
        exit 1
    }
    return $options[$idx - 1]
}

# Ensure we run from project root
Set-Location $ProjectRoot

# --- Step 1: Docker or Podman ---
$runtimeOptions = @(
    @{ Label = "Docker";  Dir = "docker" }
    @{ Label = "Podman"; Dir = "podman" }
)
$runtime = Show-Menu "Select container runtime" $runtimeOptions
$runtimeDir = Join-Path $ScriptsDir $runtime.Dir
if (-not (Test-Path $runtimeDir)) {
    Write-Host "Scripts folder not found: $runtimeDir" -ForegroundColor Red
    exit 1
}

# --- Step 2: Clean install all / Reinstall all / Single service ---
# Docker: install-all-services.ps1 = reinstall; Podman: reinstall-all-services.ps1
$reinstallScript = if ($runtime.Dir -eq "docker") { "install-all-services.ps1" } else { "reinstall-all-services.ps1" }
$modeOptions = @(
    @{ Label = "Clean install all services (mvn clean + build all + up)"; Script = "clean-install-all-services.ps1" }
    @{ Label = "Reinstall all services (build all + up, no mvn clean)";     Script = $reinstallScript }
    @{ Label = "Clean install single service";                             Script = "SINGLE" }
)

$mode = Show-Menu "Select install mode" $modeOptions

if ($mode.Script -ne "SINGLE") {
    $scriptPath = Join-Path $runtimeDir $mode.Script
    if (-not (Test-Path $scriptPath)) {
        Write-Host "Script not found: $scriptPath" -ForegroundColor Red
        exit 1
    }
    Write-Host ""
    Write-Host "Running: $scriptPath" -ForegroundColor Green
    & $scriptPath
    exit $LASTEXITCODE
}

# --- Step 3: Single service - list available clean-install-*.ps1 (exclude *-all-*) ---
$singleScripts = Get-ChildItem -Path $runtimeDir -Filter "clean-install-*.ps1" -File |
    Where-Object { $_.Name -notmatch "all-services" } |
    Sort-Object Name

if ($singleScripts.Count -eq 0) {
    Write-Host "No single-service scripts found in $runtimeDir" -ForegroundColor Red
    exit 1
}

$serviceOptions = @()
foreach ($s in $singleScripts) {
    $name = $s.BaseName -replace "^clean-install-", "" -replace "-", " "
    $serviceOptions += @{ Label = $name; Script = $s.Name }
}
$service = Show-Menu "Select service to clean install" $serviceOptions
$scriptPath = Join-Path $runtimeDir $service.Script

Write-Host ""
Write-Host "Running: $scriptPath" -ForegroundColor Green
& $scriptPath
exit $LASTEXITCODE
