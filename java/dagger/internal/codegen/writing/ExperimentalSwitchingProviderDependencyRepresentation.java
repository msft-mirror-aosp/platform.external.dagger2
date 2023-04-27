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

package dagger.internal.codegen.writing;

import static androidx.room.compiler.processing.compat.XConverters.toJavac;
import static dagger.internal.codegen.extension.DaggerStreams.toImmutableList;
import static dagger.internal.codegen.langmodel.Accessibility.isRawTypeAccessible;
import static dagger.internal.codegen.langmodel.Accessibility.isTypeAccessibleFrom;

import com.squareup.javapoet.CodeBlock;
import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import dagger.internal.codegen.base.ContributionType;
import dagger.internal.codegen.binding.BindsTypeChecker;
import dagger.internal.codegen.binding.FrameworkType;
import dagger.internal.codegen.binding.ProvisionBinding;
import dagger.internal.codegen.javapoet.Expression;
import dagger.internal.codegen.javapoet.TypeNames;
import dagger.internal.codegen.langmodel.DaggerElements;
import dagger.internal.codegen.langmodel.DaggerTypes;
import dagger.internal.codegen.writing.ComponentImplementation.ShardImplementation;
import dagger.spi.model.BindingKind;
import dagger.spi.model.DependencyRequest;
import dagger.spi.model.RequestKind;
import javax.lang.model.type.TypeMirror;

/**
 * Returns type casted expressions to satisfy dependency requests from experimental switching
 * providers.
 */
final class ExperimentalSwitchingProviderDependencyRepresentation {
  private final ProvisionBinding binding;
  private final ShardImplementation shardImplementation;
  private final BindsTypeChecker bindsTypeChecker;
  private final DaggerTypes types;
  private final DaggerElements elements;
  private final TypeMirror type;

  @AssistedInject
  ExperimentalSwitchingProviderDependencyRepresentation(
      @Assisted ProvisionBinding binding,
      ComponentImplementation componentImplementation,
      DaggerTypes types,
      DaggerElements elements) {
    this.binding = binding;
    this.shardImplementation = componentImplementation.shardImplementation(binding);
    this.types = types;
    this.elements = elements;
    this.bindsTypeChecker = new BindsTypeChecker(types, elements);
    this.type =
        isDelegateSetValuesBinding()
            ? types.erasure(elements.getTypeElement(TypeNames.COLLECTION).asType())
            : toJavac(binding.contributedType());
  }

  Expression getDependencyExpression(RequestKind requestKind, ProvisionBinding requestingBinding) {
    int index = findIndexOfDependency(requestingBinding);
    TypeMirror frameworkType =
        types.getDeclaredType(elements.getTypeElement(FrameworkType.PROVIDER.frameworkClassName()));
    Expression expression =
        FrameworkType.PROVIDER.to(
            requestKind,
            Expression.create(
                frameworkType, CodeBlock.of("(($T) dependencies[$L])", frameworkType, index)),
            types);
    if (usesExplicitTypeCast(expression, requestKind)) {
      return expression.castTo(type);
    }
    if (usesErasedTypeCast(requestKind)) {
      return expression.castTo(types.erasure(type));
    }
    return expression;
  }

  private int findIndexOfDependency(ProvisionBinding requestingBinding) {
    return requestingBinding.dependencies().stream()
            .map(DependencyRequest::key)
            .collect(toImmutableList())
            .indexOf(binding.key())
        + (requestingBinding.requiresModuleInstance()
                && requestingBinding.contributingModule().isPresent()
            ? 1
            : 0);
  }

  private boolean isDelegateSetValuesBinding() {
    return binding.kind().equals(BindingKind.DELEGATE)
        && binding.contributionType().equals(ContributionType.SET_VALUES);
  }

  private boolean usesExplicitTypeCast(Expression expression, RequestKind requestKind) {
    // If the type is accessible, we can directly cast the expression use the type.
    return requestKind.equals(RequestKind.INSTANCE)
        && !bindsTypeChecker.isAssignable(expression.type(), type, binding.contributionType())
        && isTypeAccessibleFrom(type, shardImplementation.name().packageName());
  }

  private boolean usesErasedTypeCast(RequestKind requestKind) {
    // If a type has inaccessible type arguments, then cast to raw type.
    return requestKind.equals(RequestKind.INSTANCE)
        && !isTypeAccessibleFrom(type, shardImplementation.name().packageName())
        && isRawTypeAccessible(type, shardImplementation.name().packageName());
  }

  @AssistedFactory
  static interface Factory {
    ExperimentalSwitchingProviderDependencyRepresentation create(ProvisionBinding binding);
  }
}
