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

package dagger.internal.codegen.xprocessing;

import static com.google.common.collect.Iterables.getLast;

import androidx.room.compiler.codegen.XClassName;
import androidx.room.compiler.codegen.XTypeName;
import androidx.room.compiler.processing.XType;
import com.google.common.collect.ImmutableSet;

/** Common names and convenience methods for XPoet {@link XTypeName} usage. */
public final class XTypeNames {

  // Dagger Core classnames
  public static final XClassName ASSISTED = XClassName.Companion.get("dagger.assisted", "Assisted");
  public static final XClassName ASSISTED_FACTORY =
      XClassName.Companion.get("dagger.assisted", "AssistedFactory");
  public static final XClassName ASSISTED_INJECT =
      XClassName.Companion.get("dagger.assisted", "AssistedInject");
  public static final XClassName BINDS = XClassName.Companion.get("dagger", "Binds");
  public static final XClassName BINDS_INSTANCE =
      XClassName.Companion.get("dagger", "BindsInstance");
  public static final XClassName BINDS_OPTIONAL_OF =
      XClassName.Companion.get("dagger", "BindsOptionalOf");
  public static final XClassName COMPONENT = XClassName.Companion.get("dagger", "Component");
  public static final XClassName COMPONENT_BUILDER =
      XClassName.Companion.get("dagger", "Component", "Builder");
  public static final XClassName COMPONENT_FACTORY =
      XClassName.Companion.get("dagger", "Component", "Factory");
  public static final XClassName DAGGER_PROCESSING_OPTIONS =
      XClassName.Companion.get("dagger", "DaggerProcessingOptions");
  public static final XClassName ELEMENTS_INTO_SET =
      XClassName.Companion.get("dagger.multibindings", "ElementsIntoSet");
  public static final XClassName INTO_MAP =
      XClassName.Companion.get("dagger.multibindings", "IntoMap");
  public static final XClassName INTO_SET =
      XClassName.Companion.get("dagger.multibindings", "IntoSet");
  public static final XClassName MAP_KEY = XClassName.Companion.get("dagger", "MapKey");
  public static final XClassName MODULE = XClassName.Companion.get("dagger", "Module");
  public static final XClassName MULTIBINDS =
      XClassName.Companion.get("dagger.multibindings", "Multibinds");
  public static final XClassName PROVIDES = XClassName.Companion.get("dagger", "Provides");
  public static final XClassName REUSABLE = XClassName.Companion.get("dagger", "Reusable");
  public static final XClassName SUBCOMPONENT = XClassName.Companion.get("dagger", "Subcomponent");
  public static final XClassName SUBCOMPONENT_BUILDER =
      XClassName.Companion.get("dagger", "Subcomponent", "Builder");
  public static final XClassName SUBCOMPONENT_FACTORY =
      XClassName.Companion.get("dagger", "Subcomponent", "Factory");

  // Dagger Internal classnames
  public static final XClassName IDENTIFIER_NAME_STRING =
      XClassName.Companion.get("dagger.internal", "IdentifierNameString");
  public static final XClassName KEEP_FIELD_TYPE =
      XClassName.Companion.get("dagger.internal", "KeepFieldType");
  public static final XClassName LAZY_CLASS_KEY =
      XClassName.Companion.get("dagger.multibindings", "LazyClassKey");
  public static final XClassName LAZY_CLASS_KEY_MAP =
      XClassName.Companion.get("dagger.internal", "LazyClassKeyMap");
  public static final XClassName LAZY_CLASS_KEY_MAP_FACTORY =
      XClassName.Companion.get("dagger.internal", "LazyClassKeyMap", "MapFactory");
  public static final XClassName LAZY_CLASS_KEY_MAP_PROVIDER_FACTORY =
      XClassName.Companion.get("dagger.internal", "LazyClassKeyMap", "MapProviderFactory");
  public static final XClassName LAZY_MAP_OF_PRODUCED_PRODUCER =
      XClassName.Companion.get("dagger.producers.internal", "LazyMapOfProducedProducer");
  public static final XClassName LAZY_MAP_OF_PRODUCER_PRODUCER =
      XClassName.Companion.get("dagger.producers.internal", "LazyMapOfProducerProducer");
  public static final XClassName LAZY_MAP_PRODUCER =
      XClassName.Companion.get("dagger.producers.internal", "LazyMapProducer");

