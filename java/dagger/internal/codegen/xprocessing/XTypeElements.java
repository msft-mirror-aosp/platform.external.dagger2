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

package dagger.internal.codegen.xprocessing;

import static androidx.room.compiler.processing.compat.XConverters.getProcessingEnv;
import static com.google.common.base.Preconditions.checkNotNull;
import static dagger.internal.codegen.extension.DaggerStreams.toImmutableList;
import static kotlin.streams.jdk8.StreamsKt.asStream;

import androidx.room.compiler.processing.XHasModifiers;
import androidx.room.compiler.processing.XMethodElement;
import androidx.room.compiler.processing.XProcessingEnv;
import androidx.room.compiler.processing.XTypeElement;
import androidx.room.compiler.processing.XTypeParameterElement;
import androidx.room.compiler.processing.compat.XConverters;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.devtools.ksp.symbol.Origin;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeVariableName;

// TODO(bcorso): Consider moving these methods into XProcessing library.
/** A utility class for {@link XTypeElement} helper methods. */
public final class XTypeElements {
  private enum Visibility {
    PUBLIC,
    PRIVATE,
    OTHER;

    /** Returns the visibility of the given {@link XTypeElement}. */
    private static Visibility of(XTypeElement element) {
      checkNotNull(element);
      if (element.isPrivate()) {
        return Visibility.PRIVATE;
      } else if (element.isPublic()) {
        return Visibility.PUBLIC;
      } else {
        return Visibility.OTHER;
      }
    }
  }

  // TODO(bcorso): Consider XParameterizable interface to handle both methods and types.
  /** Returns the type arguments for the given type as a list of {@link TypeVariableName}. */
  public static ImmutableList<TypeVariableName> typeVariableNames(XTypeElement typeElement) {
    return typeElement.getTypeParameters().stream()
        .map(XTypeParameterElement::getTypeVariableName)
        .collect(toImmutableList());
  }

  /** Returns {@code true} if the given element is nested. */
  public static boolean isNested(XTypeElement typeElement) {
    return typeElement.getEnclosingTypeElement() != null;
  }

  /** Returns {@code true} if the given {@code type} has type parameters. */
  public static boolean hasTypeParameters(XTypeElement typeElement) {
    return !typeElement.getTypeParameters().isEmpty();
  }

  /** Returns all non-private, non-static, abstract methods in {@code type}. */
  public static ImmutableList<XMethodElement> getAllUnimplementedMethods(XTypeElement type) {
    return getAllNonPrivateInstanceMethods(type).stream()
        .filter(XHasModifiers::isAbstract)
        .collect(toImmutableList());
  }

  /** Returns all non-private, non-static methods in {@code type}. */
  public static ImmutableList<XMethodElement> getAllNonPrivateInstanceMethods(XTypeElement type) {
    return getAllMethods(type).stream()
        .filter(method -> !method.isPrivate() && !method.isStatic())
        .collect(toImmutableList());
  }

  // TODO(wanyingd): rename this to getAllMethodsWithoutPrivate, since the private method declared
  // within this element is being filtered out. This doesn't mirror {@code
  // MoreElements#getAllMethods}'s behavior but have the same name, and can cause confusion to
  // developers.
  public static ImmutableList<XMethodElement> getAllMethods(XTypeElement type) {
    return asStream(type.getAllMethods())
        .filter(method -> isAccessibleFrom(method, type))
        .collect(toImmutableList());
  }

  public static ImmutableList<XMethodElement> getAllMethodsIncludingPrivate(XTypeElement type) {
    return asStream(type.getAllMethods()).collect(toImmutableList());
  }

  private static boolean isAccessibleFrom(XMethodElement method, XTypeElement type) {
    if (method.isPublic() || method.isProtected()) {
      return true;
    }
    if (method.isPrivate()) {
      return false;
    }
    return method
        .getClosestMemberContainer()
        .getClassName()
        .packageName()
        .equals(type.getClassName().packageName());
  }

  public static boolean isEffectivelyPublic(XTypeElement element) {
    return allVisibilities(element).stream()
        .allMatch(visibility -> visibility.equals(Visibility.PUBLIC));
  }

  public static boolean isEffectivelyPrivate(XTypeElement element) {
    return allVisibilities(element).contains(Visibility.PRIVATE);
  }

  public static boolean isJvmClass(XTypeElement element) {
    return element.isClass() || element.isKotlinObject() || element.isCompanionObject();
  }

  /**
   * Returns a list of visibilities containing visibility of the given element and the visibility of
   * its enclosing elements.
   */
  private static ImmutableSet<Visibility> allVisibilities(XTypeElement element) {
    checkNotNull(element);
    ImmutableSet.Builder<Visibility> visibilities = ImmutableSet.builder();
    XTypeElement currentElement = element;
    while (currentElement != null) {
      visibilities.add(Visibility.of(currentElement));
      currentElement = currentElement.getEnclosingTypeElement();
    }
    return visibilities.build();
  }

  /** Returns true if the source of the given type element is Kotlin. */
  public static boolean isKotlinSource(XTypeElement typeElement) {
    XProcessingEnv processingEnv = getProcessingEnv(typeElement);
    switch (processingEnv.getBackend()) {
      case KSP:
        // If this is KSP, then we should be able to check the origin of the declaration.
        Origin origin = XConverters.toKS(typeElement).getOrigin();
        return origin == Origin.KOTLIN || origin == Origin.KOTLIN_LIB;
      case JAVAC:
        // If this is KAPT, then the java stubs should have kotlin metadata.
        return typeElement.hasAnnotation(ClassName.get("kotlin", "Metadata"));
    }
    throw new AssertionError("Unhandled backend kind: " + processingEnv.getBackend());
  }

  private XTypeElements() {}
}
