# Copyright (C) 2017 The Dagger Authors.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")

#############################
# Load nested repository
#############################

# Declare the nested workspace so that the top-level workspace doesn't try to
# traverse it when calling `bazel build //...`
local_repository(
    name = "examples_bazel",
    path = "examples/bazel",
)

#############################
# Load Bazel Skylib rules
#############################

BAZEL_SKYLIB_VERSION = "1.5.0"

BAZEL_SKYLIB_SHA = "cd55a062e763b9349921f0f5db8c3933288dc8ba4f76dd9416aac68acee3cb94"

http_archive(
    name = "bazel_skylib",
    sha256 = BAZEL_SKYLIB_SHA,
    urls = [
        "https://mirror.bazel.build/github.com/bazelbuild/bazel-skylib/releases/download/%s/bazel-skylib-%s.tar.gz" % (BAZEL_SKYLIB_VERSION, BAZEL_SKYLIB_VERSION),
        "https://github.com/bazelbuild/bazel-skylib/releases/download/%s/bazel-skylib-%s.tar.gz" % (BAZEL_SKYLIB_VERSION, BAZEL_SKYLIB_VERSION),
    ],
)

load("@bazel_skylib//:workspace.bzl", "bazel_skylib_workspace")

bazel_skylib_workspace()

####################################################
# Load Protobuf repository (needed by bazel-common)
####################################################

http_archive(
    name = "rules_proto",
    # output from `sha256sum` on the downloaded tar.gz file
    sha256 = "66bfdf8782796239d3875d37e7de19b1d94301e8972b3cbd2446b332429b4df1",
    strip_prefix = "rules_proto-4.0.0",
    urls = [
        "https://mirror.bazel.build/github.com/bazelbuild/rules_proto/archive/refs/tags/4.0.0.tar.gz",
        "https://github.com/bazelbuild/rules_proto/archive/refs/tags/4.0.0.tar.gz",
    ],
)

load("@rules_proto//proto:repositories.bzl", "rules_proto_dependencies", "rules_proto_toolchains")

rules_proto_dependencies()

rules_proto_toolchains()

#############################
# Load Bazel-Common repository
#############################

http_archive(
    name = "google_bazel_common",
    sha256 = "82a49fb27c01ad184db948747733159022f9464fc2e62da996fa700594d9ea42",
    strip_prefix = "bazel-common-2a6b6406e12208e02b2060df0631fb30919080f3",
    urls = ["https://github.com/google/bazel-common/archive/2a6b6406e12208e02b2060df0631fb30919080f3.zip"],
)

load("@google_bazel_common//:workspace_defs.bzl", "google_common_workspace_rules")

google_common_workspace_rules()

#############################
# Load Protobuf dependencies
#############################

# rules_python and zlib are required by protobuf.
# TODO(ronshapiro): Figure out if zlib is in fact necessary, or if proto can depend on the
# @bazel_tools library directly. See discussion in
# https://github.com/protocolbuffers/protobuf/pull/5389#issuecomment-481785716
# TODO(cpovirk): Should we eventually get rules_python from "Bazel Federation?"
# https://github.com/bazelbuild/rules_python#getting-started

http_archive(
    name = "rules_python",
    sha256 = "e5470e92a18aa51830db99a4d9c492cc613761d5bdb7131c04bd92b9834380f6",
    strip_prefix = "rules_python-4b84ad270387a7c439ebdccfd530e2339601ef27",
    urls = ["https://github.com/bazelbuild/rules_python/archive/4b84ad270387a7c439ebdccfd530e2339601ef27.tar.gz"],
)

http_archive(
    name = "zlib",
    build_file = "@com_google_protobuf//:third_party/zlib.BUILD",
    sha256 = "629380c90a77b964d896ed37163f5c3a34f6e6d897311f1df2a7016355c45eff",
    strip_prefix = "zlib-1.2.11",
    urls = ["https://github.com/madler/zlib/archive/v1.2.11.tar.gz"],
)

#############################
# Load Robolectric repository
#############################

ROBOLECTRIC_VERSION = "4.4"

http_archive(
    name = "robolectric",
    sha256 = "d4f2eb078a51f4e534ebf5e18b6cd4646d05eae9b362ac40b93831bdf46112c7",
    strip_prefix = "robolectric-bazel-%s" % ROBOLECTRIC_VERSION,
    urls = ["https://github.com/robolectric/robolectric-bazel/archive/%s.tar.gz" % ROBOLECTRIC_VERSION],
)

