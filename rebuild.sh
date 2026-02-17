#!/bin/bash
# Rebuild the fork and install to Maven Local.
# After this, the IDE picks up changes automatically.
# For Bazel: run ./build/jpsModelToBazel.cmd from the monorepo root.

set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR"

echo "Building json-schema-validator fork..."
mvn install -DskipTests -q

echo "Done. Artifact installed to ~/.m2/repository/com/networknt/json-schema-validator/3.0.0-SNAPSHOT/"
echo ""
echo "Next steps:"
echo "  IDE (JPS):  changes are picked up automatically"
echo "  Bazel:      cd <monorepo> && ./build/jpsModelToBazel.cmd"
