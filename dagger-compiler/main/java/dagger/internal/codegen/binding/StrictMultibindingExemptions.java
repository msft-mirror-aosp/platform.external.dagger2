/*
 * Copyright (C) 2026 The Dagger Authors.
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

package dagger.internal.codegen.binding;

import dagger.internal.codegen.base.ContributionType;
import dagger.internal.codegen.compileroption.CompilerOptions;

/** Utility class to check strict multibinding exemptions for legacy modules. */
// TODO(b/567460890): Clean up remaining modules and remove this class.
final class StrictMultibindingExemptions {

  static boolean hasStrictMultibindingsExemption(
      CompilerOptions compilerOptions, ContributionBinding binding) {
    // We only give the exemption to multibound map contributions.
    if (!binding.contributionType().equals(ContributionType.MAP)) {
      return false;
    }
    return !compilerOptions.strictMultibindingValidation();
  }

  private StrictMultibindingExemptions() {}
}
