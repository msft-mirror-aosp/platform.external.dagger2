/*
 * Copyright (C) 2023 The Dagger Authors.
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

package dagger.internal.codegen;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static dagger.internal.codegen.Compilers.compilerWithOptions;

import com.google.common.collect.ImmutableList;
import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import dagger.testing.golden.GoldenFileRule;
import java.util.Collection;
import javax.tools.JavaFileObject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class InaccessibleTypeBindsTest {
  @Parameters(name = "{0}")
  public static Collection<Object[]> parameters() {
    return ImmutableList.copyOf(
        new Object[][] {
            {CompilerMode.DEFAULT_MODE},
            {CompilerMode.DEFAULT_JAVA7_MODE},
            {CompilerMode.FAST_INIT_MODE},
            // FastInit with Java7 is the mode that motivated this test, but do the other
            // modes anyway for completeness.
            {CompilerMode.FAST_INIT_JAVA7_MODE}
        });
  }

  @Rule public GoldenFileRule goldenFileRule = new GoldenFileRule();

  private final CompilerMode compilerMode;

  public InaccessibleTypeBindsTest(CompilerMode compilerMode) {
    this.compilerMode = compilerMode;
  }

  // Interface is accessible, but the impl is not. Use with a scoped binds to make sure type issues
  // are handled from doing an assignment to the Provider<Foo> from DoubleCheck.provider(fooImpl).
  @Test
  public void scopedInaccessibleTypeBound() throws Exception {
    JavaFileObject foo =
        JavaFileObjects.forSourceLines(
            "test.Foo",
            "package test;",
            "",
            "public interface Foo {",
            "}");
    JavaFileObject fooImpl =
        JavaFileObjects.forSourceLines(
            "other.FooImpl",
            "package other;",
            "",
            "import javax.inject.Inject;",
            "import test.Foo;",
            "",
            "final class FooImpl implements Foo {",
            "  @Inject FooImpl() {}",
            "}");
    JavaFileObject module =
        JavaFileObjects.forSourceLines(
            "other.TestModule",
            "package other;",
            "",
            "import dagger.Module;",
            "import dagger.Binds;",
            "import javax.inject.Singleton;",
            "import test.Foo;",
            "",
            "@Module",
            "public interface TestModule {",
            "  @Binds",
            "  @Singleton",
            "  Foo bind(FooImpl impl);",
            "}");
    JavaFileObject component =
        JavaFileObjects.forSourceLines(
            "test.TestComponent",
            "package test;",
            "",
            "import dagger.Component;",
            "import javax.inject.Provider;",
            "import javax.inject.Singleton;",
            "import other.TestModule;",
            "",
            "@Singleton",
            "@Component(modules = TestModule.class)",
            "interface TestComponent {",
            "  Foo getFoo();",
            "}");

    Compilation compilation =
        compilerWithOptions(compilerMode)
            .compile(foo, fooImpl, module, component);
    assertThat(compilation).succeeded();
    assertThat(compilation)
        .generatedSourceFile("test.DaggerTestComponent")
        .hasSourceEquivalentTo(goldenFileRule.goldenFile("test.DaggerTestComponent"));
  }

  // Interface is accessible, but the impl is not. Used with a binds in a loop to see if there are
  // type issues from doing an assignment to the delegate factory e.g.
  // DelegateFactory.setDelegate(provider, new SwitchingProvider<FooImpl>(...));
  @Test
  public void inaccessibleTypeBoundInALoop() throws Exception {
    JavaFileObject foo =
        JavaFileObjects.forSourceLines(
            "test.Foo",
            "package test;",
            "",
            "public interface Foo {",
            "}");
    JavaFileObject fooImpl =
        JavaFileObjects.forSourceLines(
            "other.FooImpl",
            "package other;",
            "",
            "import javax.inject.Inject;",
            "import javax.inject.Provider;",
            "import test.Foo;",
            "",
            "final class FooImpl implements Foo {",
            "  @Inject FooImpl(Provider<Foo> fooProvider) {}",
            "}");
    // Use another entry point to make FooImpl be the first requested class, that way FooImpl's
    // provider is the one that is delegated.
    JavaFileObject otherEntryPoint =
        JavaFileObjects.forSourceLines(
            "other.OtherEntryPoint",
            "package other;",
            "",
            "import javax.inject.Inject;",
            "",
            "public final class OtherEntryPoint {",
            "  @Inject OtherEntryPoint(FooImpl impl) {}",
            "}");
    JavaFileObject module =
        JavaFileObjects.forSourceLines(
            "other.TestModule",
            "package other;",
            "",
            "import dagger.Module;",
            "import dagger.Binds;",
            "import test.Foo;",
            "",
            "@Module",
            "public interface TestModule {",
            "  @Binds",
            "  Foo bind(FooImpl impl);",
            "}");
    JavaFileObject component =
        JavaFileObjects.forSourceLines(
            "test.TestComponent",
            "package test;",
            "",
            "import dagger.Component;",
            "import other.OtherEntryPoint;",
            "import other.TestModule;",
            "",
            "@Component(modules = TestModule.class)",
            "interface TestComponent {",
            "  OtherEntryPoint getOtherEntryPoint();",
            "}");

    Compilation compilation =
        compilerWithOptions(compilerMode)
            .compile(foo, fooImpl, otherEntryPoint, module, component);
    assertThat(compilation).succeeded();
    assertThat(compilation)
        .generatedSourceFile("test.DaggerTestComponent")
        .hasSourceEquivalentTo(goldenFileRule.goldenFile("test.DaggerTestComponent"));
  }

  // Same as above but with the binding scoped.
  @Test
  public void inaccessibleTypeBoundInALoopScoped() throws Exception {
    JavaFileObject foo =
        JavaFileObjects.forSourceLines(
            "test.Foo",
            "package test;",
            "",
            "public interface Foo {",
            "}");
    JavaFileObject fooImpl =
        JavaFileObjects.forSourceLines(
            "other.FooImpl",
            "package other;",
            "",
            "import javax.inject.Inject;",
            "import javax.inject.Provider;",
            "import test.Foo;",
            "",
            "final class FooImpl implements Foo {",
            "  @Inject FooImpl(Provider<Foo> fooProvider) {}",
            "}");
    // Use another entry point to make FooImpl be the first requested class, that way FooImpl's
    // provider is the one that is delegated.
    JavaFileObject otherEntryPoint =
        JavaFileObjects.forSourceLines(
            "other.OtherEntryPoint",
            "package other;",
            "",
            "import javax.inject.Inject;",
            "",
            "public final class OtherEntryPoint {",
            "  @Inject OtherEntryPoint(FooImpl impl) {}",
            "}");
    JavaFileObject module =
        JavaFileObjects.forSourceLines(
            "other.TestModule",
            "package other;",
            "",
            "import dagger.Module;",
            "import dagger.Binds;",
            "import javax.inject.Singleton;",
            "import test.Foo;",
            "",
            "@Module",
            "public interface TestModule {",
            "  @Binds",
            "  @Singleton",
            "  Foo bind(FooImpl impl);",
            "}");
    JavaFileObject component =
        JavaFileObjects.forSourceLines(
            "test.TestComponent",
            "package test;",
            "",
            "import dagger.Component;",
            "import javax.inject.Singleton;",
            "import other.OtherEntryPoint;",
            "import other.TestModule;",
            "",
            "@Singleton",
            "@Component(modules = TestModule.class)",
            "interface TestComponent {",
            "  OtherEntryPoint getOtherEntryPoint();",
            "}");

    Compilation compilation =
        compilerWithOptions(compilerMode)
            .compile(foo, fooImpl, otherEntryPoint, module, component);
    assertThat(compilation).succeeded();
    assertThat(compilation)
        .generatedSourceFile("test.DaggerTestComponent")
        .hasSourceEquivalentTo(goldenFileRule.goldenFile("test.DaggerTestComponent"));
  }
}
