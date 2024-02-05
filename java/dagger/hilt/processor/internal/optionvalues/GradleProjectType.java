/*
 * Copyright (C) 2022 The Dagger Authors.
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

package dagger.hilt.processor.internal.optionvalues;

/**
 * Valid Gradle project type values. Note that we exclude 'com.android.feature' as Hilt doesn't
 * support it for now.
 */
public enum GradleProjectType {
  /** Project type is not set, e.g. Hilt Gradle Plugin not applied. */
  UNSET,

  /** App project created with plugin 'com.android.application'. */
  APP,

  /** Library project created with plugin 'com.android.library'. */
  LIBRARY,

  /** Test project created with plugin 'com.android.test'. */
  TEST
}
