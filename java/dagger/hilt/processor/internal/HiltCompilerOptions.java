/*
 * Copyright (C) 2020 The Dagger Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dagger.hilt.processor.internal;

import com.google.common.base.Ascii;
import com.google.common.collect.ImmutableSet;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;

/** Hilt annotation processor options. */
// TODO(danysantiago): Consider consolidating with Dagger compiler options logic.
public final class HiltCompilerOptions {

  /**
   * Returns {@code true} if the superclass validation is disabled for
   * {@link dagger.hilt.android.AndroidEntryPoint}-annotated classes.
   *
   * This flag is for internal use only! The superclass validation checks that the super class is a
   * generated {@code Hilt_} class. This flag is disabled by the Hilt Gradle plugin to enable
   * bytecode transformation to change the superclass.
   */
  public static boolean isAndroidSuperclassValidationDisabled(
      TypeElement element, ProcessingEnvironment env) {
    BooleanOption option = BooleanOption.DISABLE_ANDROID_SUPERCLASS_VALIDATION;
    return option.get(env);
  }

  /**
   * Returns {@code true} if cross-compilation root validation is disabled.
   *
   * <p>This flag should rarely be needed, but may be used for legacy/migration purposes if
   * tests require the use of {@link dagger.hilt.android.HiltAndroidApp} rather than
   * {@link dagger.hilt.android.testing.HiltAndroidTest}.
   *
   * <p>Note that Hilt still does validation within a single compilation unit. In particular,
   * a compilation unit that contains a {@code HiltAndroidApp} usage cannot have other
   * {@code HiltAndroidApp} or {@code HiltAndroidTest} usages in the same compilation unit.
   */
  public static boolean isCrossCompilationRootValidationDisabled(
      ImmutableSet<TypeElement> rootElements, ProcessingEnvironment env) {
    BooleanOption option = BooleanOption.DISABLE_CROSS_COMPILATION_ROOT_VALIDATION;
    return option.get(env);
  }

  /** Returns {@code true} if the check for {@link dagger.hilt.InstallIn} is disabled. */
  public static boolean isModuleInstallInCheckDisabled(ProcessingEnvironment env) {
    return BooleanOption.DISABLE_MODULES_HAVE_INSTALL_IN_CHECK.get(env);
  }

  /**
   * Returns {@code true} of unit tests should try to share generated components, rather than using
   * separate generated components per Hilt test root.
   *
   * <p>Tests that provide their own test bindings (e.g. using {@link
   * dagger.hilt.android.testing.BindValue} or a test {@link dagger.Module}) cannot use the shared
   * component. In these cases, a component will be generated for the test.
   */
  public static boolean isSharedTestComponentsEnabled(ProcessingEnvironment env) {
    return BooleanOption.SHARE_TEST_COMPONENTS.get(env);
  }

  /**
   * Returns {@code true} if the aggregating processor is enabled (default is {@code true}).
   *
   * <p>Note:This is for internal use only!
   */
  public static boolean useAggregatingRootProcessor(ProcessingEnvironment env) {
    return BooleanOption.USE_AGGREGATING_ROOT_PROCESSOR.get(env);
  }

  /** Processor options which can have true or false values. */
  private enum BooleanOption {
    /** Do not use! This is for internal use only. */
    DISABLE_ANDROID_SUPERCLASS_VALIDATION(
        "android.internal.disableAndroidSuperclassValidation", false),

    /** Do not use! This is for internal use only. */
    USE_AGGREGATING_ROOT_PROCESSOR("internal.useAggregatingRootProcessor", true),

    DISABLE_CROSS_COMPILATION_ROOT_VALIDATION("disableCrossCompilationRootValidation", false),

    DISABLE_MODULES_HAVE_INSTALL_IN_CHECK("disableModulesHaveInstallInCheck", false),

    SHARE_TEST_COMPONENTS(
        "shareTestComponents", true);

    private final String name;
    private final boolean defaultValue;

    BooleanOption(String name, boolean defaultValue) {
      this.name = name;
      this.defaultValue = defaultValue;
    }

    boolean get(ProcessingEnvironment env) {
      String value = env.getOptions().get(getQualifiedName());
      if (value == null) {
        return defaultValue;
      }

      // Using Boolean.parseBoolean will turn any non-"true" value into false. Strictly verify the
      // inputs to reduce user errors.
      String lowercaseValue = Ascii.toLowerCase(value);
      switch (lowercaseValue) {
        case "true":
          return true;
        case "false":
          return false;
        default:
          throw new IllegalStateException(
              "Expected a value of true/false for the flag \""
                  + name
                  + "\". Got instead: "
                  + value);
      }
    }

    String getQualifiedName() {
      return "dagger.hilt." + name;
    }
  }

  private static final ImmutableSet<String> DEPRECATED_OPTIONS = ImmutableSet.of(
      "dagger.hilt.android.useFragmentGetContextFix");

  public static void checkWrongAndDeprecatedOptions(ProcessingEnvironment env) {
    Set<String> knownOptions = getProcessorOptions();
    for (String option : env.getOptions().keySet()) {
      if (knownOptions.contains(option)) {
        continue;
      }

      if (DEPRECATED_OPTIONS.contains(option)) {
        env.getMessager().printMessage(
            Kind.ERROR,
            "The compiler option " + option + " is deprecated and no longer does anything. "
            + "Please do not set this option.");
        continue;
      }

      if (option.startsWith("dagger.hilt.")) {
        env.getMessager().printMessage(
            Kind.ERROR,
            "The compiler option " + option + " is not a recognized Hilt option. Is there a typo?");
      }
    }
  }

  public static Set<String> getProcessorOptions() {
    return Arrays.stream(BooleanOption.values())
        .map(BooleanOption::getQualifiedName)
        .collect(Collectors.toSet());
  }
}
