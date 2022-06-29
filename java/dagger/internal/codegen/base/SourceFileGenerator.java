/*
 * Copyright (C) 2014 The Dagger Authors.
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

package dagger.internal.codegen.base;

import static androidx.room.compiler.processing.JavaPoetExtKt.addOriginatingElement;
import static androidx.room.compiler.processing.compat.XConverters.toJavac;
import static com.google.auto.common.GeneratedAnnotations.generatedAnnotation;
import static com.google.common.base.Preconditions.checkNotNull;
import static dagger.internal.codegen.javapoet.AnnotationSpecs.Suppression.RAWTYPES;
import static dagger.internal.codegen.javapoet.AnnotationSpecs.Suppression.UNCHECKED;

import androidx.room.compiler.processing.XElement;
import androidx.room.compiler.processing.XFiler;
import androidx.room.compiler.processing.XMessager;
import androidx.room.compiler.processing.compat.XConverters;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import dagger.internal.DaggerGenerated;
import dagger.internal.codegen.javapoet.AnnotationSpecs;
import dagger.internal.codegen.javapoet.AnnotationSpecs.Suppression;
import dagger.internal.codegen.langmodel.DaggerElements;
import java.util.Optional;
import javax.lang.model.SourceVersion;

/**
 * A template class that provides a framework for properly handling IO while generating source files
 * from an annotation processor. Particularly, it makes a best effort to ensure that files that fail
 * to write successfully are deleted.
 *
 * @param <T> The input type from which source is to be generated.
 */
public abstract class SourceFileGenerator<T> {
  private static final String GENERATED_COMMENTS = "https://dagger.dev";

  private final XFiler filer;
  private final DaggerElements elements;
  private final SourceVersion sourceVersion;

  public SourceFileGenerator(XFiler filer, DaggerElements elements, SourceVersion sourceVersion) {
    this.filer = checkNotNull(filer);
    this.elements = checkNotNull(elements);
    this.sourceVersion = checkNotNull(sourceVersion);
  }

  public SourceFileGenerator(SourceFileGenerator<T> delegate) {
    this(delegate.filer, delegate.elements, delegate.sourceVersion);
  }

  /**
   * Generates a source file to be compiled for {@code T}. Writes any generation exception to {@code
   * messager} and does not throw.
   */
  public void generate(T input, XMessager messager) {
    try {
      generate(input);
    } catch (SourceFileGenerationException e) {
      e.printMessageTo(messager);
    }
  }

  /** Generates a source file to be compiled for {@code T}. */
  public void generate(T input) throws SourceFileGenerationException {
    for (TypeSpec.Builder type : topLevelTypes(input)) {
      try {
        buildJavaFile(input, type).writeTo(XConverters.toJavac(filer));
      } catch (Exception e) {
        // if the code above threw a SFGE, use that
        Throwables.propagateIfPossible(e, SourceFileGenerationException.class);
        // otherwise, throw a new one
        throw new SourceFileGenerationException(Optional.empty(), e, originatingElement(input));
      }
    }
  }

  private JavaFile buildJavaFile(T input, TypeSpec.Builder typeSpecBuilder) {
    addOriginatingElement(typeSpecBuilder, originatingElement(input));
    typeSpecBuilder.addAnnotation(DaggerGenerated.class);
    Optional<AnnotationSpec> generatedAnnotation =
        generatedAnnotation(elements, sourceVersion)
            .map(
                annotation ->
                    AnnotationSpec.builder(ClassName.get(annotation))
                        .addMember("value", "$S", "dagger.internal.codegen.ComponentProcessor")
                        .addMember("comments", "$S", GENERATED_COMMENTS)
                        .build());
    generatedAnnotation.ifPresent(typeSpecBuilder::addAnnotation);

    // TODO(b/134590785): remove this and only suppress annotations locally, if necessary
    typeSpecBuilder.addAnnotation(
        AnnotationSpecs.suppressWarnings(
            ImmutableSet.<Suppression>builder()
                .addAll(warningSuppressions())
                .add(UNCHECKED, RAWTYPES)
                .build()));

    JavaFile.Builder javaFileBuilder =
        JavaFile.builder(
                elements
                    .getPackageOf(toJavac(originatingElement(input)))
                    .getQualifiedName()
                    .toString(),
                typeSpecBuilder.build())
            .skipJavaLangImports(true);
    if (!generatedAnnotation.isPresent()) {
      javaFileBuilder.addFileComment("Generated by Dagger ($L).", GENERATED_COMMENTS);
    }
    return javaFileBuilder.build();
  }

  /** Returns the originating element of the generating type. */
  public abstract XElement originatingElement(T input);

  /**
   * Returns {@link TypeSpec.Builder types} be generated for {@code T}, or an empty list if no types
   * should be generated.
   *
   * <p>Every type will be generated in its own file.
   */
  public abstract ImmutableList<TypeSpec.Builder> topLevelTypes(T input);

  /** Returns {@link Suppression}s that are applied to files generated by this generator. */
  // TODO(b/134590785): When suppressions are removed locally, remove this and inline the usages
  protected ImmutableSet<Suppression> warningSuppressions() {
    return ImmutableSet.of();
  }
}
