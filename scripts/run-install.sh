#!/usr/bin/env bash
# Common launcher: choose Docker or Podman, then Clean install / Reinstall (all or single service).
# Run from project root: ./scripts/run-install.sh
# On Windows (Git Bash): use PowerShell script instead, or run: pwsh -File scripts/run-install.ps1

set -e
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
cd "$PROJECT_ROOT"

RUN_DOCKER="docker"
RUN_PODMAN="podman"

run_script() {
    local path="$1"
    if [[ ! -f "$path" ]]; then
        echo "Script not found: $path" >&2
        exit 1
    fi
    if [[ "$path" == *.ps1 ]]; then
        if command -v pwsh &>/dev/null; then
            pwsh -NoProfile -File "$path"
        elif command -v powershell.exe &>/dev/null; then
            powershell.exe -NoProfile -ExecutionPolicy Bypass -File "$path"
        else
            echo "PowerShell required to run $path. Install pwsh or run from Windows: .\\scripts\\run-install.ps1" >&2
            exit 1
        fi
    else
        bash "$path"
    fi
}

echo ""
echo "=== Select container runtime ==="
echo "  1) Docker"
echo "  2) Podman"
echo "  0) Exit"
read -r -p "Select (0-2): " choice
case "$choice" in
    0) exit 0 ;;
    1) RUNTIME="docker" ;;
    2) RUNTIME="podman" ;;
    *) echo "Invalid option." >&2; exit 1 ;;
esac

RUNTIME_DIR="$SCRIPT_DIR/$RUNTIME"
if [[ ! -d "$RUNTIME_DIR" ]]; then
    echo "Scripts folder not found: $RUNTIME_DIR" >&2
    exit 1
fi

echo ""
echo "=== Select install mode ==="
echo "  1) Clean install all services (mvn clean + build all + up)"
echo "  2) Reinstall all services (build all + up, no mvn clean)"
echo "  3) Clean install single service"
echo "  0) Exit"
read -r -p "Select (0-3): " mode
case "$mode" in
    0) exit 0 ;;
    1)
        run_script "$RUNTIME_DIR/clean-install-all-services.ps1"
        exit 0
        ;;
    2)
        if [[ "$RUNTIME" == "docker" ]]; then
            run_script "$RUNTIME_DIR/install-all-services.ps1"
        else
            run_script "$RUNTIME_DIR/reinstall-all-services.ps1"
        fi
        exit 0
        ;;
    3) ;;
    *) echo "Invalid option." >&2; exit 1 ;;
esac

# Single service: list clean-install-*.ps1 (exclude *-all-*), sorted
SINGLE_SCRIPTS=()
for f in "$RUNTIME_DIR"/clean-install-*.ps1; do
    [[ -f "$f" ]] && [[ "$f" != *"all-services"* ]] && SINGLE_SCRIPTS+=("$f")
done
if [[ ${#SINGLE_SCRIPTS[@]} -gt 0 ]]; then
    SINGLE_SCRIPTS=($(printf '%s\n' "${SINGLE_SCRIPTS[@]}" | sort))
fi

if [[ ${#SINGLE_SCRIPTS[@]} -eq 0 ]]; then
    echo "No single-service scripts in $RUNTIME_DIR" >&2
    exit 1
fi

echo ""
echo "=== Select service to clean install ==="
i=1
for f in "${SINGLE_SCRIPTS[@]}"; do
    name="$(basename "$f" .ps1 | sed 's/^clean-install-//;s/-/ /g')"
    echo "  $i) $name"
    ((i++)) || true
done
echo "  0) Exit"
read -r -p "Select (0-$(( ${#SINGLE_SCRIPTS[@]} ))): " idx
if [[ "$idx" -eq 0 ]]; then
    exit 0
fi
if [[ "$idx" -lt 1 || "$idx" -gt ${#SINGLE_SCRIPTS[@]} ]]; then
    echo "Invalid option." >&2
    exit 1
fi
run_script "${SINGLE_SCRIPTS[$((idx-1))]}"
