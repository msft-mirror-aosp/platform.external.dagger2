/*
 * Copyright (C) 2018 The Dagger Authors.
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

package dagger.android.internal;

/**
 * An internal implementation detail of Dagger's generated code. This is not guaranteed to remain
 * consistent from version to version.
 */
@GenerateAndroidInjectionProguardRules
public final class AndroidInjectionKeys {
  /**
   * Accepts the fully qualified name of a class that is injected with {@code dagger.android}.
   *
   * <p>From a runtime perspective, this method does nothing except return its single argument. It
   * is used as a signal to bytecode shrinking tools that its argument should be rewritten if it
   * corresponds to a class that has been obfuscated/relocated. Once it is done so, it is expected
   * that the argument will be inlined and this method will go away.
   */
  public static String of(String mapKey) {
    return mapKey;
  }

  private AndroidInjectionKeys() {}
}
