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

package dagger.internal.codegen;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static dagger.internal.codegen.Compilers.compilerWithOptions;

import com.google.common.collect.ImmutableList;
import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import javax.tools.JavaFileObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public final class ComponentProtectedTypeTest {
  @Parameters(name = "{0}")
  public static ImmutableList<Object[]> parameters() {
    return CompilerMode.TEST_PARAMETERS;
  }

  private final CompilerMode compilerMode;

  public ComponentProtectedTypeTest(CompilerMode compilerMode) {
    this.compilerMode = compilerMode;
  }

  @Test
  public void componentAccessesProtectedType_succeeds() {
    JavaFileObject baseSrc =
        JavaFileObjects.forSourceLines(
            "test.sub.TestComponentBase",
            "package test.sub;",
            "",
            "import javax.inject.Inject;",
            "import javax.inject.Singleton;",
            "",
            "public abstract class TestComponentBase {",
            "  static class Dep {",
            "    @Inject",
            "    Dep() {}",
            "  }",
            "",
            "  @Singleton",
            "  protected static final class ProtectedType {",
            "    @Inject",
            "    ProtectedType(Dep dep) {}",
            "  }",
            "}");
    JavaFileObject componentSrc =
        JavaFileObjects.forSourceLines(
            "test.TestComponent",
            "package test;",
            "",
            "import dagger.Component;",
            "import javax.inject.Provider;",
            "import javax.inject.Singleton;",
            "import test.sub.TestComponentBase;",
            "",
            "@Singleton",
            "@Component",
            "public abstract class TestComponent extends TestComponentBase {",
            // This component method will be implemented as:
            // TestComponentBase.ProtectedType provideProtectedType() {
            //   return protectedTypeProvider.get();
            // }
            // The protectedTypeProvider can't be a raw provider, otherwise it will have a type cast
            // error. So protected accessibility should be evaluated when checking accessibility of
            // a type.
            "  abstract TestComponentBase.ProtectedType provideProtectedType();",
            "}");
    JavaFileObject generatedComponent =
        JavaFileObjects.forSourceLines(
            "test.DaggerTestComponent",
            "package test;",
            "",
            GeneratedLines.generatedAnnotations(),
            "public final class DaggerTestComponent extends TestComponent {",
            "  private Provider<test.sub.TestComponentBase.ProtectedType> protectedTypeProvider;",
            "",
            "  @Override",
            "  test.sub.TestComponentBase.ProtectedType provideProtectedType() {",
            "    return protectedTypeProvider.get();",
            "  }",
            "}");

    Compilation compilation =
        compilerWithOptions(compilerMode.javacopts()).compile(baseSrc, componentSrc);

    assertThat(compilation).succeeded();
    assertThat(compilation)
        .generatedSourceFile("test.DaggerTestComponent")
        .containsElementsIn(generatedComponent);
  }
}
