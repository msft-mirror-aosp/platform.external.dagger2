#!/bin/bash

set -ex

# These jobs should match .github/workflows/ci.yml. We can't run this script
# directly in Github since it's too slow for a single job.

# Build versions
readonly AGP_VERSION="9.0.0"

# Min test versions
readonly MIN_JAVA_VERSION="11"
readonly MIN_KOTLIN_VERSION="2.3.0"
readonly MIN_KSP_VERSION="2.3.6"

# Max test versions
readonly MAX_JAVA_VERSION="21"
readonly MAX_KOTLIN_VERSION="2.4.20"
readonly MAX_KSP_VERSION="2.3.12"

# Run local bazel build and tests
bazel build //java/dagger/...
bazel test --test_output=errors //...
(cd examples/bazel && bazel test --test_output=errors //...)

# Install and validate local maven artifacts
util/install-local-snapshot.sh
util/validate-artifacts.sh

# Run local gradle build and plugin tests
util/build-gradle.sh
AGP_VERSION="${AGP_VERSION}" ./java/dagger/hilt/android/plugin/gradlew \
  -p java/dagger/hilt/android/plugin \
  clean test --continue --no-daemon --stacktrace

# Run local gradle artifact tests (min & max versions)
util/run-local-gradle-tests.sh \
  "${MIN_JAVA_VERSION}" "${MIN_KOTLIN_VERSION}" "${MIN_KSP_VERSION}"
util/run-local-gradle-tests.sh \
  "${MAX_JAVA_VERSION}" "${MAX_KOTLIN_VERSION}" "${MAX_KSP_VERSION}"
util/run-local-gradle-android-tests.sh \
  "${AGP_VERSION}" "${MIN_JAVA_VERSION}" "${MIN_KOTLIN_VERSION}" "${MIN_KSP_VERSION}"
util/run-local-gradle-android-tests.sh \
  "${AGP_VERSION}" "${MAX_JAVA_VERSION}" "${MAX_KOTLIN_VERSION}" "${MAX_KSP_VERSION}"
