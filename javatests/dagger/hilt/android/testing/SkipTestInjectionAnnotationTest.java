/*
 * Copyright (C) 2024 The Dagger Authors.
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

package dagger.hilt.android.testing;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import javax.inject.Inject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

@HiltAndroidTest
@SkipTestInjectionAnnotationTest.TestAnnotation
@RunWith(AndroidJUnit4.class)
@Config(application = HiltTestApplication.class)
public final class SkipTestInjectionAnnotationTest {
  @Rule public final HiltAndroidRule rule = new HiltAndroidRule(this);

  @SkipTestInjection
  @interface TestAnnotation {}

  @Inject String string;  // Never provided, shouldn't compile without @SkipTestInjection

  @Test
  public void testCannotCallInjectOnTestRule() throws Exception {
    IllegalStateException exception =
        assertThrows(
            IllegalStateException.class,
            () -> rule.inject());
    assertThat(exception)
          .hasMessageThat()
          .isEqualTo("Cannot inject test when using @TestAnnotation");
  }
}
