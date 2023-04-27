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

package dagger.internal.codegen.binding;

import static dagger.internal.codegen.base.MoreAnnotationMirrors.unwrapOptionalEquivalence;
import static dagger.internal.codegen.xprocessing.XElements.asMethod;
import static java.util.Arrays.asList;

import androidx.room.compiler.processing.XElement;
import androidx.room.compiler.processing.XElementKt;
import androidx.room.compiler.processing.XType;
import androidx.room.compiler.processing.XTypeElement;
import com.google.common.base.Equivalence;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.CheckReturnValue;
import dagger.internal.codegen.base.ContributionType;
import dagger.internal.codegen.base.ContributionType.HasContributionType;
import dagger.internal.codegen.base.MapType;
import dagger.internal.codegen.base.SetType;
import dagger.internal.codegen.xprocessing.XTypes;
import dagger.spi.model.BindingKind;
import dagger.spi.model.DependencyRequest;
import dagger.spi.model.Key;
import java.util.Optional;
import javax.lang.model.element.AnnotationMirror;

/**
 * An abstract class for a value object representing the mechanism by which a {@link Key} can be
 * contributed to a dependency graph.
 */
public abstract class ContributionBinding extends Binding implements HasContributionType {

  /** Returns the type that specifies this' nullability, absent if not nullable. */
  public abstract Optional<XType> nullableType();

  public abstract Optional<Equivalence.Wrapper<AnnotationMirror>> wrappedMapKeyAnnotation();

  public final Optional<AnnotationMirror> mapKeyAnnotation() {
    return unwrapOptionalEquivalence(wrappedMapKeyAnnotation());
  }

  /** If {@link #bindingElement()} is a method that returns a primitive type, returns that type. */
  public final Optional<XType> contributedPrimitiveType() {
    return bindingElement()
        .filter(XElementKt::isMethod)
        .map(bindingElement -> asMethod(bindingElement).getReturnType())
        .filter(XTypes::isPrimitive);
  }

  @Override
  public boolean requiresModuleInstance() {
    return !isContributingModuleKotlinObject() && super.requiresModuleInstance();
  }

  @Override
  public final boolean isNullable() {
    return nullableType().isPresent();
  }

  /**
   * Returns {@code true} if the contributing module is a Kotlin object. Note that a companion
   * object is also considered a Kotlin object.
   */
  private boolean isContributingModuleKotlinObject() {
    return contributingModule().isPresent()
        && (contributingModule().get().isKotlinObject()
            || contributingModule().get().isCompanionObject());
  }

  /**
   * The {@link XType type} for the {@code Factory<T>} or {@code Producer<T>} which is created for
   * this binding. Uses the binding's key, V in the case of {@code Map<K, FrameworkClass<V>>>}, and
   * E {@code Set<E>} for {@link dagger.multibindings.IntoSet @IntoSet} methods.
   */
  public final XType contributedType() {
    switch (contributionType()) {
      case MAP:
        return MapType.from(key()).unwrappedFrameworkValueType();
      case SET:
        return SetType.from(key()).elementType();
      case SET_VALUES:
      case UNIQUE:
        return key().type().xprocessing();
    }
    throw new AssertionError();
  }

  public abstract Builder<?, ?> toBuilder();

  /**
   * Base builder for {@link com.google.auto.value.AutoValue @AutoValue} subclasses of {@link
   * ContributionBinding}.
   */
  @CanIgnoreReturnValue
  public abstract static class Builder<C extends ContributionBinding, B extends Builder<C, B>> {
    public abstract B dependencies(Iterable<DependencyRequest> dependencies);

    public B dependencies(DependencyRequest... dependencies) {
      return dependencies(asList(dependencies));
    }

    public abstract B unresolved(C unresolved);

    public abstract B contributionType(ContributionType contributionType);

    public abstract B bindingElement(XElement bindingElement);

    abstract B bindingElement(Optional<XElement> bindingElement);

    public final B clearBindingElement() {
      return bindingElement(Optional.empty());
    };

    abstract B contributingModule(XTypeElement contributingModule);

    public abstract B key(Key key);

    public abstract B nullableType(Optional<XType> nullableType);

    abstract B wrappedMapKeyAnnotation(
        Optional<Equivalence.Wrapper<AnnotationMirror>> wrappedMapKeyAnnotation);

    public abstract B kind(BindingKind kind);

    @CheckReturnValue
    abstract C build();
  }
}
