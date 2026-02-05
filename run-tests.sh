#!/bin/bash

# Simple Test Runner Shortcut
# Usage: ./run-tests.sh [all|unit|integration]

TYPE=${1:-all}

case $TYPE in
  unit)
    echo "Running Unit Tests..."
    ./mvnw clean test -Dtest="com.mini.project.financial_tracker.service.**,com.mini.project.financial_tracker.util.**,com.mini.project.financial_tracker.exception.**"
    ;;
  integration)
    echo "Running Integration Tests..."
    ./mvnw clean test -Dtest="com.mini.project.financial_tracker.integration.**"
    ;;
  all)
    echo "Running All Tests..."
    ./mvnw clean test
    ;;
  *)
    echo "Invalid option: $TYPE"
    echo "Usage: ./run-tests.sh [all|unit|integration]"
    exit 1
    ;;
esac