load("@robolectric//bazel:robolectric.bzl", "robolectric_repositories")

robolectric_repositories()

#############################
# Load Kotlin repository
#############################

RULES_KOTLIN_TAG = "v1.8"

RULES_KOTLIN_SHA = "01293740a16e474669aba5b5a1fe3d368de5832442f164e4fbfc566815a8bc3a"

http_archive(
    name = "io_bazel_rules_kotlin",
    sha256 = RULES_KOTLIN_SHA,
    urls = ["https://github.com/bazelbuild/rules_kotlin/releases/download/%s/rules_kotlin_release.tgz" % RULES_KOTLIN_TAG],
)

load("@io_bazel_rules_kotlin//kotlin:repositories.bzl", "kotlin_repositories", "kotlinc_version")

KOTLIN_VERSION = "1.9.20"

# Get from https://github.com/JetBrains/kotlin/releases/
KOTLINC_RELEASE_SHA = "15a8a2825b74ccf6c44e04e97672db802d2df75ce2fbb63ef0539bf3ae5006f0"

kotlin_repositories(
    compiler_release = kotlinc_version(
        release = KOTLIN_VERSION,
        sha256 = KOTLINC_RELEASE_SHA,
    ),
)

load("@io_bazel_rules_kotlin//kotlin:core.bzl", "kt_register_toolchains")

kt_register_toolchains()

#############################
# Load Maven dependencies
#############################

RULES_JVM_EXTERNAL_TAG = "4.5"

RULES_JVM_EXTERNAL_SHA = "b17d7388feb9bfa7f2fa09031b32707df529f26c91ab9e5d909eb1676badd9a6"

http_archive(
    name = "rules_jvm_external",
    sha256 = RULES_JVM_EXTERNAL_SHA,
    strip_prefix = "rules_jvm_external-%s" % RULES_JVM_EXTERNAL_TAG,
    url = "https://github.com/bazelbuild/rules_jvm_external/archive/%s.zip" % RULES_JVM_EXTERNAL_TAG,
)

load("@rules_jvm_external//:defs.bzl", "maven_install")

ANDROID_LINT_VERSION = "30.1.0"

AUTO_COMMON_VERSION = "1.2.1"

# NOTE(bcorso): Even though we set the version here, our Guava version in
#  processor code will use whatever version is built into JavaBuilder, which is
#  tied to the version of Bazel we're using.
GUAVA_VERSION = "33.0.0"

GRPC_VERSION = "1.2.0"

INCAP_VERSION = "0.2"

BYTE_BUDDY_VERSION = "1.9.10"

CHECKER_FRAMEWORK_VERSION = "2.5.3"

ERROR_PRONE_VERSION = "2.14.0"

KSP_VERSION = KOTLIN_VERSION + "-1.0.14"

