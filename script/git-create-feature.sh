#!/bin/bash

# git-create-feature.sh
# Creates a new feature branch following best practices

set -e # Exit on error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_info() {
  echo -e "${BLUE}ℹ${NC} $1"
}

print_success() {
  echo -e "${GREEN}✓${NC} $1"
}

print_warning() {
  echo -e "${YELLOW}⚠${NC} $1"
}

print_error() {
  echo -e "${RED}✗${NC} $1"
}

# Check if we're in a git repository
if ! git rev-parse --git-dir >/dev/null 2>&1; then
  print_error "Not in a git repository!"
  exit 1
fi

# Get feature name from argument
if [ -z "$1" ]; then
  print_error "Usage: $0 <feature-name>"
  echo ""
  echo "Examples:"
  echo "  $0 user-authentication"
  echo "  $0 todo-crud-api"
  echo "  $0 firebase-auth-integration"
  exit 1
fi

FEATURE_NAME=$1
BRANCH_NAME="feature/$FEATURE_NAME"

# Check if branch already exists
if git show-ref --verify --quiet refs/heads/$BRANCH_NAME; then
  print_error "Branch '$BRANCH_NAME' already exists!"
  exit 1
fi

# Get current branch
CURRENT_BRANCH=$(git rev-parse --abbrev-ref HEAD)
print_info "Current branch: $CURRENT_BRANCH"

# Check if there are uncommitted changes
if ! git diff-index --quiet HEAD --; then
  print_warning "You have uncommitted changes!"
  read -p "Do you want to stash them? (y/n) " -n 1 -r
  echo
  if [[ $REPLY =~ ^[Yy]$ ]]; then
    git stash push -m "Auto-stash before creating feature/$FEATURE_NAME"
    print_success "Changes stashed"
  else
    print_error "Please commit or stash your changes first"
    exit 1
  fi
fi

# Make sure we're up to date with dev
print_info "Switching to dev branch..."
if ! git checkout dev 2>/dev/null; then
  print_warning "Dev branch doesn't exist. Creating it from main..."
  git checkout -b dev main
fi

print_info "Pulling latest changes from dev..."
git pull origin dev || print_warning "Could not pull from origin (might not exist yet)"

# Create and switch to new feature branch
print_info "Creating feature branch: $BRANCH_NAME"
git checkout -b $BRANCH_NAME

print_success "Successfully created and switched to branch: $BRANCH_NAME"
echo ""
print_info "Next steps:"
echo "  1. Make your changes"
echo "  2. git add <files>"
echo "  3. git commit -m 'Your commit message'"
echo "  4. git push -u origin $BRANCH_NAME"
echo ""
print_info "When done:"
echo "  - Create a Pull Request to merge into 'dev'"
echo "  - After review, merge and delete the feature branch"