  public static final XClassName DELEGATE_FACTORY =
      XClassName.Companion.get("dagger.internal", "DelegateFactory");
  public static final XClassName DOUBLE_CHECK =
      XClassName.Companion.get("dagger.internal", "DoubleCheck");

  public static final XClassName FACTORY = XClassName.Companion.get("dagger.internal", "Factory");
  public static final XClassName INJECTED_FIELD_SIGNATURE =
      XClassName.Companion.get("dagger.internal", "InjectedFieldSignature");
  public static final XClassName INSTANCE_FACTORY =
      XClassName.Companion.get("dagger.internal", "InstanceFactory");
  public static final XClassName MAP_BUILDER =
      XClassName.Companion.get("dagger.internal", "MapBuilder");
  public static final XClassName MAP_FACTORY =
      XClassName.Companion.get("dagger.internal", "MapFactory");
  public static final XClassName MAP_PROVIDER_FACTORY =
      XClassName.Companion.get("dagger.internal", "MapProviderFactory");
  public static final XClassName MEMBERS_INJECTOR =
      XClassName.Companion.get("dagger", "MembersInjector");
  public static final XClassName MEMBERS_INJECTORS =
      XClassName.Companion.get("dagger.internal", "MembersInjectors");
  public static final XClassName PROVIDER = XClassName.Companion.get("javax.inject", "Provider");
  public static final XClassName DAGGER_PROVIDER =
      XClassName.Companion.get("dagger.internal", "Provider");
  public static final XClassName DAGGER_PROVIDERS =
      XClassName.Companion.get("dagger.internal", "Providers");
  public static final XClassName PROVIDER_OF_LAZY =
      XClassName.Companion.get("dagger.internal", "ProviderOfLazy");
  public static final XClassName SCOPE_METADATA =
      XClassName.Companion.get("dagger.internal", "ScopeMetadata");
  public static final XClassName QUALIFIER_METADATA =
      XClassName.Companion.get("dagger.internal", "QualifierMetadata");
  public static final XClassName SET_FACTORY =
      XClassName.Companion.get("dagger.internal", "SetFactory");
  public static final XClassName SINGLE_CHECK =
      XClassName.Companion.get("dagger.internal", "SingleCheck");
  public static final XClassName LAZY = XClassName.Companion.get("dagger", "Lazy");

