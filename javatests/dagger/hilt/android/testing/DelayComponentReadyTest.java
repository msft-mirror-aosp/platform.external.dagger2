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

package dagger.hilt.android.testing;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import dagger.hilt.EntryPoint;
import dagger.hilt.EntryPoints;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.model.Statement;
import org.robolectric.annotation.Config;

@HiltAndroidTest
@RunWith(AndroidJUnit4.class)
@Config(application = HiltTestApplication.class)
public final class DelayComponentReadyTest {
  private static final String EXPECTED_VALUE = "expected";

  @EntryPoint
  @InstallIn(SingletonComponent.class)
  public interface FooEntryPoint {
    String foo();
  }

  // If true, verifies that HiltAndroidRule threw an IllegalStateException
  private boolean verifyTestRuleThrew = false;

  // A test rule that wraps HiltAndroidRule to verify it throws
  private final TestRule exceptionVerifyingRule =
      (base, description) -> {
        return new Statement() {
          @Override
          public void evaluate() throws Throwable {
            AtomicReference<IllegalStateException> caught = new AtomicReference<>();
            try {
              base.evaluate();
            } catch (IllegalStateException e) {
              caught.set(e);
              if (!verifyTestRuleThrew) {
                throw e;
              }
            }
            if (verifyTestRuleThrew) {
              IllegalStateException expected = caught.get();
              if (expected == null) {
                throw new AssertionError("Did not throw expected expection");
              }
              assertThat(expected)
                  .hasMessageThat()
                  .isEqualTo("Used delayComponentReady(), but never called componentReady()");
            }
          }
        };
      };

  private final HiltAndroidRule rule = new HiltAndroidRule(this).delayComponentReady();

  @Rule public final RuleChain ruleChain = RuleChain.outerRule(exceptionVerifyingRule).around(rule);

  @BindValue String foo;

  @Test
  public void testLateBindValue() throws Exception {
    AtomicReference<String> fooRef = new AtomicReference<>();
    OnComponentReadyRunner.addListener(
        ApplicationProvider.getApplicationContext(),
        FooEntryPoint.class,
        entryPoint -> fooRef.set(entryPoint.foo()));

    // Test that setting the listener before the component is ready doesn't run the listener.
    assertThat(fooRef.get()).isNull();

    foo = EXPECTED_VALUE;
    rule.componentReady().inject();
    assertThat(EntryPoints.get(getApplicationContext(), FooEntryPoint.class).foo())
        .isEqualTo(EXPECTED_VALUE);
  }

  @Test
  public void testUnitializedBindValue_fails() throws Exception {
    OnComponentReadyRunner.addListener(
        ApplicationProvider.getApplicationContext(), FooEntryPoint.class, FooEntryPoint::foo);

    // foo not set
    NullPointerException expected = assertThrows(NullPointerException.class, rule::componentReady);
    // This is not the best error message, but it is equivalent to the message from a regular
    // (non-delayed) @BindValue returning null;
    assertThat(expected)
        .hasMessageThat()
        .isEqualTo("Cannot return null from a non-@Nullable @Provides method");
  }

  @Test
  public void testDoubleComponentReady_fails() throws Exception {
    foo = EXPECTED_VALUE;
    rule.componentReady();
    IllegalStateException expected =
        assertThrows(IllegalStateException.class, rule::componentReady);
    assertThat(expected).hasMessageThat().isEqualTo("Called componentReady() multiple times");
  }

  @Test
  public void testMissingComponentReady_fails() throws Exception {
    // componentReady not called
    foo = EXPECTED_VALUE;
    IllegalStateException expected = assertThrows(IllegalStateException.class, rule::inject);
    assertThat(expected)
        .hasMessageThat()
        .isEqualTo("Called inject() before calling componentReady()");
  }

  @Test
  public void testDelayComponentReadyAfterStart_fails() throws Exception {
    IllegalStateException expected =
        assertThrows(IllegalStateException.class, rule::delayComponentReady);
    assertThat(expected)
        .hasMessageThat()
        .isEqualTo("Called delayComponentReady after test execution started");
    // Prevents failure due to never calling componentReady()
    foo = EXPECTED_VALUE;
    rule.componentReady();
  }

  @Test
  public void neverCallsComponentReady_fails() throws Exception {
    // Does not call componentReady()
    verifyTestRuleThrew = true;
  }
}
