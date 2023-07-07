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

package dagger.internal.codegen.validation;

import static dagger.internal.codegen.extension.DaggerStreams.toImmutableSet;
import static javax.tools.Diagnostic.Kind.ERROR;

import androidx.room.compiler.processing.XFiler;
import androidx.room.compiler.processing.compat.XConverters;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import dagger.internal.codegen.compileroption.ProcessingOptions;
import dagger.internal.codegen.langmodel.DaggerElements;
import dagger.internal.codegen.langmodel.DaggerTypes;
import dagger.internal.codegen.validation.DiagnosticReporterFactory.DiagnosticReporterImpl;
import dagger.model.BindingGraph;
import dagger.spi.BindingGraphPlugin;
import dagger.spi.DiagnosticReporter;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;

/** Initializes {@link BindingGraphPlugin}s. */
public final class ExternalBindingGraphPlugins {
  private final ImmutableSet<BindingGraphPlugin> plugins;
  private final DiagnosticReporterFactory diagnosticReporterFactory;
  private final XFiler filer;
  private final DaggerTypes types;
  private final DaggerElements elements;
  private final Map<String, String> processingOptions;

  @Inject
  ExternalBindingGraphPlugins(
      ImmutableSet<BindingGraphPlugin> plugins,
      DiagnosticReporterFactory diagnosticReporterFactory,
      XFiler filer,
      DaggerTypes types,
      DaggerElements elements,
      @ProcessingOptions Map<String, String> processingOptions) {
    this.plugins = plugins;
    this.diagnosticReporterFactory = diagnosticReporterFactory;
    this.filer = filer;
    this.types = types;
    this.elements = elements;
    this.processingOptions = processingOptions;
  }

  /** Returns {@link BindingGraphPlugin#supportedOptions()} from all the plugins. */
  public ImmutableSet<String> allSupportedOptions() {
    return plugins.stream()
        .flatMap(plugin -> plugin.supportedOptions().stream())
        .collect(toImmutableSet());
  }

  /** Initializes the plugins. */
  // TODO(ronshapiro): Should we validate the uniqueness of plugin names?
  public void initializePlugins() {
    plugins.forEach(this::initializePlugin);
  }

  private void initializePlugin(BindingGraphPlugin plugin) {
    plugin.initFiler(XConverters.toJavac(filer));
    plugin.initTypes(types);
    plugin.initElements(elements);
    Set<String> supportedOptions = plugin.supportedOptions();
    if (!supportedOptions.isEmpty()) {
      plugin.initOptions(Maps.filterKeys(processingOptions, supportedOptions::contains));
    }
  }

  /** Returns {@code false} if any of the plugins reported an error. */
  boolean visit(dagger.spi.model.BindingGraph spiGraph) {
    BindingGraph graph = ExternalBindingGraphConverter.fromSpiModel(spiGraph);
    boolean isClean = true;
    for (BindingGraphPlugin plugin : plugins) {
      DiagnosticReporterImpl spiReporter =
          diagnosticReporterFactory.reporter(
              spiGraph, plugin.pluginName(), /* reportErrorsAsWarnings= */ false);
      DiagnosticReporter reporter = ExternalBindingGraphConverter.fromSpiModel(spiReporter);
      plugin.visitGraph(graph, reporter);
      if (spiReporter.reportedDiagnosticKinds().contains(ERROR)) {
        isClean = false;
      }
    }
    return isClean;
  }
}