  // Dagger Producers classnames
  public static final XClassName ABSTRACT_PRODUCER =
      XClassName.Companion.get("dagger.producers.internal", "AbstractProducer");
  public static final XClassName ABSTRACT_PRODUCES_METHOD_PRODUCER =
      XClassName.Companion.get("dagger.producers.internal", "AbstractProducesMethodProducer");
  public static final XClassName CANCELLATION_LISTENER =
      XClassName.Companion.get("dagger.producers.internal", "CancellationListener");
  public static final XClassName CANCELLATION_POLICY =
      XClassName.Companion.get("dagger.producers", "CancellationPolicy");
  public static final XClassName DELEGATE_PRODUCER =
      XClassName.Companion.get("dagger.producers.internal", "DelegateProducer");
  public static final XClassName DEPENDENCY_METHOD_PRODUCER =
      XClassName.Companion.get("dagger.producers.internal", "DependencyMethodProducer");
  public static final XClassName MAP_OF_PRODUCED_PRODUCER =
      XClassName.Companion.get("dagger.producers.internal", "MapOfProducedProducer");
  public static final XClassName MAP_OF_PRODUCER_PRODUCER =
      XClassName.Companion.get("dagger.producers.internal", "MapOfProducerProducer");
  public static final XClassName MAP_PRODUCER =
      XClassName.Companion.get("dagger.producers.internal", "MapProducer");
  public static final XClassName MONITORS =
      XClassName.Companion.get("dagger.producers.monitoring.internal", "Monitors");
  public static final XClassName PRODUCED =
      XClassName.Companion.get("dagger.producers", "Produced");
  public static final XClassName PRODUCER =
      XClassName.Companion.get("dagger.producers", "Producer");
  public static final XClassName PRODUCERS =
      XClassName.Companion.get("dagger.producers.internal", "Producers");
  public static final XClassName PRODUCER_MODULE =
      XClassName.Companion.get("dagger.producers", "ProducerModule");
  public static final XClassName PRODUCES =
      XClassName.Companion.get("dagger.producers", "Produces");
  public static final XClassName PRODUCTION =
      XClassName.Companion.get("dagger.producers", "Production");
  public static final XClassName PRODUCTION_COMPONENT =
      XClassName.Companion.get("dagger.producers", "ProductionComponent");
  public static final XClassName PRODUCTION_COMPONENT_BUILDER =
      XClassName.Companion.get("dagger.producers", "ProductionComponent", "Builder");
  public static final XClassName PRODUCTION_COMPONENT_FACTORY =
      XClassName.Companion.get("dagger.producers", "ProductionComponent", "Factory");
  public static final XClassName PRODUCTION_EXECTUTOR_MODULE =
      XClassName.Companion.get("dagger.producers.internal", "ProductionExecutorModule");
  public static final XClassName PRODUCTION_IMPLEMENTATION =
      XClassName.Companion.get("dagger.producers.internal", "ProductionImplementation");
  public static final XClassName PRODUCTION_SUBCOMPONENT =
      XClassName.Companion.get("dagger.producers", "ProductionSubcomponent");
  public static final XClassName PRODUCTION_SUBCOMPONENT_BUILDER =
      XClassName.Companion.get("dagger.producers", "ProductionSubcomponent", "Builder");
  public static final XClassName PRODUCTION_SUBCOMPONENT_FACTORY =
      XClassName.Companion.get("dagger.producers", "ProductionSubcomponent", "Factory");
  public static final XClassName PRODUCER_TOKEN =
      XClassName.Companion.get("dagger.producers.monitoring", "ProducerToken");
  public static final XClassName PRODUCTION_COMPONENT_MONITOR =
      XClassName.Companion.get("dagger.producers.monitoring", "ProductionComponentMonitor");
  public static final XClassName PRODUCTION_COMPONENT_MONITOR_FACTORY =
      XClassName.Companion.get(
          "dagger.producers.monitoring", "ProductionComponentMonitor", "Factory");
  public static final XClassName SET_OF_PRODUCED_PRODUCER =
      XClassName.Companion.get("dagger.producers.internal", "SetOfProducedProducer");
  public static final XClassName SET_PRODUCER =
      XClassName.Companion.get("dagger.producers.internal", "SetProducer");
  public static final XClassName PRODUCTION_SCOPE =
      XClassName.Companion.get("dagger.producers", "ProductionScope");

  // Other classnames
  public static final XClassName EXECUTOR =
      XClassName.Companion.get("java.util.concurrent", "Executor");
  public static final XClassName ERROR = XClassName.Companion.get("java.lang", "Error");
  public static final XClassName EXCEPTION = XClassName.Companion.get("java.lang", "Exception");
  public static final XClassName RUNTIME_EXCEPTION =
      XClassName.Companion.get("java.lang", "RuntimeException");
  public static final XClassName STRING = XClassName.Companion.get("java.lang", "String");

