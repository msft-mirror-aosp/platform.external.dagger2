/*
 * Copyright (C) 2021 The Dagger Authors.
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


package dagger.hilt.android.internal.uninstallmodules;

import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Retention;

/** Holds aggregated data about {@link dagger.hilt.android.testing.UninstallModules} elements. */
@Retention(CLASS)
public @interface AggregatedUninstallModules {

  /** Returns the test annotated with {@link dagger.hilt.android.testing.UninstallModules}. */
  String test();

  /** Returns the list of modules to uninstall. */
  String[] uninstallModules();
}
