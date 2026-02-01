#!/bin/bash
# RoleNoteAI Flywheel Team - Quick Sync and Status Check
# Usage: bash sync_status.sh

REPO_PATH="/home/ubuntu/RoleNoteAI"

echo "🔄 RoleNoteAI Flywheel Team - Sync & Status"
echo "============================================"

# Check if repo exists
if [ ! -d "$REPO_PATH" ]; then
    echo "⚠️  Repository not found. Cloning..."
    cd /home/ubuntu && gh repo clone creator35lwb-web/RoleNoteAI
fi

cd "$REPO_PATH"

# Pull latest
echo ""
echo "📥 Pulling latest changes..."
git pull origin main

# Show recent commits
echo ""
echo "📋 Recent Commits (last 5):"
git log --oneline -5

# Show open issues
echo ""
echo "📌 Open Issues:"
gh issue list --state open

# Show current branch and status
echo ""
echo "🌿 Current Branch:"
git branch --show-current

echo ""
echo "📊 Git Status:"
git status --short

echo ""
echo "✅ Sync complete! Ready for Flywheel Team work."