  public static final XClassName MAP = XClassName.Companion.get("java.util", "Map");
  public static final XClassName KOTLIN_METADATA = XClassName.Companion.get("kotlin", "Metadata");
  public static final XClassName IMMUTABLE_MAP =
      XClassName.Companion.get("com.google.common.collect", "ImmutableMap");
  public static final XClassName SINGLETON =
      XClassName.Companion.get("jakarta.inject", "Singleton");
  public static final XClassName SINGLETON_JAVAX =
      XClassName.Companion.get("javax.inject", "Singleton");
  public static final XClassName SCOPE = XClassName.Companion.get("jakarta.inject", "Scope");
  public static final XClassName SCOPE_JAVAX = XClassName.Companion.get("javax.inject", "Scope");
  public static final XClassName INJECT = XClassName.Companion.get("jakarta.inject", "Inject");
  public static final XClassName INJECT_JAVAX = XClassName.Companion.get("javax.inject", "Inject");
  public static final XClassName QUALIFIER =
      XClassName.Companion.get("jakarta.inject", "Qualifier");
  public static final XClassName QUALIFIER_JAVAX =
      XClassName.Companion.get("javax.inject", "Qualifier");
  public static final XClassName COLLECTION = XClassName.Companion.get("java.util", "Collection");
  public static final XClassName LIST = XClassName.Companion.get("java.util", "List");
  public static final XClassName SET = XClassName.Companion.get("java.util", "Set");
  public static final XClassName IMMUTABLE_SET =
      XClassName.Companion.get("com.google.common.collect", "ImmutableSet");
  public static final XClassName FUTURES =
      XClassName.Companion.get("com.google.common.util.concurrent", "Futures");
  public static final XClassName LISTENABLE_FUTURE =
      XClassName.Companion.get("com.google.common.util.concurrent", "ListenableFuture");
  public static final XClassName FLUENT_FUTURE =
      XClassName.Companion.get("com.google.common.util.concurrent", "FluentFuture");
  public static final XClassName GUAVA_OPTIONAL =
      XClassName.Companion.get("com.google.common.base", "Optional");
  public static final XClassName JDK_OPTIONAL = XClassName.Companion.get("java.util", "Optional");
  public static final XClassName OVERRIDE = XClassName.Companion.get("java.lang", "Override");
  public static final XClassName JVM_STATIC = XClassName.Companion.get("kotlin.jvm", "JvmStatic");
  public static final XClassName CLASS = XClassName.Companion.get("java.lang", "Class");
  public static final XClassName KCLASS = XClassName.Companion.get("kotlin.reflect", "KClass");

  public static XTypeName abstractProducerOf(XTypeName typeName) {
    return ABSTRACT_PRODUCER.parametrizedBy(typeName);
  }

  public static XTypeName factoryOf(XTypeName factoryType) {
    return FACTORY.parametrizedBy(factoryType);
  }

  public static XTypeName lazyOf(XTypeName typeName) {
    return LAZY.parametrizedBy(typeName);
  }

  public static XTypeName listOf(XTypeName typeName) {
    return LIST.parametrizedBy(typeName);
  }

  public static XTypeName listenableFutureOf(XTypeName typeName) {
    return LISTENABLE_FUTURE.parametrizedBy(typeName);
  }

  public static XTypeName membersInjectorOf(XTypeName membersInjectorType) {
    return MEMBERS_INJECTOR.parametrizedBy(membersInjectorType);
  }

  public static XTypeName producedOf(XTypeName typeName) {
    return PRODUCED.parametrizedBy(typeName);
  }

  public static XTypeName producerOf(XTypeName typeName) {
    return PRODUCER.parametrizedBy(typeName);
  }

  public static XTypeName dependencyMethodProducerOf(XTypeName typeName) {
    return DEPENDENCY_METHOD_PRODUCER.parametrizedBy(typeName);
  }

  public static XTypeName providerOf(XTypeName typeName) {
    return PROVIDER.parametrizedBy(typeName);
  }

  public static XTypeName daggerProviderOf(XTypeName typeName) {
    return DAGGER_PROVIDER.parametrizedBy(typeName);
  }

  public static XTypeName setOf(XTypeName elementType) {
    return SET.parametrizedBy(elementType);
  }

  private static final ImmutableSet<XClassName> FUTURE_TYPES =
      ImmutableSet.of(LISTENABLE_FUTURE, FLUENT_FUTURE);

  public static boolean isFutureType(XType type) {
    return isFutureType(type.asTypeName());
  }

  public static boolean isFutureType(XTypeName typeName) {
    return FUTURE_TYPES.contains(typeName.getRawTypeName());
  }

  public static String simpleName(XClassName className) {
    return getLast(className.getSimpleNames());
  }

  private XTypeNames() {}
}