maven_install(
    artifacts = [
        "androidx.annotation:annotation:1.1.0",
        "androidx.annotation:annotation-experimental:1.3.1",
        "androidx.appcompat:appcompat:1.3.1",
        "androidx.activity:activity:1.5.1",
        "androidx.fragment:fragment:1.5.1",
        "androidx.lifecycle:lifecycle-common:2.5.1",
        "androidx.lifecycle:lifecycle-viewmodel:2.5.1",
        "androidx.lifecycle:lifecycle-viewmodel-savedstate:2.5.1",
        "androidx.multidex:multidex:2.0.1",
        "androidx.navigation:navigation-common:2.5.1",
        "androidx.navigation:navigation-fragment:2.5.1",
        "androidx.navigation:navigation-runtime:2.5.1",
        "androidx.savedstate:savedstate:1.2.0",
        "androidx.test:monitor:1.4.0",
        "androidx.test:core:1.4.0",
        "androidx.test.ext:junit:1.1.3",
        "com.android.support:appcompat-v7:25.0.0",
        "com.android.support:support-annotations:25.0.0",
        "com.android.support:support-fragment:25.0.0",
        "com.android.tools.external.org-jetbrains:uast:%s" % ANDROID_LINT_VERSION,
        "com.android.tools.external.com-intellij:intellij-core:%s" % ANDROID_LINT_VERSION,
        "com.android.tools.external.com-intellij:kotlin-compiler:%s" % ANDROID_LINT_VERSION,
        "com.android.tools.lint:lint:%s" % ANDROID_LINT_VERSION,
        "com.android.tools.lint:lint-api:%s" % ANDROID_LINT_VERSION,
        "com.android.tools.lint:lint-checks:%s" % ANDROID_LINT_VERSION,
        "com.android.tools.lint:lint-tests:%s" % ANDROID_LINT_VERSION,
        "com.android.tools:testutils:%s" % ANDROID_LINT_VERSION,
        "com.google.auto:auto-common:%s" % AUTO_COMMON_VERSION,
        "com.google.auto.factory:auto-factory:1.0",
        "com.google.auto.service:auto-service:1.0",
        "com.google.auto.service:auto-service-annotations:1.0",
        "com.google.auto.value:auto-value:1.9",
        "com.google.auto.value:auto-value-annotations:1.9",
        "com.google.code.findbugs:jsr305:3.0.1",
        "com.google.devtools.ksp:symbol-processing:%s" % KSP_VERSION,
        "com.google.devtools.ksp:symbol-processing-api:%s" % KSP_VERSION,
        "com.google.errorprone:error_prone_annotation:%s" % ERROR_PRONE_VERSION,
        "com.google.errorprone:error_prone_annotations:%s" % ERROR_PRONE_VERSION,
        "com.google.errorprone:error_prone_check_api:%s" % ERROR_PRONE_VERSION,
        "com.google.googlejavaformat:google-java-format:1.5",
        "com.google.guava:guava:%s-jre" % GUAVA_VERSION,
        "com.google.guava:guava-testlib:%s-jre" % GUAVA_VERSION,
        "com.google.guava:failureaccess:1.0.1",
        "com.google.guava:guava-beta-checker:1.0",
        "com.google.protobuf:protobuf-java:3.7.0",
        "com.google.testing.compile:compile-testing:0.18",
        "com.google.truth:truth:1.4.0",
        "com.squareup:javapoet:1.13.0",
        "com.squareup:kotlinpoet:1.11.0",
        "io.github.java-diff-utils:java-diff-utils:4.11",
        "io.grpc:grpc-context:%s" % GRPC_VERSION,
        "io.grpc:grpc-core:%s" % GRPC_VERSION,
        "io.grpc:grpc-netty:%s" % GRPC_VERSION,
        "io.grpc:grpc-protobuf:%s" % GRPC_VERSION,
        "jakarta.inject:jakarta.inject-api:2.0.1",
        "javax.annotation:javax.annotation-api:1.3.2",
        "javax.inject:javax.inject:1",
        "javax.inject:javax.inject-tck:1",
        "junit:junit:4.13",
        "net.bytebuddy:byte-buddy:%s" % BYTE_BUDDY_VERSION,
        "net.bytebuddy:byte-buddy-agent:%s" % BYTE_BUDDY_VERSION,
        "net.ltgt.gradle.incap:incap:%s" % INCAP_VERSION,
        "net.ltgt.gradle.incap:incap-processor:%s" % INCAP_VERSION,
        "org.checkerframework:checker-compat-qual:%s" % CHECKER_FRAMEWORK_VERSION,
        "org.checkerframework:dataflow:%s" % CHECKER_FRAMEWORK_VERSION,
        "org.checkerframework:javacutil:%s" % CHECKER_FRAMEWORK_VERSION,
        "org.hamcrest:hamcrest-core:1.3",
        "org.jetbrains.kotlin:kotlin-annotation-processing-embeddable:%s" % KOTLIN_VERSION,
        "org.jetbrains.kotlin:kotlin-compiler-embeddable:%s" % KOTLIN_VERSION,
        "org.jetbrains.kotlin:kotlin-daemon-embeddable:%s" % KOTLIN_VERSION,
        "org.jetbrains.kotlin:kotlin-stdlib:%s" % KOTLIN_VERSION,
        "org.jetbrains.kotlinx:kotlinx-metadata-jvm:0.6.2",
        "org.jspecify:jspecify:0.3.0",
        "org.mockito:mockito-core:2.28.2",
        "org.objenesis:objenesis:1.0",
        "org.robolectric:robolectric:4.4",
        "org.robolectric:shadows-framework:4.4",  # For ActivityController
    ],
    repositories = [
        "https://repo1.maven.org/maven2",
        "https://maven.google.com",
    ],
)
