#!/usr/bin/env bash
# Bounded native harness runner; Gradle owns fresh-report and exact-fixture checks.
set -euo pipefail
if (( $# == 0 )); then printf 'Usage: %s :library:loader:version:runTask [Gradle flags]\n' "$0" >&2; exit 64; fi
task=$1
shift
case "$task" in
    :*:runClientGameTest) deadline=300; display=(xvfb-run --auto-servernum) ;;
    :*:runServer|:*:runGameTestServer|:*:runCctGameTest|:*:runStorageGameTest|:*:runPeripheralGameTest) deadline=900; display=() ;;
    *) printf 'Not a supported GameTest task: %s\n' "$task" >&2; exit 64 ;;
esac
root=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/../.." && pwd)
log_dir=${LOG_DIR:-${TMPDIR:?}/gametest-validation}
mkdir -p -- "$log_dir"
log=$(mktemp "$log_dir/${task//:/_}.XXXXXX.log")
printf 'Task=%s deadline=%ss log=%s\n' "$task" "$deadline" "$log"
set +e
timeout --foreground "${deadline}s" "${display[@]}" "$root/gradlew" -p "$root" "$task" "$@" --no-daemon --console=plain > "$log" 2>&1
status=$?
set -e
printf 'Exit=%s log=%s\n' "$status" "$log"
exit "$status"
